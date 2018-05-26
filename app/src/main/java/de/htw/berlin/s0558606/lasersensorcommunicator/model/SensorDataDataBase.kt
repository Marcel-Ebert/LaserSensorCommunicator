package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.room.Room


/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Database(entities = arrayOf(SensorData::class), version = 1, exportSchema = false)
abstract class SensorDataDataBase : RoomDatabase() {

    abstract fun sensorDataDao(): SensorDataDao

    companion object {
        private var INSTANCE: SensorDataDataBase? = null
        @JvmStatic fun getDatabase(context: Context): SensorDataDataBase {
            if (INSTANCE == null) {
                INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext, SensorDataDataBase::class.java).allowMainThreadQueries().build()
            }
            return INSTANCE!!
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}