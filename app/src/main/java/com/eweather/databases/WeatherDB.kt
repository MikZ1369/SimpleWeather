package com.eweather.databases

import android.content.Context
import androidx.room.*

class WeatherDB {
    @Entity (tableName = "weatherCurrently")
    class WeatherCurrently (
       @PrimaryKey(autoGenerate = true) var weatherCurrentlyID: Int? = null,
       @ColumnInfo(name = "icon") var icon: String,
       @ColumnInfo(name = "temperature") var temperature: Int,
       @ColumnInfo(name = "windSpeed") var windSpeed: Int
    ) {}

    @Entity (tableName = "weatherHourly")
    class WeatherHourly (
        @PrimaryKey(autoGenerate = true) var weatherCurrentlyID: Int? = null,
        @ColumnInfo(name = "icon") var icon: String,
        @ColumnInfo(name = "time") var time: Int,
        @ColumnInfo(name = "temperature") var temperature: Int,
        @ColumnInfo(name = "windSpeed") var windSpeed: Int
    ) {}

    @Entity (tableName = "weatherDaily")
    class WeatherDaily (
        @PrimaryKey(autoGenerate = true) var weatherCurrentlyID: Int? = null,
        @ColumnInfo(name = "icon") var icon: String,
        @ColumnInfo(name = "time") var time: Int,
        @ColumnInfo(name = "temperatureHigh") var temperatureHigh: Int,
        @ColumnInfo(name = "temperatureLow") var temperatureLow: Int,
        @ColumnInfo(name = "windSpeed") var windSpeed: Int
    ) {}

    data class WeatherPackage(val weatherCurrently: WeatherCurrently, val weatherHourly: ArrayList<WeatherHourly>,
                         val weatherDaily: ArrayList<WeatherDaily>)

    @Dao
    interface WeatherCurrentlyDao {
        @Insert
        fun insertAll(vararg weatherCurrently: WeatherCurrently)

        @Query("DELETE FROM weatherCurrently")
        fun deleteAll()

        @Query("SELECT * FROM weatherCurrently")
        fun getAll(): List<WeatherCurrently>
    }

    @Dao
    interface WeatherHourlyDao {
        @Insert
        fun insertAll(vararg weatherHourly: WeatherHourly)

        @Query("DELETE FROM weatherHourly")
        fun deleteAll()

        @Query("SELECT * FROM weatherHourly")
        fun getAll(): List<WeatherHourly>
    }

    @Dao
    interface WeatherDailyDao {
        @Insert
        fun insertAll(vararg weatherDaily: WeatherDaily)

        @Query("DELETE FROM weatherDaily")
        fun deleteAll()

        @Query("SELECT * FROM weatherDaily")
        fun getAll(): List<WeatherDaily>
    }

    @Database(entities = arrayOf(
        WeatherCurrently::class,
        WeatherHourly::class,
        WeatherDaily::class
    ), version = 1)
    abstract class WeatherDataBase : RoomDatabase() {
        abstract fun weatherCurrentlyDao() : WeatherCurrentlyDao
        abstract fun weatherHourlyDao() : WeatherHourlyDao
        abstract fun weatherDailyDao() : WeatherDailyDao
    }
}

fun creatorWeatherDB(applicationContext: Context, databaseName: String = "weatherDB") :WeatherDB.WeatherDataBase {
    return Room.databaseBuilder(applicationContext, WeatherDB.WeatherDataBase::class.java, databaseName).build()
}