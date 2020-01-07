package com.eweather.databases

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JsonParserTest {
    @Test
    fun test() {
        val currentlyIcon = "iconCurrently"
        val currentlyTemperature = 25
        val currentlyWindSpeed = 6

        val hourlyIcon = "iconHourly"
        val hourlyTime = 0
        val hourlyTemperature = 25
        val hourlyWindSpeed = 6

        val dailyIcon = "iconDaily"
        val dailyTime = 0
        val dailyTemperatureHigh = 25
        val dailyTemperatureLow = 25
        val dailyWindSpeed = 6


        val json = "{\"currently\": {\"icon\":$currentlyIcon, \"temperature\":$currentlyTemperature" +
                ", \"windSpeed\":$currentlyWindSpeed},\"hourly\":{\"data\":[{\"icon\":$hourlyIcon," +
                "\"time\":$hourlyTime,\"temperature\":$hourlyTemperature,\"windSpeed\":$hourlyWindSpeed" +
                "}]}, \"daily\":{\"data\":[{\"icon\":$dailyIcon,\"time\":$dailyTime,\"temperatureHigh\":" +
                "$dailyTemperatureHigh,\"temperatureLow\":$dailyTemperatureLow,\"windSpeed\":$dailyWindSpeed}]}}"

        val jsonParse = JsonParse()
        jsonParse.weatherParse(json)

        val currenltyWeather = jsonParse.currentlyWeather
        val hourlyWeather = jsonParse.hourlyWeatherArray
        val dailyWeather = jsonParse.dailyWeatherArray

        assertEquals(currentlyIcon, currenltyWeather.icon)
        assertEquals(currentlyTemperature, currenltyWeather.temperature)
        assertEquals(currentlyWindSpeed, currenltyWeather.windSpeed)

        hourlyWeather.forEach {
            assertEquals(hourlyIcon, it.icon)
            assertEquals(hourlyTime, it.time)
            assertEquals(hourlyTemperature, it.temperature)
            assertEquals(hourlyWindSpeed, it.windSpeed)
        }

        dailyWeather.forEach {
            assertEquals(dailyIcon, it.icon)
            assertEquals(dailyTime, it.time)
            assertEquals(dailyTemperatureHigh, it.temperatureHigh)
            assertEquals(dailyTemperatureLow, it.temperatureLow)
            assertEquals(dailyWindSpeed, it.windSpeed)
        }
    }
}