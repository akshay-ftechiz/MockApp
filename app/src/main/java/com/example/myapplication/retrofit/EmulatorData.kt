package com.example.myapplication.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmulatorData(
    @SerializedName("emulatorName")
    @Expose
    val emulatorName:String,

    @SerializedName("emulatorIp")
    @Expose
    val emulatorId: String,

    @SerializedName("timestamp")
    @Expose
    val time:String
    )
