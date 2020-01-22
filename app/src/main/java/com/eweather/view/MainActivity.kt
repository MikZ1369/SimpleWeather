package com.eweather.view

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.eweather.R
import com.eweather.connection.GetterWeatherFromAPI
import com.eweather.databases.DataFromJson
import com.eweather.databases.WeatherDataBase
import com.eweather.location.GetterDeviceLocation
import kotlinx.coroutines.*
import java.lang.NullPointerException
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)
        }
        else {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = MainFragment()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.commit()
        }

    }

    override fun onResume() {
        super.onResume()

        if(prefs.getBoolean("firstrun", true)) {
            val editor = prefs.edit()
            editor.putString(getString(R.string.temperatureKey), getString(R.string.celsius))
            editor.putBoolean("firstrun", false).apply()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) { MY_PERMISSIONS_REQUEST_ACCESS_LOCATION -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val fragment = MainFragment()
                    fragmentTransaction.replace(R.id.container, fragment)
                    fragmentTransaction.commit()
                } else {
                    Toast.makeText(this, "OK:(",
                        Toast.LENGTH_LONG).show()
                }
                return
        } } }


}