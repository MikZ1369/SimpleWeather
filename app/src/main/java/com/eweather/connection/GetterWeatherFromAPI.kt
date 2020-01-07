package com.eweather.connection

import android.location.Location
import java.io.*
import java.net.*



class GetterWeatherFromAPI {
    private val APIKEY: String = "c9464521fc7e17f35180903309d9f13c"

    fun getWeather(location: Location): String? {
        var url = "https://api.darksky.net/forecast/"
        url += "$APIKEY/${location.latitude},${location.longitude}"
        return getData(url)
    }

    private fun getData(url: String): String?{
        if (!checkConnection()) return null
        var inputLine: String
        var inputStream: InputStream? = null

        try {
            val connect = URL(url)
            val con: HttpURLConnection = connect.openConnection() as HttpURLConnection
            inputStream = con.getInputStream()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        inputLine = ""
        var line: String? = null

        try {
            while (bufferedReader!!.readLine().also({ line = it }) != null) {
                inputLine += line
            }
        } catch (ignored: IOException) {
        }

        try {
            bufferedReader!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return inputLine
    }

    private fun checkConnection(): Boolean {
        try {
            val timeOutms = 1500
            val socket: Socket = Socket()
            val socketAddress: SocketAddress = InetSocketAddress("8.8.8.8", 53)

            socket.connect(socketAddress, timeOutms)
            socket.close()
            return true
        } catch (e: IOException) {
            return false
        }
    }

}