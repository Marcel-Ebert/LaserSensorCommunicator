package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    fun getAllMeasurements(): LiveData<List<Measurement>>


    @Query("SELECT * FROM measurement")
    fun getAllMeasurementsSynchronous(): List<Measurement>

    @Query("SELECT * FROM measurement where id = :id")
    fun findMeasurementById(id: Long): Measurement

    @Query("SELECT * FROM measurement where location_id = :id")
    fun findMeasurementsByLocationID(id: Long): LiveData<List<Measurement>>


    @Query("SELECT * FROM measurement where location_id = :id")
    fun findMeasurementsByLocationIDSynchronous(id: Long): List<Measurement>

    @Insert(onConflict = REPLACE)
    fun insertMeasurement(measurement: Measurement)

    @Update(onConflict = REPLACE)
    fun updateMeasurement(measurement: Measurement)

    @Delete
    fun deleteMeasurement(measurement: Measurement)
}