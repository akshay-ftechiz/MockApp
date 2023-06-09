package com.example.myapplication.retrofit

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private fun retrofit(url: String?):Retrofit  {
        return  Retrofit.Builder()
            .baseUrl(url)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun getApi(context: Context?,url: String):Api{
        return retrofit(url).create(Api::class.java)
    }

}
