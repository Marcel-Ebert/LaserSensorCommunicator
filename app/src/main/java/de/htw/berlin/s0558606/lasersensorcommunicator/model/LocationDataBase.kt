package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.room.Room


/**
 * Created by Marcel Ebert S0558606 on 19.05.18.
 */

@Database(entities = arrayOf(Location::class), version = 1, exportSchema = false)
abstract class LocationDataBase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        private var INSTANCE: LocationDataBase? = null
        @JvmStatic fun getDatabase(context: Context): LocationDataBase {
            if (INSTANCE == null) {
                INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext, LocationDataBase::class.java).allowMainThreadQueries().build()
            }
            return INSTANCE!!
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}