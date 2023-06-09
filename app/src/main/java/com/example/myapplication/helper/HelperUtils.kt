package com.example.myapplication.helper

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader

object HelperUtils {
    const val READ_PHONE_REQUEST_CODE = 109
    val AUTHORIZATION: String = "Baeldung"
    const val CONTENT_TYPE = "application/json"
    const val BASE_URL = "http://192.168.1.119:8080/"


    fun isAllowMockLocation(context: Context): Boolean {
        var canMockPosition = false
        try {
            val locationManager =
                context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager //获得LocationManager引用
            val providerStr = LocationManager.GPS_PROVIDER
            val provider = locationManager.getProvider(providerStr)
            try {
                locationManager.removeTestProvider(providerStr)
                Log.d("PERMISSION", "try to move test provider")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("PERMISSION", "try to move test provider")
            }
            if (provider != null) {
                try {
                    locationManager.addTestProvider(
                        provider.name,
                        provider.requiresNetwork(),
                        provider.requiresSatellite(),
                        provider.requiresCell(),
                        provider.hasMonetaryCost(),
                        provider.supportsAltitude(),
                        provider.supportsSpeed(),
                        provider.supportsBearing(),
                        provider.powerRequirement,
                        provider.accuracy
                    )
                    canMockPosition = true
                } catch (e: java.lang.Exception) {
                    Log.e("FUCK", "add origin gps test provider error")
                    canMockPosition = false
                    e.printStackTrace()
                }
            } else {
                try {
                    locationManager.addTestProvider(
                        providerStr,
                        true,
                        true,
                        false,
                        false,
                        true,
                        true,
                        true,
                        Criteria.POWER_HIGH,
                        Criteria.ACCURACY_FINE
                    )
                    canMockPosition = true
                } catch (e: java.lang.Exception) {
                    Log.e("FUCK", "add gps test provider error")
                    canMockPosition = false
                    e.printStackTrace()
                }
            }

            // 模拟位置可用
            if (canMockPosition) {
                locationManager.setTestProviderEnabled(providerStr, true)
                locationManager.setTestProviderStatus(
                    providerStr,
                    LocationProvider.AVAILABLE,
                    null,
                    System.currentTimeMillis()
                )
                //remove test provider
                locationManager.setTestProviderEnabled(providerStr, false)
                locationManager.removeTestProvider(providerStr)
            }
        } catch (e: SecurityException) {
            canMockPosition = false
            e.printStackTrace()
        }
        return canMockPosition
    }

     fun hasPermission(permission: String, context: Context):Boolean{
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
               return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
         return false
    }
     fun isGpsOpened(context: Context): Boolean {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    fun getEmulatorName(context: Context?): String? {
        if (isEmulator()) {
            try {
                val process = Runtime.getRuntime().exec("adb devices")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    if (line.contains("emulator-")) {
                        val parts = line.split("\t".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (parts.size > 1) {
                            return parts[1].trim { it <= ' ' }
                        }
                    }
                }
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun isEmulator(): Boolean {
        return (Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.DEVICE.contains("generic")
                || Build.MANUFACTURER.contains("Genymotion")) || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
            "generic"
        ) || "google_sdk" == Build.PRODUCT
    }

}