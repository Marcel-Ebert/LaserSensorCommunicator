package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Dao
interface SensorDataDao {

    @Query("SELECT * FROM sensordata")
    fun getAllData(): Flowable<List<SensorData>>

    @Query("SELECT * FROM sensordata where id = :id")
    fun findDataById(id: Long): SensorData

    @Insert(onConflict = REPLACE)
    fun insertData(data: SensorData)

    @Update(onConflict = REPLACE)
    fun updateData(data: SensorData)

    @Delete
    fun deleteData(data: SensorData)
}