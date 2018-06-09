package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import android.os.Build.VERSION_CODES.M
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Entity(tableName = "sensordata", foreignKeys = [
    (ForeignKey(entity = Measurement::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("measurement_id"),
            onDelete = CASCADE))])
@TypeConverters(DateConverter::class)
data class SensorData(var pm25: String = "",
                      var pm10: String = "",

                      @ColumnInfo(name = "measurement_id")
                      var measurementID: Long = 0) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "timestamp")
    var timestamp: Date = Date()

    @Ignore
    val unit: String = "µg/m³"

    @Ignore
    var initializedCorrectly = true

    constructor(bytes: ByteArray) : this() {
        populateDataFromBytes(bytes)
    }

    private fun populateDataFromBytes(bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            val pm25lowbyte = bytes[2]
            val pm25highbyte = bytes[3]
            val pm25d: Double = Math.abs(((pm25highbyte * 256.0) + pm25lowbyte) / 10.0)

            val pm10lowbyte = bytes[4]
            val pm10highbyte = bytes[5]
            val pm10d: Double = Math.abs(((pm10highbyte * 256.0) + pm10lowbyte) / 10.0)

            pm25 = pm25d.toString()
            pm10 = pm10d.toString()
        } else {
            initializedCorrectly = false
        }
    }


    fun getDateAsString(): String {
        return SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp)
    }
}