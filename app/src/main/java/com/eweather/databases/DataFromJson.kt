package com.eweather.databases

import android.content.Context
import android.content.SharedPreferences
import com.eweather.R
import org.json.JSONObject;

class DataFromJson(val context: Context) {
    lateinit var currentlyWeather: WeatherDataBase.WeatherCurrently
    lateinit var hourlyWeatherArray: ArrayList<WeatherDataBase.WeatherHourly>
    lateinit var dailyWeatherArray: ArrayList<WeatherDataBase.WeatherDaily>
    private var prefs: SharedPreferences
    private lateinit var temperatureKey: String

    init {
        prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        temperatureKey = prefs.getString(context.getString(R.string.temperatureKey),
            context.getString(R.string.celsius)).toString()
    }

    fun weatherParse(json: String) {
        val jsonObject = JSONObject(json)
        currentlyWeather = getCurrentlyWeather(jsonObject)
        hourlyWeatherArray = getHourlyWeather(jsonObject)
        dailyWeatherArray = getDailyWeather(jsonObject)
    }

    private fun getCurrentlyWeather(jsonObject: JSONObject): WeatherDataBase.WeatherCurrently {
        val jWeatherCurrently = jsonObject.getJSONObject("currently")
        var temperature = ((jWeatherCurrently.getInt("temperature") - 32) / 1.8).toInt()
        if(temperatureKey.equals(context.getString(R.string.fahrenheit))) {
            temperature = jWeatherCurrently.getInt("temperature")
        }
        val windSpeed = jWeatherCurrently.getInt("windSpeed")
        return WeatherDataBase.WeatherCurrently(null,
            jWeatherCurrently.getString("icon"), temperature, windSpeed,
            jsonObject.getString("timezone"))
    }

    private fun getHourlyWeather(jsonObject: JSONObject): ArrayList<WeatherDataBase.WeatherHourly> {
        val jsonHourly = jsonObject.getJSONObject("hourly")
        val jsonArrayHourly = jsonHourly.getJSONArray("data")
        val weatherHourlyArray: ArrayList<WeatherDataBase.WeatherHourly> = ArrayList()
        for (item: Int in 0 until 22) {
            val jWeatherHourly = jsonArrayHourly.getJSONObject(item)
            var temperature = ((jWeatherHourly.getInt("temperature") - 32) / 1.8).toInt()
            if(temperatureKey == context.getString(R.string.fahrenheit)) {
                temperature = jWeatherHourly.getInt("temperature")
            }
            val windSpeed = jWeatherHourly.getInt("windSpeed")
            weatherHourlyArray.add(WeatherDataBase.WeatherHourly(null,
                jWeatherHourly.getString("icon"), jWeatherHourly.getInt("time"),
                temperature, windSpeed))
        }
        return weatherHourlyArray
    }

    private fun getDailyWeather(jsonObject: JSONObject): ArrayList<WeatherDataBase.WeatherDaily> {
        val jsonDaily = jsonObject.getJSONObject("daily")
        val jsonArrayDaily = jsonDaily.getJSONArray("data")
        val weatherDailyArray: ArrayList<WeatherDataBase.WeatherDaily> = ArrayList()
        for (item: Int in 0 until jsonArrayDaily.length() - 1) {
            val jWeatherDaily = jsonArrayDaily.getJSONObject(item)
            var temperatureHigh = ((jWeatherDaily.getInt("temperatureHigh") - 32) / 1.8).toInt()
            if(temperatureKey == context.getString(R.string.fahrenheit)) {
                temperatureHigh = jWeatherDaily.getInt("temperatureHigh")
            }
            var temperatureLow = ((jWeatherDaily.getInt("temperatureLow") - 32) / 1.8).toInt()
            if(temperatureKey == context.getString(R.string.fahrenheit)) {
                temperatureLow = jWeatherDaily.getInt("temperatureLow")
            }
            val windSpeed = jWeatherDaily.getInt("windSpeed")
            weatherDailyArray.add(WeatherDataBase.WeatherDaily(null,
                jWeatherDaily.getString("icon"), jWeatherDaily.getInt("time"),
                temperatureHigh, temperatureLow, windSpeed))
        }
        return weatherDailyArray
    }
}