package com.example.palette.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.palette.data.auth.RegisterRequest

class RegisterViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _birthdate = MutableLiveData<String>()
    val birthdate: LiveData<String> get() = _birthdate

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setBirthdate(value: String) {
        _birthdate.value = value
    }

    fun setUsername(value: String) {
        _username.value = value
    }

    fun getRegisterRequestData(): LiveData<RegisterRequest> {
        val userData = MutableLiveData<RegisterRequest>()
        val email = _email.value ?: ""
        val password = _password.value ?: ""
        val birthDate = _birthdate.value ?: ""
        val username = _username.value ?: ""

        userData.value = RegisterRequest(email, password, birthDate, username)
        return userData
    }
}