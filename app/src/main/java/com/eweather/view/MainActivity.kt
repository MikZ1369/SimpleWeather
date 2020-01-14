package com.eweather.view

import android.content.Context
import android.content.pm.PackageManager
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.eweather.R
import com.eweather.connection.GetterWeatherFromAPI
import com.eweather.databases.JsonParse
import com.eweather.databases.WeatherDataBase
import com.eweather.location.GetterDeviceLocation
import kotlinx.coroutines.*
import java.lang.NullPointerException
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(R.layout.activity_main)
        context = this
        launchRequestingDataFromDB()

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
                    launchRequestingDataFromNet()
                } else {
                    Toast.makeText(this, "OK:(",
                        Toast.LENGTH_LONG).show()
                }
                return
        } } }


    private fun launchRequestingDataFromDB() {
        scope.launch {
            var weatherPackage: WeatherDataBase.WeatherPackage? = null
            withContext(Dispatchers.IO) {
                val weatherDB = Room.databaseBuilder(applicationContext,
                    WeatherDataBase.WeatherDB::class.java, "weatherDB").build()
                val currentlyDao = weatherDB.weatherCurrentlyDao()
                val hourlyDao = weatherDB.weatherHourlyDao()
                val dailyDao = weatherDB.weatherDailyDao()
                var weatherCurrentlyList = currentlyDao.getAll()
                if (weatherCurrentlyList.size == 0) return@withContext
                var weatherHourlyList = hourlyDao.getAll()
                var weatherDailyList = dailyDao.getAll()

                val weatherCurrenlty = weatherCurrentlyList[0]
                weatherPackage = WeatherDataBase.WeatherPackage(weatherCurrenlty, weatherHourlyList,
                    weatherDailyList)
            }
            try {
                updateUI(weatherPackage!!)
            } catch (exception: NullPointerException) { }
        }
    }

    private fun launchRequestingDataFromNet() {
        val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, this)
        scope.launch {
            getterDeviceLocation.getLocation()
        }
    }

    private fun updateUI(weatherPackage: WeatherDataBase.WeatherPackage) {
        val currentTempTextView = findViewById<TextView>(R.id.currentTemp)
        currentTempTextView.text = weatherPackage.weatherCurrently.temperature.toString()
        val viewManagerHourly = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val viewManagerDaily = LinearLayoutManager(this)
        val hourlyAdapter = HourlyAdapter(weatherPackage.weatherHourly, this)
        val dailyAdapter = DailyAdapter(weatherPackage.weatherDaily)

        val recyclerViewHoruly = findViewById<RecyclerView>(R.id.hourly_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManagerHourly
            adapter = hourlyAdapter
        }

        val recyclerViewDaily = findViewById<RecyclerView>(R.id.daily_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManagerDaily
            adapter = dailyAdapter
        }
    }

    private val workerGetWeatherFromApi: (Location) -> Unit = { location: Location ->
        scope.launch {
            var weatherPackage: WeatherDataBase.WeatherPackage? = null
            withContext(Dispatchers.IO) {
                try {
                    val getterWeatherFromAPI = GetterWeatherFromAPI()
                    val jsonResult = getterWeatherFromAPI.getWeather(location)
                    val jsonParse = JsonParse()
                    jsonParse.weatherParse(jsonResult!!)
                    weatherPackage = WeatherDataBase.WeatherPackage(jsonParse.currentlyWeather,
                        jsonParse.hourlyWeatherArray, jsonParse.dailyWeatherArray)
                } catch (exception: NullPointerException) {
                    launchRequestingDataFromNet()
                }
            }
            updateUI(weatherPackage!!)
            workerSaveWeatherDataToDB(weatherPackage!!)
        }
    }

    private val workerSaveWeatherDataToDB: (WeatherDataBase.WeatherPackage) -> Unit = { weatherPackage: WeatherDataBase.WeatherPackage ->
        scope.launch {
            withContext(Dispatchers.IO) {
                val weatherDB = Room.databaseBuilder(applicationContext,
                    WeatherDataBase.WeatherDB::class.java, "weatherDB").build()
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