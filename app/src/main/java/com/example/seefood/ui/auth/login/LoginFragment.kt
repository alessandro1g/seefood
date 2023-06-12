package com.example.seefood.ui.auth.login

import android.content.Intent
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
import com.example.seefood.MainActivity
import com.example.seefood.R
import com.example.seefood.ui.auth.register.RegisterFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
        private val TAG = "LoginF"
    }


    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var emailTxt: EditText
    private lateinit var passTxt: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        loginBtn = root.findViewById(R.id.login_btn)
        emailTxt = root.findViewById(R.id.inputLoginEmail)
        passTxt = root.findViewById(R.id.inputLoginPassword)
        signUpBtn = root.findViewById(R.id.loginToSignUpBtn)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        // TODO: Use the ViewModel
        auth = Firebase.auth
        if (loginViewModel.email.value != null)
            emailTxt.text = SpannableStringBuilder(loginViewModel.email.value)
        if(loginViewModel.password.value != null)
            passTxt.text = SpannableStringBuilder(loginViewModel.password.value)

        emailTxt.addTextChangedListener {
            Log.i(RegisterFragment.TAG, "Changing email to $it")
            loginViewModel.updateEmail(it.toString())
        }

        passTxt.addTextChangedListener {
            Log.i(RegisterFragment.TAG, "Changing password to $it")
            loginViewModel.updatePassword(it.toString())
        }
        loginBtn.setOnClickListener {
            if(isValidInput()) login(emailTxt.text.toString(), passTxt.text.toString())
        }
        signUpBtn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, RegisterFragment.newInstance())
                ?.commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
    private fun isValidInput():Boolean {
        if(emailTxt.text.isBlank() || passTxt.text.isBlank()){
            Toast.makeText(context, "Empty Fields!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}