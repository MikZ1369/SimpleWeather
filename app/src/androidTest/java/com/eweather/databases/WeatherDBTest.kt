package com.eweather.databases

import androidx.room.Room
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class WeatherDBTest {
    private val context = getTargetContext()
    @Test
    fun testDB() {
        val weatherDB = Room.databaseBuilder(context,
            WeatherDataBase.WeatherDB::class.java, "testDB").build()
        val currentlyDao = weatherDB.weatherCurrentlyDao()
        val hourlyDao = weatherDB.weatherHourlyDao()
        val dailyDao = weatherDB.weatherDailyDao()

        val weatherCurrently = WeatherDataBase.WeatherCurrently(null,
            "", 0, 23)

        val weatherHourly = WeatherDataBase.WeatherHourly(null,
            "", 0, 2, 23)

        val weatherDaily = WeatherDataBase.WeatherDaily(null,
        "", 0, 2, 1, 23)

        currentlyDao.insertAll(weatherCurrently)
        hourlyDao.insertAll(weatherHourly)
        dailyDao.insertAll(weatherDaily)

        var weatherCurrentlyListTest = currentlyDao.getAll()
        var weatherHourlyListTest = hourlyDao.getAll()
        var weatherDailyListTest = dailyDao.getAll()

        weatherCurrentlyListTest.forEach {
            assertEquals(weatherCurrently.icon, it.icon)
            assertEquals(weatherCurrently.temperature, it.temperature)
            assertEquals(weatherCurrently.windSpeed, it.windSpeed)
        }

        weatherHourlyListTest.forEach {
            assertEquals(weatherHourly.icon, it.icon)
            assertEquals(weatherHourly.temperature, it.temperature)
            assertEquals(weatherHourly.time, it.time)
            assertEquals(weatherHourly.windSpeed, it.windSpeed)
        }

        weatherDailyListTest.forEach {
            assertEquals(weatherDaily.icon, it.icon)
            assertEquals(weatherDaily.temperatureLow, it.temperatureLow)
            assertEquals(weatherDaily.temperatureHigh, it.temperatureHigh)
            assertEquals(weatherDaily.time, it.time)
            assertEquals(weatherDaily.windSpeed, it.windSpeed)
        }

        currentlyDao.deleteAll()
        weatherCurrentlyListTest = currentlyDao.getAll()
        assertEquals(0, weatherCurrentlyListTest.size)

        hourlyDao.deleteAll()
        weatherHourlyListTest = hourlyDao.getAll()
        assertEquals(0, weatherHourlyListTest.size)

        dailyDao.deleteAll()
        weatherDailyListTest = dailyDao.getAll()
        assertEquals(0, weatherDailyListTest.size)

        weatherDB.close()
    }



    @After
    fun removeDB() {
        var dataBaseFile = File( File(getTargetContext().applicationInfo.dataDir +
                "/databases"), "testDB")
        dataBaseFile.delete()
    }
}