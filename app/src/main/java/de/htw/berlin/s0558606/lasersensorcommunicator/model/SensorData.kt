package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Entity(tableName = "sensordata", foreignKeys = [
    (ForeignKey(entity = Location::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("location_id"),
            onDelete = CASCADE))])
@TypeConverters(DateConverter::class)
data class SensorData(val pm25: String,
                      val pm10: String,
                      val locationX: String,
                      val locationY: String) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "timestamp")
    var timestamp: Date = Date()

    fun getDateAsString(): String {
        return SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp)
    }
}