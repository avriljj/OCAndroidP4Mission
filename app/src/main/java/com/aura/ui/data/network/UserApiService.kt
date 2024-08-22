package com.aura.ui.data.network
import LoginRequest
import LoginResponse
import com.aura.ui.data.Account
import com.aura.ui.data.transfer.Transfer
import com.aura.ui.data.transfer.TransferResult


import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST
import retrofit2.http.Path


interface UserApiService {

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/accounts/{id}")
    suspend fun getAccounts(@Path("id") userId: String): Response<List<Account>>


    @POST("/transfer")
    suspend fun transfer(@Body request: Transfer): Response<TransferResult>



}



