package com.eweather.view

import android.content.Context
import android.graphics.drawable.Drawable
import com.eweather.R

class ResourcesImage(val context: Context) {
    fun getResourcesImage(icon: String) : Drawable {
        when(icon) {
            "clear-day" -> return context.resources.getDrawable(R.drawable.clear_day)
            "clear-night" -> return context.resources.getDrawable(R.drawable.clear_night)
            "rain" -> return context.resources.getDrawable(R.drawable.rain)
            "snow" -> return context.resources.getDrawable(R.drawable.snow)
            "sleet" -> return context.resources.getDrawable(R.drawable.sleet)
            "wind" -> return context.resources.getDrawable(R.drawable.wind)
            "fog" -> return context.resources.getDrawable(R.drawable.fog)
            "cloudy" -> return context.resources.getDrawable(R.drawable.cloudy)
            "partly-cloudy-day" -> return context.resources.getDrawable(R.drawable.partly_cloudy_day)
            "partly-cloudy-night" -> return context.resources.getDrawable(R.drawable.partly_cloudy_night)
        }
        return context.resources.getDrawable(R.drawable.clear_day)
    }
}