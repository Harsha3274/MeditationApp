package com.wodo.meditationapp.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodo.meditationapp.repository.AuthRepository
import java.security.InvalidParameterException

class LoginActivityViewModelFactory(
    private val authRepository: AuthRepository,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    fun <T:ViewModel?> create(modelClass: Class<T>):T{
        if(modelClass.isAssignableFrom(LoginActivityViewModelFactory::class.java)){
            return LoginActivityViewModelFactory(authRepository,application) as T
        }

        throw InvalidParameterException("Unable to construct RegisterActivityViewModel")
    }
}