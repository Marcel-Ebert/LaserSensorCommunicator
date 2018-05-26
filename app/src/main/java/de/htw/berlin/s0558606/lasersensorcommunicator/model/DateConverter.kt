package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.TypeConverter
import java.util.*


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.let { date.time }
    }
}