package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class LatLngConverter {
    @TypeConverter
    fun fromString(value: String?): LatLng? {
        return value?.let {
            var array = value.split("|")
            LatLng(array.get(0).toDouble(), array.get(1).toDouble())
        }
    }

    @TypeConverter
    fun latLngToString(location: LatLng?): String? {
        return location?.let {
            "${location.latitude}|${location.longitude}"
        }
    }
}