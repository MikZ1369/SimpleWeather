package com.eweather.view

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class MainFragment: Fragment() {
    private val MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0
    private val job: Job = Job()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val scope = CoroutineScope(coroutineContext)
    private var dataUpdatedKey = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, context!!)
        scope.launch {
            withContext(Dispatchers.IO) {getterDeviceLocation.getLocation()}
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val buttonSettings = view.findViewById<Button>(R.id.settingsButton)
        buttonSettings.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = SettingsFragment()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(dataUpdatedKey) {
            val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, context!!)
            scope.launch {
                withContext(Dispatchers.IO) {getterDeviceLocation.getLocation()}
            }
        }
    }

    private fun launchRequestingDataFromDB() {
        scope.launch {
            var weatherPackage: WeatherDataBase.WeatherPackage? = null
            withContext(Dispatchers.IO) {
                val weatherDB = Room.databaseBuilder(context!!,
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
        scope.launch {
            try {
                val getterDeviceLocation = GetterDeviceLocation(workerGetWeatherFromApi, context!!)
                getterDeviceLocation.getLocation()
            } catch (e: KotlinNullPointerException) {
                scope.cancel()
            }

        }
    }

    private fun updateUI(weatherPackage: WeatherDataBase.WeatherPackage) {
        val currentTempTextView = view!!.findViewById<TextView>(R.id.currentTemp)
        val summaryCurrentlyTextView = view!!.findViewById<TextView>(R.id.summaryCurrently)
        val summaryHourlyTextView = view!!.findViewById<TextView>(R.id.summaryHourly)
        val imageViewIcon = view!!.findViewById(R.id.currentlyIcon) as ImageView
        currentTempTextView.text = weatherPackage.weatherCurrently.temperature.toString()
        summaryCurrentlyTextView.text = weatherPackage.weatherCurrently.summaryCurrently
        summaryHourlyTextView.text = weatherPackage.weatherCurrently.summaryHourly
        imageViewIcon.setImageDrawable(ResourcesImage(context!!).getResourcesImage(weatherPackage.weatherCurrently.icon))
        val viewManagerHourly = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        val viewManagerDaily = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)

        val hourlyAdapter = HourlyAdapter(weatherPackage.weatherHourly,
            weatherPackage.weatherCurrently.timeZone, context!!)
        val dailyAdapter = DailyAdapter(weatherPackage.weatherDaily,
            weatherPackage.weatherCurrently.timeZone, context!!)

        val recyclerViewHoruly = view!!.findViewById<RecyclerView>(R.id.hourly_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManagerHourly
            adapter = hourlyAdapter
        }

        val recyclerViewDaily = view!!.findViewById<RecyclerView>(R.id.daily_recycler_view).apply {
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
                    val jsonParse = DataFromJson(context!!)
                    jsonParse.weatherParse(jsonResult!!)
                    weatherPackage = WeatherDataBase.WeatherPackage(jsonParse.currentlyWeather,
                        jsonParse.hourlyWeatherArray, jsonParse.dailyWeatherArray)
                } catch (exception: NullPointerException) {
                    launchRequestingDataFromNet()
                }
            }
            dataUpdatedKey = true
            updateUI(weatherPackage!!)
            workerSaveWeatherDataToDB(weatherPackage!!)
        }
    }

    private val workerSaveWeatherDataToDB: (WeatherDataBase.WeatherPackage) -> Unit = { weatherPackage: WeatherDataBase.WeatherPackage ->
        scope.launch {
            withContext(Dispatchers.IO) {
                val weatherDB = Room.databaseBuilder(context!!,
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