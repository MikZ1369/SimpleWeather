package com.eweather.databases

import org.json.JSONObject;

class JsonParse {
    lateinit var currentlyWeather: WeatherDB.WeatherCurrently
    lateinit var hourlyWeatherArray: ArrayList<WeatherDB.WeatherHourly>
    lateinit var dailyWeatherArray: ArrayList<WeatherDB.WeatherDaily>

    fun weatherParse(json: String) {
        val jsonObject = JSONObject(json)
        currentlyWeather = getCurrentlyWeather(jsonObject)
        hourlyWeatherArray = getHourlyWeather(jsonObject)
        dailyWeatherArray = getDailyWeather(jsonObject)
    }

    private fun getCurrentlyWeather(jsonObject: JSONObject): WeatherDB.WeatherCurrently {
        val jWeatherCurrently = jsonObject.getJSONObject("currently")
        return WeatherDB.WeatherCurrently(null,
            jWeatherCurrently.getString("icon"), jWeatherCurrently.getInt("temperature"),
            jWeatherCurrently.getInt("windSpeed"))
    }

    private fun getHourlyWeather(jsonObject: JSONObject): ArrayList<WeatherDB.WeatherHourly> {
        val jsonHourly = jsonObject.getJSONObject("hourly")
        val jsonArrayHourly = jsonHourly.getJSONArray("data")
        val weatherHourlyArray: ArrayList<WeatherDB.WeatherHourly> = ArrayList()
        for (item: Int in 0 until jsonArrayHourly.length()) {
            val jWeatherHourly = jsonArrayHourly.getJSONObject(item)
            weatherHourlyArray.add(WeatherDB.WeatherHourly(null,
                jWeatherHourly.getString("icon"), jWeatherHourly.getInt("time"),
                jWeatherHourly.getInt("temperature"), jWeatherHourly.getInt("windSpeed")))
        }
        return weatherHourlyArray
    }

    private fun getDailyWeather(jsonObject: JSONObject): ArrayList<WeatherDB.WeatherDaily> {
        val jsonDaily = jsonObject.getJSONObject("daily")
        val jsonArrayDaily = jsonDaily.getJSONArray("data")
        val weatherDailyArray: ArrayList<WeatherDB.WeatherDaily> = ArrayList()
        for (item: Int in 0 until jsonArrayDaily.length()) {
            val jWeatherDaily = jsonArrayDaily.getJSONObject(item)
            weatherDailyArray.add(WeatherDB.WeatherDaily(null,
                jWeatherDaily.getString("icon"), jWeatherDaily.getInt("time"),
                jWeatherDaily.getInt("temperatureHigh"), jWeatherDaily.getInt("temperatureLow")
                , jWeatherDaily.getInt("windSpeed")))
        }
        return weatherDailyArray
    }
}