package com.eweather.connection

import android.location.Location
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetterWeatherFromAPITest {
    @Test
    fun getWeather() {
        val location = Location("1")
        location.latitude = 50.0
        location.longitude =50.0

        val getterWeatherFromAPI = GetterWeatherFromAPI()
        val result = getterWeatherFromAPI.getWeather(location)
        assertNotNull(result)
    }
}