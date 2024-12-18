package com.example.cozyfocus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private val CAMERA_REQUEST_CODE = 100
    private lateinit var profilePicture: ImageView
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileTitle = view.findViewById<TextView>(R.id.profile_title)
        val editButton = view.findViewById<TextView>(R.id.edit_button)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        profilePicture = view.findViewById<ImageView>(R.id.profile_photo)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Fetch User data
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    profileTitle.text = "Hello " + document.getString("nama")
                    // Load profile picture if exists
                    val encodedPhoto = document.getString("photo")
                    if (encodedPhoto != null) {
                        val decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        profilePicture.setImageBitmap(bitmap)
                    } else {
                        Toast.makeText(requireContext(), "Foto profil tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal mendapatkan data pengguna!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }

        editButton.setOnClickListener {
            // Handle Camera Permission and open camera
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            } else {
                openCamera()
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), GuestActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap
            saveProfilePicture(photo)
        }
    }

    private fun saveProfilePicture(photo: Bitmap) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Convert the photo to a circular image
            val circularPhoto = getCircularBitmap(photo)
            profilePicture.setImageBitmap(circularPhoto)

            // Convert the photo to a Base64 string
            val byteArrayOutputStream = ByteArrayOutputStream()
            circularPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedPhoto = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Create a map to store the photo in Firestore
            val profilePhoto = hashMapOf("photo" to encodedPhoto)

            // Update user's profile photo in Firestore
            db.collection("Users").document(userId)
                .set(profilePhoto, SetOptions.merge()) // Merge to retain other fields
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Foto profil berhasil diperbaharui!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Foto profil gagal diperbaharui!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Gagal mendapatkan User ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCircularBitmap(photo: Bitmap): Bitmap {
        val width = photo.width
        val height = photo.height
        val minEdge = minOf(width, height)
        val radius = minEdge / 2

        val output = Bitmap.createBitmap(minEdge, minEdge, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val rect = Rect(0, 0, minEdge, minEdge)
        val rectF = RectF(rect)

        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.BLACK
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius.toFloat(), paint)

        // Set mode to source in to only draw what's inside the circle
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(photo, null, rectF, paint)

        return output
    }
}
