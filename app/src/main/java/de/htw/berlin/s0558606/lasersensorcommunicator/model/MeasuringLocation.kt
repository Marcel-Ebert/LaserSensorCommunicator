package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

/**
 * Created by Marcel Ebert S0558606 on 26.05.18.
 */

@Entity(tableName = "measuring_location")
@TypeConverters(LatLngConverter::class, DateConverter::class)
data class MeasuringLocation(
        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "location")
        var location: LatLng) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}