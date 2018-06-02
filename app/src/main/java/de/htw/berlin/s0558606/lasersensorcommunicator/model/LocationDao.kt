package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAllLocations(): LiveData<List<Location>>

    @Query("SELECT * FROM location")
    fun getAllLocationsSynchronous(): List<Location>

    @Query("SELECT * FROM location where id = :id")
    fun findLocationById(id: Long): Location

    @Insert(onConflict = REPLACE)
    fun insertLocation(location: Location)

    @Update(onConflict = REPLACE)
    fun updateLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)
}