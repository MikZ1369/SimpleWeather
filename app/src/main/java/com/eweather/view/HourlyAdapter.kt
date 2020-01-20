package com.eweather.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eweather.R
import com.eweather.databases.WeatherDataBase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class HourlyAdapter(private val weatherHourlyList: List<WeatherDataBase.WeatherHourly>,
                    private val timeZone: String,private val context: Context) : RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = weatherHourlyList.size

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherHourly = weatherHourlyList[position]
        val imageViewIcon = holder.itemView.findViewById(R.id.hourlyIcon) as ImageView
        val textViewTemperature = holder.itemView.findViewById(R.id.hourlyTemperature) as TextView
        val textViewTime = holder.itemView.findViewById(R.id.hourlyTime) as TextView
        val textViewWindSpeed = holder.itemView.findViewById(R.id.hourlyWindSpeed) as TextView

        imageViewIcon.setImageDrawable(ResourcesImage(context).getResourcesImage(weatherHourly.icon))
        textViewTemperature.text = weatherHourly.temperature.toString()
        textViewWindSpeed.text = weatherHourly.windSpeed.toString()
        val time = LocalDateTime.ofEpochSecond(weatherHourly.time.toLong(), 0 ,
            ZoneId.of(timeZone).rules.getOffset(LocalDateTime.now()))
        textViewTime.text = time.hour.toString() + ":00"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


}