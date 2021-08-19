package com.example.oicys.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oicys.LoginActivity
import com.example.oicys.ProfileActivity
import com.example.oicys.R
import com.example.oicys.SUser
import com.example.oicys.databinding.FragmentPersonalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_personal.*

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

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    // Email
    private lateinit var emailTv: TextView
    //private lateinit var user_name: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTv = view.findViewById(R.id.emailTv)
        //user_name = view.findViewById(R.id.user_name)
        getUserProfile()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_personal, container, false)

        val bt = v.findViewById<Button>(R.id.logout_bt)
        bt.setOnClickListener {
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val bt2 = v.findViewById<Button>(R.id.setting_3)
        bt2.setOnClickListener {
            FirebaseAuth.getInstance().currentUser!!.delete()
            FirebaseAuth.getInstance().signOut()
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        /*databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val uname = user_name.text.toString()
        val user = SUser(uname)
        databaseReference.setValue(user)*/


        /*val bt1 = v.findViewById<Button>(R.id.setting_2)
        bt1.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ProfileActivity::class.java)
                startActivity(intent)
            }
        }*/

        return v
    }

    private fun deleted() {
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
        val user = Firebase.auth.currentUser
        // [START get_user_profile]
        user?.let {
            // Name, email address, and profile photo Url
            //val name = user.displayName
            val email = user.email
            emailTv.setText(user.email.toString()).toString()

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
            //uid.toString()
        }

        /*database = FirebaseDatabase.getInstance().getReference("Users")
        val uname = database.child("uname").get()
        user_name.setText(uname.toString()).toString()*/
    }
}