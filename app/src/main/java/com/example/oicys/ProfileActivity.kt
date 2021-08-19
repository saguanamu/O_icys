package com.example.oicys

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.oicys.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.jar.Manifest

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        saveBtn.setOnClickListener {
            val uname = nameEt.text.toString()

            val user = SUser(uname)
            if (uid != null) {
                databaseReference.setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //uploadImage()
                    } else {
                        Toast.makeText(this, "Failed to upload proflie", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}