package com.eweather.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class GetterDeviceLocation(private val worker: (Location?) -> Unit, private val context: Context) {
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { location ->
            if (location.result == null) {
                createLocationCallBack()
                createLocationRequest()
                startLocationUpdates()
                return@addOnCompleteListener
            } else postLocationUpdate(location.result!!)
        }
    }

    private fun createLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                postLocationUpdate(locationResult!!.lastLocation)
                stopLocationUpdates()
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun postLocationUpdate(location: Location?) {
        worker(location)
    }
}