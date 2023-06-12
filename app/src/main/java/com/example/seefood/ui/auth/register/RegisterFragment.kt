package com.example.seefood.ui.auth.register

import android.app.AlertDialog

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.seefood.R
import com.example.seefood.utils.Food
import com.example.seefood.utils.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import android.content.DialogInterface
import android.content.Intent
import com.example.seefood.MainActivity
import com.google.firebase.auth.ktx.userProfileChangeRequest


class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
        val TAG = "RF"
    }

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var regBtn: Button
    private lateinit var userTxt: EditText
    private lateinit var emailTxt: EditText
    private lateinit var passTxt:EditText
    private lateinit var confirmPassTxt:EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register, container, false)
        regBtn = root.findViewById(R.id.register_sign_up_button)
        emailTxt = root.findViewById(R.id.inputRegisterEmail)
        userTxt = root.findViewById(R.id.inputRegisterUsername)
        passTxt = root.findViewById(R.id.inputRegisterPassword)
        confirmPassTxt = root.findViewById(R.id.inputRegisterConfirmPassword)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        auth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().reference.child("Users")
        //registerUser("test@email.com", "testing12345")
        //Add view model to lifecycle observer
        if (registerViewModel.user.value != null)
            userTxt.text = SpannableStringBuilder(registerViewModel.user.value)
        if (registerViewModel.email.value != null)
            emailTxt.text = SpannableStringBuilder(registerViewModel.email.value)
        if(registerViewModel.password.value != null)
            passTxt.text = SpannableStringBuilder(registerViewModel.password.value)
        if(registerViewModel.confirmPassword.value != null)
            confirmPassTxt.text = SpannableStringBuilder(registerViewModel.confirmPassword.value!!)

        userTxt.addTextChangedListener {
            Log.i(TAG, "Changing user to $it")
            registerViewModel.updateUser(it.toString())
        }
        emailTxt.addTextChangedListener {
            Log.i(TAG, "Changing email to $it")
            registerViewModel.updateEmail(it.toString())
        }
        passTxt.addTextChangedListener {
            Log.i(TAG, "Changing password to $it")
            registerViewModel.updatePassword(it.toString())
        }
        confirmPassTxt.addTextChangedListener {
            Log.i(TAG, "Changing confirmedPass to $it")
            registerViewModel.updateConfirmPassword(it.toString())
        }
        regBtn.setOnClickListener {

            if(isValidInput()){
                val snackbar = Snackbar.make(it, "Registering new user!", Snackbar.LENGTH_SHORT).show()
                registerUser(registerViewModel.email.value!!, registerViewModel.password.value!!)
            }
        }
        this.requireActivity().lifecycle.addObserver(registerViewModel)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
    private fun isValidInput():Boolean{
        if(passTxt.text.toString() != confirmPassTxt.text.toString()){
            Toast.makeText(this.context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(userTxt.text.isBlank() || emailTxt.text.isBlank() || passTxt.text.isBlank() || confirmPassTxt.text.isBlank()){
            Toast.makeText(this.context, "Empty Fields!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun registrationDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this.context)
        alertDialogBuilder.setTitle("Registration Successful")
        .setMessage("Let's visit your dashboard!").setCancelable(false)
        .setPositiveButton("OK"
        ) { _, _ ->
            val intent = Intent(this.context, MainActivity::class.java)
            startActivity(intent)
        }
        .create()
        .show()
    }
    private fun registerUser(email: String, password:String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmail:success")
                    val emptyLst = ArrayList<Food>()
                    val newData = User(userTxt.text.toString(),emptyLst)
                    val user = auth.currentUser!!

                    user.updateProfile(userProfileChangeRequest {
                        displayName = userTxt.text.toString()
                    }
                    )
                    Log.i(TAG, "displayname: ${auth.currentUser?.displayName}")
                    dbRef.child(auth.uid!!).setValue(newData)
                    registrationDialog()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)

                }
            }
        // [END create_user_with_email]
    }
}