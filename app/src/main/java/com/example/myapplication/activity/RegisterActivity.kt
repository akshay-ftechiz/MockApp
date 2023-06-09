package com.example.myapplication.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.helper.DATA_POSTED
import com.example.myapplication.helper.HelperUtils
import com.example.myapplication.helper.HelperUtils.AUTHORIZATION
import com.example.myapplication.helper.HelperUtils.BASE_URL
import com.example.myapplication.helper.HelperUtils.CONTENT_TYPE
import com.example.myapplication.helper.HelperUtils.READ_PHONE_REQUEST_CODE
import com.example.myapplication.helper.HelperUtils.hasPermission
import com.example.myapplication.helper.SharedPreferenceHelper
import com.example.myapplication.retrofit.EmulatorData
import com.example.myapplication.retrofit.RetrofitBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    private val binding:ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(!hasPermission(Manifest.permission.READ_PHONE_STATE,this))
        {
            requestAndCheckPermisison()

        }
        else
        {
            populateAndPostData()
        }

        binding.enterButton.setOnClickListener {

            if(hasPermission(Manifest.permission.READ_PHONE_STATE,this)) {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                allowPermission()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == READ_PHONE_REQUEST_CODE)
        {
            if(hasPermission(Manifest.permission.READ_PHONE_STATE,this))
            {
                populateAndPostData()
            }
            else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun requestAndCheckPermisison() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                READ_PHONE_REQUEST_CODE
            )

        }
        else
        {
            allowPermission()
        }

    }
    private fun allowPermission(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", applicationContext.packageName, null)
        intent.data = uri
        startActivityForResult(intent, READ_PHONE_REQUEST_CODE)


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ( requestCode == READ_PHONE_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            populateAndPostData()
        }
        else {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("HardwareIds")
    fun populateAndPostData()
    {
        if(!SharedPreferenceHelper.getBooleanSharedPreference(this, DATA_POSTED,false)) {
            val deviceId = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            val emulatorName = Build.MANUFACTURER + Build.MODEL
            val installTime = System.currentTimeMillis().toString()
            val emulatorData = EmulatorData(deviceId, emulatorName, installTime)
            postData(emulatorData)
        }
    }

    private fun postData(emulatorData: EmulatorData) {
        val apiClient = RetrofitBuilder.getApi(this, BASE_URL)

        apiClient.postData(AUTHORIZATION, CONTENT_TYPE,emulatorData)?.enqueue(object : Callback<EmulatorData?> {

            override fun onResponse(call: Call<EmulatorData?>, response: Response<EmulatorData?>) {
                SharedPreferenceHelper.setBooleanSharedPreference(
                    this@RegisterActivity,
                    DATA_POSTED,
                    true
                )
                Toast.makeText(this@RegisterActivity, "Success", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<EmulatorData?>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "fail", Toast.LENGTH_SHORT).show()

            }
        })


    }


}