package com.example.cozyfocus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val namaText = view.findViewById<EditText>(R.id.nama_input_field)
        val emailText = view.findViewById<TextView>(R.id.email_input_field)
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)

        emailText.isEnabled = false

        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    namaText.setText(document.getString("nama"))
                    emailText.text = auth.currentUser?.email ?: "N/A"
                }
            }
        }

        confirmButton.setOnClickListener {
            val newNama = namaText.text.toString()
            val newEmail = emailText.text.toString()

            if (newNama.isNotEmpty() && newEmail.isNotEmpty()) {
                val updatedUser = hashMapOf(
                    "nama" to newNama,
                )
                db.collection("Users").document(userId!!).update(updatedUser as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profil sukses diperbaharui!", Toast.LENGTH_SHORT).show()
                    namaText.isEnabled = false
                }
            } else {
                Toast.makeText(requireContext(), "Setiap data wajib diisi", Toast.LENGTH_SHORT).show()
            }
        }

        val changePasswordButton = view.findViewById<TextView>(R.id.change_password_button)
        changePasswordButton.setOnClickListener {
            val EditPasswordFragment = EditPasswordFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, EditPasswordFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}