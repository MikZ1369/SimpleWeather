package com.eweather.view

import android.content.Context
import android.content.pm.PackageManager
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eweather.R
import com.eweather.connection.GetterWeatherFromAPI
import com.eweather.databases.JsonParse
import com.eweather.databases.WeatherDB
import com.eweather.databases.creatorWeatherDB
import com.eweather.location.GetterDeviceLocation
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : Activity() {
    private val MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0
    private val job: Job = Job()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val scope = CoroutineScope(coroutineContext)
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_LOCATION)
        }
        else {
            val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, this)
            scope.launch {
                withContext(Dispatchers.IO) {getterDeviceLocation.getLocation()}
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) { MY_PERMISSIONS_REQUEST_ACCESS_LOCATION -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, this)
                    scope.launch {
                        getterDeviceLocation.getLocation()
                    }
                } else {
                }
                return
            }
        }
    }

    private fun updateUI(weatherPackage: WeatherDB.WeatherPackage) {
        val currentTempTextView = findViewById<TextView>(R.id.currentTemp)
        currentTempTextView.text = weatherPackage.weatherCurrently.temperature.toString()
    }

    private val workerGetWeatherFromApi: (Location?) -> Unit = { location: Location? ->
        scope.launch {
            var weatherPackage: WeatherDB.WeatherPackage? = null
            withContext(Dispatchers.IO) {
                val getterWeatherFromAPI = GetterWeatherFromAPI()
                val jsonResult = getterWeatherFromAPI.getWeather(location!!)
                val jsonParse = JsonParse()
                jsonParse.weatherParse(jsonResult!!)
                weatherPackage = WeatherDB.WeatherPackage(jsonParse.currentlyWeather,
                    jsonParse.hourlyWeatherArray, jsonParse.dailyWeatherArray)
            }
            updateUI(weatherPackage!!)
            workerSaveWeatherDataToDB(weatherPackage!!)
        }
    }

    private val workerSaveWeatherDataToDB: (WeatherDB.WeatherPackage) -> Unit = {weatherPackage: WeatherDB.WeatherPackage ->
        scope.launch {
            withContext(Dispatchers.IO) {
                val weatherDB = creatorWeatherDB(context, "weatherDB")
                val currentlyDao = weatherDB.weatherCurrentlyDao()
                val hourlyDao = weatherDB.weatherHourlyDao()
                val dailyDao = weatherDB.weatherDailyDao()

                currentlyDao.deleteAll()
                hourlyDao.deleteAll()
                dailyDao.deleteAll()

                currentlyDao.insertAll(weatherPackage.weatherCurrently)
                weatherPackage.weatherHourly.forEach {
                    hourlyDao.insertAll(it)
                }
                weatherPackage.weatherDaily.forEach {
                    dailyDao.insertAll(it)
                }
            }
        }
    }

}