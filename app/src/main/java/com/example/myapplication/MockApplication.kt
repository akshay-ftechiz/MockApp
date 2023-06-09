package com.example.myapplication

import android.app.Application
import android.os.AsyncTask
import com.google.firebase.FirebaseApp

class MockApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        }
        catch (ignored: Exception) {
        }

        try {
            FcmAsyncTask(applicationContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
        catch (exception:Exception)
        {

        }

    }
}