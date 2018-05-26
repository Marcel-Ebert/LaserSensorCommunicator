package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.*
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Created by Marcel Ebert S0558606 on 26.05.18.
 */

@Entity(tableName = "location")
@TypeConverters(LatLngConverter::class, DateConverter::class)
data class Location(
        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "location")
        var location: LatLng,

        @ColumnInfo(name = "pm25")
        var pm25: String = "0",

        @ColumnInfo(name = "pm10")
        var pm10: String = "0",

        @ColumnInfo(name = "start")
        var start: Date = Date(),

        @ColumnInfo(name = "end")
        var end: Date = Date()) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}