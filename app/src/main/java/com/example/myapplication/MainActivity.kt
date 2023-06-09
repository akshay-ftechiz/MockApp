package com.example.myapplication
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.helper.HelperUtils
import com.example.myapplication.services.MockGpsService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

private const val SDK_PERMISSION_REQUEST = 127
class MainActivity : AppCompatActivity() {

    companion object
    {
        var latLngInfo = "104.06121778639009&30.544111926165282"

    }
    private var gpsTracker: GpsTracker? = null

    private val  mainActivityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private var isMockLocOpen = false
    private var mockServiceReceiver: MockServiceReceiver? = null
    var isServiceRun = false
    private var isMockServStart = false
    private var isGPSOpen = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainActivityMainBinding.root)

        getPerMissions()
        setFabListener()

        Thread {
            while (!HelperUtils.isGpsOpened(this)) {
                Log.d("GPS", "gps not open")
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            isGPSOpen = true
            Log.d("GPS", "gps opened")
            //如果GPS定位开启，打开定位图层
        }.start()
        val lat =   intent.getStringExtra("lat")
        val long =    intent.getStringExtra("long")
        if(lat!= null && long!= null)
        {
            latLngInfo = "$lat&$long"

        }

        Log.d("onCreate", "onCreate: "+ latLngInfo)

        try {
            mockServiceReceiver = MockServiceReceiver()
            val filter = IntentFilter()
            filter.addAction("com.example.service.MockGpsService")
            //            this.unregisterReceiver(mockServiceReceiver);
            this.registerReceiver(mockServiceReceiver, filter)
        } catch (e: java.lang.Exception) {
            Log.e("UNKNOWN", "registerReceiver error")
            e.printStackTrace()
        }



        mainActivityMainBinding.stopService.setOnClickListener {
            stopService(Intent(this@MainActivity, MockGpsService::class.java))

        }


    }

    fun getLocation() {
        gpsTracker = GpsTracker(this@MainActivity)
        if (gpsTracker!!.canGetLocation()) {
            val latitude = gpsTracker!!.getLatitude()
            val longitude = gpsTracker!!.getLongitude()
            mainActivityMainBinding.latitude.text = latitude.toString()
            mainActivityMainBinding.logitude.text = longitude.toString()
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }



    override fun onResume() {
        super.onResume()
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== SDK_PERMISSION_REQUEST)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!HelperUtils.isAllowMockLocation(this).also { isMockLocOpen = it }) {
                    setDialog()
                }
            }
            else
            {
                Toast.makeText(this, "Allow permission", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun getPerMissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        {
            val permissions = ArrayList<String>()
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (permissions.size > 0) {
                requestPermissions(permissions.toTypedArray<String>(), SDK_PERMISSION_REQUEST)
            }
        }
        else
        {
            allowPermission()
        }
    }
    private fun allowPermission(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", applicationContext.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==101)
        {
            if(HelperUtils.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION,this))
            {
                if (!HelperUtils.isAllowMockLocation(this).also { isMockLocOpen = it }) {
                    setDialog()
                }
            }
            else
            {
                Toast.makeText(this, "Allow permission", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setFabListener() {

        //应用内悬浮按钮
        mainActivityMainBinding.fab.setOnClickListener { view ->
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                if (!isGPSOpen) {
                    showGpsDialog()
                } else {
                    if (!HelperUtils.isAllowMockLocation(this).also { isMockLocOpen = it }) {
                        setDialog()
                    } else {
                        if (!isMockServStart && !isServiceRun) {
                            val mockLocServiceIntent = Intent(
                                this@MainActivity,
                                MockGpsService::class.java
                            )
                            mockLocServiceIntent.putExtra("key", latLngInfo)

                            if (Build.VERSION.SDK_INT >= 26) {
                                startForegroundService(mockLocServiceIntent)
                                Log.d("DEBUG", "startForegroundService: MOCK_GPS")
                            } else {
                                startService(mockLocServiceIntent)
                                Log.d("DEBUG", "startService: MOCK_GPS")
                            }
                            isMockServStart = true
                            Snackbar.make(view, "Location mocking is on", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()

                        } else {
                            Snackbar.make(
                                view,
                                "Location simulation is already running", Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null).show()
                            isMockServStart = true
                        }
                    }
                }
            } else {
                getPerMissions()
            }
        }
    }


    private fun showGpsDialog() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Tips") //这里是表头的内容
            .setMessage("Do you want to enable GPS location service?") //这里是中间显示的具体信息
            .setPositiveButton(
                "Sure"
            ) { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 0)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> }
            .show()
    }
    private fun setDialog() {

        AlertDialog.Builder(this)
            .setTitle("Enable location mocking") //这里是表头的内容
            .setMessage("Please set it in \"Developer Options → Select mock location information application\"") //这里是中间显示的具体信息
            .setPositiveButton(
                "Set up"
            )
            { dialog, which ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } //setPositiveButton
            .setNegativeButton(
                "Cancel"
            )  //这个string是设置右边按钮的文字
            { dialog, which -> } //setNegativeButton
            .show()
    }
    inner class MockServiceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val statusCode: Int
            val bundle = intent.extras!!
            statusCode = bundle.getInt("statusCode")
            Log.d("DEBUG", "BroadcastReceiver statusCode: $statusCode")
            /*if (statusCode == RunCode) {
                 = true
            } else if (statusCode == StopCode) {
                i = false
            }*/
        }
    }


}