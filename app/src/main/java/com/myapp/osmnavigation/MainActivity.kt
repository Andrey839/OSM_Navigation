package com.myapp.osmnavigation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment
import com.myapp.osmnavigation.databinding.ActivityMainBinding
import com.myapp.osmnavigation.serviceLocation.MyServiceLocation
import com.myapp.osmnavigation.util.Const
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, )

        requestPermissions()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
    }

    //  переопределяем запрос разрешения в Easy Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //запускаем сервис по поиску местоположения
        startService(Intent(this, MyServiceLocation::class.java))
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AppSettingsDialog.Builder(this).build().show()
    }

    // запрос разрешения
    private fun requestPermissions() {
        if (requestPermission()) {
            //запускаем сервис по поиску местоположения
            startService(Intent(this, MyServiceLocation::class.java))
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale),
                Const.PERMISSION_NUMB,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // проверка давал ражрешение пользователь?
    private fun requestPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) Toast.makeText(
                this,
                getString(R.string.permission_rationale_sitting),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("tyi", "error permission")
        }
    }

    override fun onResume() {
        super.onResume()
        KeyboardVisibilityEvent.setEventListener(this) {
            if (it){
                binding.myMap.visibility = View.GONE
                binding.userAccount.visibility = View.GONE
                binding.aboutInfo.visibility = View.GONE
            } else {
                binding.myMap.visibility = View.VISIBLE
                binding.userAccount.visibility = View.VISIBLE
                binding.aboutInfo.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyServiceLocation::class.java))
    }

}