package com.aura.ui.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    val retrofit = Retrofit.Builder()
      //  .baseUrl("http://127.0.0.1:8080/")
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
        .build()

    val apiService: UserApiService = retrofit.create(UserApiService::class.java)
}
