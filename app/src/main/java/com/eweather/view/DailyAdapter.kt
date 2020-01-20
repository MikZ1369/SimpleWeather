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

class DailyAdapter(private val weatherDailyList: List<WeatherDataBase.WeatherDaily>,
                   private val timeZone: String, private val context: Context) : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = weatherDailyList.size

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherDaily = weatherDailyList[position]
        val imageViewIcon = holder.itemView.findViewById(R.id.dailyIcon) as ImageView
        val textViewTime = holder.itemView.findViewById(R.id.dailyDate) as TextView
        val textViewWindSpeed = holder.itemView.findViewById(R.id.dailyWindSpeed) as TextView
        val textViewTemperatureHigh = holder.itemView.findViewById(R.id.dailyTemperatureHigh) as TextView
        val textViewTemperatureLow = holder.itemView.findViewById(R.id.dailyTemperatureLow) as TextView

        imageViewIcon.setImageDrawable(ResourcesImage(context).getResourcesImage(weatherDaily.icon))
        val date = LocalDateTime.ofEpochSecond(weatherDaily.time.toLong(), 0 ,
            ZoneId.of(timeZone).rules.getOffset(LocalDateTime.now()))
        textViewTemperatureHigh.text = weatherDaily.temperatureHigh.toString()
        textViewTemperatureLow.text = weatherDaily.temperatureLow.toString()
        textViewTime.text = date.dayOfWeek.toString().substring(0, 3) + " " + date.dayOfMonth.toString()
        textViewWindSpeed.text = weatherDaily.windSpeed.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}