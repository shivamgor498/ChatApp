package com.example.shivam.placement

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register_button.setOnClickListener {
            val email = email_register.text.toString()
            val password = password_register.text.toString()
            Log.d("MainActivity","email: $email")
            Log.d("MainActivity","password: $password")
            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this,"email and password should not be empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if(!it.isSuccessful)return@addOnCompleteListener
                // else successful
                Toast.makeText(this,"User Created with uid : ${it.result!!.user.uid}",Toast.LENGTH_LONG).show()
                Log.d("MainActivity","User Created with uid : ${it.result!!.user.uid}")
            }.addOnFailureListener {
                Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
            }
            uploadPhoto()
        }
        already_registered.setOnClickListener {
            Log.d("MainActivity","User already registered")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        select_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            startActivityForResult(intent,0)
        }
    }
    var selectedPhotouri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode==Activity.RESULT_OK && data!=null)
        {
            Toast.makeText(this,"Photo was selected",Toast.LENGTH_SHORT).show()
            selectedPhotouri= data.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotouri)
            select_imageview.setImageBitmap(bitmap)
            select_photo.alpha = 0f
            /*var bitmapDrawable = BitmapDrawable(bitmap)
            select_photo.setBackgroundDrawable(bitmapDrawable)
            select_photo.setText("")*/
        }
    }
    private fun uploadPhoto(){
        if(selectedPhotouri==null)
            return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotouri!!).addOnSuccessListener {
            Toast.makeText(this,"Successfully uploaded image : ${it.metadata?.path}",Toast.LENGTH_SHORT).show()
        ref.downloadUrl.addOnSuccessListener {
            Toast.makeText(this,"Url is : $it.",Toast.LENGTH_SHORT).show()
            saveUserToFirebase(it.toString())
        }
        }
    }
    private fun saveUserToFirebase(profileUrl: String) {
        val uid =  FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_register.text.toString(),profileUrl)
        ref.setValue(user).addOnSuccessListener {
            Toast.makeText(this,"User data uploaded",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,Dashboard::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
        }
    }
}
@Parcelize
class User(val uid: String,val username: String, val profileUrl : String) : Parcelable{
    constructor() : this("","","")
}