package com.example.oicys

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.oicys.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivitySignUpBinding

    //ActionBar
    //private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Actionbar,  //enable back button
        /*
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
         */

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click, begin signup
        binding.signupBtn.setOnClickListener {
            //validate data
            validateData()
        }

    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val regex = "sookmyung.ac.kr".toRegex()
        val match = regex.find(email)

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.emailEt.error = "Invalid email format"
        }
        else if(match == null){
            binding.emailEt.error = "Enter SOOKMYUNG email"
        }
        else if (TextUtils.isEmpty(password)){
            //password isn't entered
            binding.passwordEt.error = "Please enter password"
        }
        else if (password.length <6){
            //password length is less than 6
            binding.passwordEt.error = "Password must at least 6 chracters long"
        }
        else{
            //data is valid, continue signup
            firebaseSignUp()
        }
    }

    /*private fun verifyEmail() {

        email = binding.emailEt.text.toString().trim()
        val firebaseUser = firebaseAuth.currentUser
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.emailEt.error = "Invalid email format"
        }
        else{
            firebaseUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        binding.signUpBtn.isEnabled = true
                    }
                }
        }
    }*/

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    val user:FirebaseUser? = firebaseAuth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                }
                else{
                    Toast.makeText(
                        baseContext, "Sign Up failed. Try again after some time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }
}