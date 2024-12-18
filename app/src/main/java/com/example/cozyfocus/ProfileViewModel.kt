import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    // LiveData to store profile data
    val profileName: MutableLiveData<String> = MutableLiveData()
    val profilePhoto: MutableLiveData<Bitmap> = MutableLiveData()

    // Function to load profile data
    fun loadProfile(userId: String) {
        val db = FirebaseFirestore.getInstance()

        // Fetch the profile data from Firestore
        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    profileName.value = document.getString("nama")

                    val encodedPhoto = document.getString("photo")
                    if (encodedPhoto != null) {
                        val decodedString = Base64.decode(encodedPhoto, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        profilePhoto.value = bitmap
                    }
                }
            }
            .addOnFailureListener {
                // Handle failure (optional)
            }
    }
}
