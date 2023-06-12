package com.example.seefood.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    internal val email: LiveData<String>
        get() = _email
    private val _password = MutableLiveData<String>()
    internal val password: LiveData<String>
        get() = _password
    fun updateEmail(txt: String){
        _email.value = txt
    }
    fun updatePassword(txt:String){
        _password.value = txt
    }
}