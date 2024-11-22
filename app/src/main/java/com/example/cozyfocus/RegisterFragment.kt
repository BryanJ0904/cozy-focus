package com.example.cozyfocus

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val namaInput = view.findViewById<TextInputEditText>(R.id.nama_input_field)
        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input_field)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input_field)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.confirm_password_input_field)

        val loginButton = view.findViewById<TextView>(R.id.login_button)
        loginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.display, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        val registerButton = view.findViewById<Button>(R.id.submit_button)
        registerButton.setOnClickListener {
            val nama = namaInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || nama.isEmpty()) {
                Toast.makeText(requireContext(), "Setiap data wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                if (password != confirmPassword){
                    Toast.makeText(requireContext(), "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                } else{
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                saveUserData(nama)
                                val profileFragment = ProfileFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.display, profileFragment)
                                    .addToBackStack(null)
                                    .commit()
                                Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                                val userId = auth.currentUser?.uid
                                val db = FirebaseFirestore.getInstance()
                                val userData = hashMapOf(
                                    "nama" to nama
                                )
                                db.collection("Users").document(userId!!).set(userData)
                            } else {
                                Toast.makeText(requireContext(), "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserData(nama: String) {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", 0)
        with(sharedPref.edit()) {
            putString("nama", nama)
            apply()
        }
    }
}