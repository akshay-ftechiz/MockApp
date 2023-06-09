package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.myapplication.helper.HelperUtils
import com.example.myapplication.services.MockGpsService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("@message", "recieved" + remoteMessage.data)
        try {
            val map = remoteMessage.data
            val gson = Gson()
            val item: NotificationModel = gson.fromJson(map["data"], NotificationModel::class.java)
            Log.d("@message", item.toString())
            GenericNotificationManager.handleGenericNotification(applicationContext, item)
            with(applicationContext)
            {
                if(HelperUtils.isGpsOpened(this) && HelperUtils.isGpsOpened(this) && HelperUtils.hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,this))
                {
                    val mockLocServiceIntent = Intent(
                        this,
                        MockGpsService::class.java
                    )
                    var latLng ="${item.latitude}&${item.longitude}"

                    mockLocServiceIntent.putExtra("key", latLng)
                    if (Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(mockLocServiceIntent)
                        Log.d("DEBUG", "startForegroundService: MOCK_GPS")
                    } else {
                        startService(mockLocServiceIntent)
                        Log.d("DEBUG", "startService: MOCK_GPS")
                    }

                }
            }
        } catch (e: Exception) {
//                Crashlytics.logException(new Throwable("NOTIFICATION CRASHES", e));
        }
    }
}