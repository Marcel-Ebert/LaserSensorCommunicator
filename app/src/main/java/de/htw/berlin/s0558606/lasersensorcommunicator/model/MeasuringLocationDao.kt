package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.OnConflictStrategy.REPLACE

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Dao
interface MeasuringLocationDao {

    @Query("SELECT * FROM measuring_location")
    fun getAllLocations(): LiveData<List<MeasuringLocation>>

    @Query("SELECT * FROM measuring_location")
    fun getAllLocationsSynchronous(): List<MeasuringLocation>

    @Query("SELECT * FROM measuring_location where id = :id")
    fun findLocationById(id: Long): MeasuringLocation

    @Insert(onConflict = IGNORE)
    fun insertLocation(location: MeasuringLocation)

    @Update(onConflict = REPLACE)
    fun updateLocation(location: MeasuringLocation)

    @Delete
    fun deleteLocation(location: MeasuringLocation)
}