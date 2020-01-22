package com.eweather.databases

import androidx.room.*

class WeatherDataBase {
    @Entity (tableName = "weatherCurrently")
    class WeatherCurrently (
       @PrimaryKey(autoGenerate = true) var weatherCurrentlyID: Int? = null,
       @ColumnInfo(name = "icon") var icon: String,
       @ColumnInfo(name = "temperature") var temperature: Int,
       @ColumnInfo(name = "wind_speed") var windSpeed: Int,
       @ColumnInfo(name = "time_zone") var timeZone: String,
       @ColumnInfo(name = "summary_currently") var summaryCurrently: String,
       @ColumnInfo(name = "summary_hourly") var summaryHourly: String
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
        @ColumnInfo(name = "temperature_high") var temperatureHigh: Int,
        @ColumnInfo(name = "temperature_low") var temperatureLow: Int,
        @ColumnInfo(name = "windSpeed") var windSpeed: Int
    ) {}

    @Entity (tableName = "places")
    class Places (
        @PrimaryKey(autoGenerate = true) var weatherCurrentlyID: Int? = null,
        @ColumnInfo(name = "latitude") var latitude: Long,
        @ColumnInfo(name = "longitude") var longitude: Long,
        @ColumnInfo(name = "place_name") var placeName: String
    ) {}
    data class WeatherPackage(val weatherCurrently: WeatherCurrently, val weatherHourly: List<WeatherHourly>,
                         val weatherDaily: List<WeatherDaily>)

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

    @Dao
    interface PlacesDao {
        @Insert
        fun insertAll(vararg places: Places)

        @Delete
        fun delete(vararg places: Places)

        @Query("SELECT * FROM places")
        fun getAll(): List<Places>
    }

    @Database(entities = arrayOf(
        WeatherCurrently::class,
        WeatherHourly::class,
        WeatherDaily::class,
        Places::class
    ), version = 1, exportSchema = false)
    abstract class WeatherDB : RoomDatabase() {
        abstract fun weatherCurrentlyDao(): WeatherCurrentlyDao
        abstract fun weatherHourlyDao(): WeatherHourlyDao
        abstract fun weatherDailyDao(): WeatherDailyDao
        abstract fun placesDao(): PlacesDao
    }
}