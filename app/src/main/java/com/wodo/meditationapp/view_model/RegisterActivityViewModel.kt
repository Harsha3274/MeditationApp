package com.wodo.meditationapp.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodo.meditationapp.data.RegisterBody
import com.wodo.meditationapp.data.User
import com.wodo.meditationapp.data.ValidateEmailBody
import com.wodo.meditationapp.repository.AuthRepository
import com.wodo.meditationapp.utils.AuthToken
import com.wodo.meditationapp.utils.RequestStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.security.AuthProvider

class RegisterActivityViewModel(private val authRepository: AuthRepository, private val application: Application): ViewModel() {
    private var isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private var errorMessage:MutableLiveData<HashMap<String,String>> = MutableLiveData()
    private var isUniqueEmail: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    private var user: MutableLiveData<User> = MutableLiveData()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String,String>> = errorMessage
    fun getIsUniqueEmail(): LiveData<Boolean> = isUniqueEmail
    fun getUser(): LiveData<User> = user
    fun validateEmailAddress(body: ValidateEmailBody){
        viewModelScope.launch {
            authRepository.validateEmailAddress(body).collect{
                when(it){
                    is RequestStatus.Waiting->{
                        isLoading.value=true
                    }
                    is RequestStatus.Success->{
                        isLoading.value=false
                        isUniqueEmail.value=it.data.isUnique
                    }
                    is RequestStatus.Error->{
                        isLoading.value=false
                        errorMessage.value=it.message
                    }
                }
            }
        }
    }
    fun registerUser(body: RegisterBody){
        viewModelScope.launch {
            authRepository.registerUser(body).collect{
                when(it){
                    is RequestStatus.Waiting->{
                        isLoading.value=true
                    }
                    is RequestStatus.Success->{
                        isLoading.value=false
                        user.value=it.data.user
                        AuthToken.getInstance(application.baseContext).token=it.data.token
                    }
                    is RequestStatus.Error->{
                        isLoading.value=false
                        errorMessage.value=it.message
                    }
                }
            }
        }
    }
}