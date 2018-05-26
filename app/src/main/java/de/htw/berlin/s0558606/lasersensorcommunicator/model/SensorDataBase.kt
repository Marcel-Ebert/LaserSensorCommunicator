package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Database(entities = arrayOf(SensorData::class), version = 1, exportSchema = false)
abstract class SensorDataBase : RoomDatabase(){

    abstract fun sensorDataDao(): SensorDataDao
}