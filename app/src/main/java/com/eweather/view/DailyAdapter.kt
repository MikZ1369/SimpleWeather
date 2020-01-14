package com.eweather.view


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eweather.R
import com.eweather.databases.WeatherDataBase

class DailyAdapter(private val weatherDailyList: List<WeatherDataBase.WeatherDaily>) : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = weatherDailyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherHourly = weatherDailyList[position]
        val textViewIcon = holder.itemView.findViewById(R.id.dailyIcon) as TextView
        val textViewTime = holder.itemView.findViewById(R.id.dailyTime) as TextView
        val textViewWindSpeed = holder.itemView.findViewById(R.id.dailyWindSpeed) as TextView

        textViewIcon.text = weatherHourly.icon
        textViewTime.text = weatherHourly.time.toString()
        textViewWindSpeed.text = weatherHourly.windSpeed.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}