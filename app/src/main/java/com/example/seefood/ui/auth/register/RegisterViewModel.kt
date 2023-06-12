package com.example.seefood.ui.auth.register

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel(), DefaultLifecycleObserver {
    // TODO: Implement the ViewModel
    private val _user = MutableLiveData<String>()
    internal val user: LiveData<String>
        get() = _user
    private val _email = MutableLiveData<String>()
    internal val email: LiveData<String>
        get() = _email
    private val _password = MutableLiveData<String>()
    internal val password: LiveData<String>
        get() = _password
    private val _confirmPassword = MutableLiveData<String>()
    internal val confirmPassword: LiveData<String>
        get() = _password
    fun updateUser(txt: String){
        _user.value = txt
    }
    fun updateEmail(txt: String){
        _email.value = txt
    }
    fun updatePassword(txt:String){
        _password.value = txt
    }
    fun updateConfirmPassword(txt: String){
        _confirmPassword.value= txt
    }

}