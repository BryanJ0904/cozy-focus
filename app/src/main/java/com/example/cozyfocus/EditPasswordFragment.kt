package com.example.cozyfocus

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [EditPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPasswordFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input_field)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.confirm_password_input_field)

        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Setiap data wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                if (password != confirmPassword) {
                    Toast.makeText(requireContext(), "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                } else {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        currentUser.updatePassword(password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(requireContext(), "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the task stack
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else {
                                    Toast.makeText( requireContext(), "Gagal memperbarui password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "Pengguna tidak terautentikasi!", Toast.LENGTH_SHORT).show()
                    }
                }

                val backButton = view.findViewById<TextView>(R.id.back_button)
                backButton.setOnClickListener {
                    val EditProfileFragment = EditProfileFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, EditProfileFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}