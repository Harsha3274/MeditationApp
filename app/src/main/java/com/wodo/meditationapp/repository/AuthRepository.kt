package com.wodo.meditationapp.repository

import com.wodo.meditationapp.data.LoginBody
import com.wodo.meditationapp.data.RegisterBody
import com.wodo.meditationapp.data.UniqueEmailValidationResponse
import com.wodo.meditationapp.data.ValidateEmailBody
import com.wodo.meditationapp.utils.APIConsumer
import com.wodo.meditationapp.utils.RequestStatus
import com.wodo.meditationapp.utils.SimplifiedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val consumer: APIConsumer) {
    fun validateEmailAddress(body: ValidateEmailBody) = flow{
        emit(RequestStatus.Waiting)
        val response=consumer.validateEmailAddress(body)
        if(response.isSuccessful){
            emit((RequestStatus.Success(response.body()!!)))
        }else{
            emit(RequestStatus.Error(SimplifiedMessage.get(response.errorBody()!!.byteStream().reader().readText())))
        }
    }
    fun registerUser(body: RegisterBody)= flow {
        emit(RequestStatus.Waiting)
        val response=consumer.registerUser(body)
        if(response.isSuccessful){
            emit((RequestStatus.Success(response.body()!!)))
        }else{
            emit(RequestStatus.Error(SimplifiedMessage.get(response.errorBody()!!.byteStream().reader().readText())))
        }
    }

    fun loginUser(body: LoginBody)= flow {
        emit(RequestStatus.Waiting)
        val response=consumer.loginUser(body)
        if(response.isSuccessful){
            emit((RequestStatus.Success(response.body()!!)))
        }else{
            emit(RequestStatus.Error(SimplifiedMessage.get(response.errorBody()!!.byteStream().reader().readText())))
        }
    }
}