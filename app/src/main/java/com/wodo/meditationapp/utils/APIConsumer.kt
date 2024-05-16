package com.wodo.meditationapp.utils

import com.wodo.meditationapp.data.RegisterBody
import com.wodo.meditationapp.data.AuthResponse
import com.wodo.meditationapp.data.LoginBody
import com.wodo.meditationapp.data.UniqueEmailValidationResponse
import com.wodo.meditationapp.data.ValidateEmailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {
    @POST("users/validate-unique-email")
   suspend fun validateEmailAddress(@Body body: ValidateEmailBody):Response<UniqueEmailValidationResponse>

   @POST("users/register")
   suspend fun registerUser(@Body body: RegisterBody): Response<AuthResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody): Response<AuthResponse>
}