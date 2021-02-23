package com.myapp.osmnavigation.serviceLocation

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.myapp.osmnavigation.util.Const
import java.util.concurrent.TimeUnit

class MyServiceLocation : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
// настройка таймаута обновления местоположения
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if (p0?.lastLocation != null) {
                    Log.e("tyi", "${p0.lastLocation}")

                    // отправляем локацию пользователя
                    val intent = Intent(Const.SET_ACTION)
                    intent.putExtra(Const.KEY_LAT_LNG, p0.lastLocation)
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        locationUpdate()
        return START_STICKY
    }

    // обновляем местоположения
    private fun locationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}