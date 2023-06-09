package com.example.myapplication.helper

import android.content.Context

const val APPLICATION_ID = "com.videomaster.editor"
const val DATA_POSTED = "data posted"


object SharedPreferenceHelper
{

    fun setSharedPreference(ctx: Context, Key: String?, Value: String?) {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(Key, Value)
        editor.apply()
    }


    fun setBooleanSharedPreference(ctx: Context, Key: String?, Value: Boolean) {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Key, Value)
        editor.apply()
    }
    fun getBooleanSharedPreference(ctx: Context, Key: String?, defaultValue: Boolean): Boolean {
        val pref = ctx.getSharedPreferences(APPLICATION_ID, Context.MODE_PRIVATE)
        return if (pref.contains(Key)) {
            pref.getBoolean(Key, defaultValue)
        } else defaultValue
    }


}
