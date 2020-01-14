package com.eweather.databases

import org.json.JSONObject;

class JsonParse {
    lateinit var currentlyWeather: WeatherDataBase.WeatherCurrently
    lateinit var hourlyWeatherArray: ArrayList<WeatherDataBase.WeatherHourly>
    lateinit var dailyWeatherArray: ArrayList<WeatherDataBase.WeatherDaily>

    fun weatherParse(json: String) {
        val jsonObject = JSONObject(json)
        currentlyWeather = getCurrentlyWeather(jsonObject)
        hourlyWeatherArray = getHourlyWeather(jsonObject)
        dailyWeatherArray = getDailyWeather(jsonObject)
    }

    private fun getCurrentlyWeather(jsonObject: JSONObject): WeatherDataBase.WeatherCurrently {
        val jWeatherCurrently = jsonObject.getJSONObject("currently")
        return WeatherDataBase.WeatherCurrently(null,
            jWeatherCurrently.getString("icon"), jWeatherCurrently.getInt("temperature"),
            jWeatherCurrently.getInt("windSpeed"))
    }

    private fun getHourlyWeather(jsonObject: JSONObject): ArrayList<WeatherDataBase.WeatherHourly> {
        val jsonHourly = jsonObject.getJSONObject("hourly")
        val jsonArrayHourly = jsonHourly.getJSONArray("data")
        val weatherHourlyArray: ArrayList<WeatherDataBase.WeatherHourly> = ArrayList()
        for (item: Int in 0 until jsonArrayHourly.length()) {
            val jWeatherHourly = jsonArrayHourly.getJSONObject(item)
            weatherHourlyArray.add(WeatherDataBase.WeatherHourly(null,
                jWeatherHourly.getString("icon"), jWeatherHourly.getInt("time"),
                jWeatherHourly.getInt("temperature"), jWeatherHourly.getInt("windSpeed")))
        }
        return weatherHourlyArray
    }

    private fun getDailyWeather(jsonObject: JSONObject): ArrayList<WeatherDataBase.WeatherDaily> {
        val jsonDaily = jsonObject.getJSONObject("daily")
        val jsonArrayDaily = jsonDaily.getJSONArray("data")
        val weatherDailyArray: ArrayList<WeatherDataBase.WeatherDaily> = ArrayList()
        for (item: Int in 0 until jsonArrayDaily.length()) {
            val jWeatherDaily = jsonArrayDaily.getJSONObject(item)
            weatherDailyArray.add(WeatherDataBase.WeatherDaily(null,
                jWeatherDaily.getString("icon"), jWeatherDaily.getInt("time"),
                jWeatherDaily.getInt("temperatureHigh"), jWeatherDaily.getInt("temperatureLow")
                , jWeatherDaily.getInt("windSpeed")))
        }
        return weatherDailyArray
    }
}