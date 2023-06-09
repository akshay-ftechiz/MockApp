package com.example.myapplication.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @POST("emulators")
     fun postData(@Header("X-API-KEY") authorization: String?,@Header("Content-Type") contentType: String?,@Body body: EmulatorData) : Call<EmulatorData?>?

}