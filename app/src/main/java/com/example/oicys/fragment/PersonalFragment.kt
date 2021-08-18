package com.example.oicys.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.oicys.LoginActivity
import com.example.oicys.R
import com.example.oicys.databinding.FragmentPersonalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //private lateinit var logoutButton: Button
    //private lateinit var logoutButton: Button
    //ViewBinding
    //private lateinit var binding: FragmentPersonalBinding
    //FirebaseAuth
    //private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    // Email
    private lateinit var emailTv: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTv = view.findViewById(R.id.emailTv)
        getUserProfile()
        //logoutButton = view.findViewById(R.id.logoutBtn)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //init firebase auth
        //firebaseAuth = FirebaseAuth.getInstance()
        //checkUser()

        /*
        //handle click, logout
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
            //checkUser()
        }*/

    }

    /*
    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.emailTv.text = email
        }
        else{ //user is null, user is not logged in, goto login activity
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getUserProfile() {
        // [START get_user_profile]
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            //val name = user.displayName
            val email = user.email
            emailTv.setText(user.email.toString()).toString()

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }
    }
}