package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = arrayOf(Location::class, SensorData::class), version = 5)
abstract class AppDatabase : RoomDatabase() {

    /*//////////////////////////////////////////////////////////
    // ABSTRACT METHODS
    *///////////////////////////////////////////////////////////
    abstract fun locationDao(): LocationDao
    abstract fun sensorDataDao(): SensorDataDao


    /*//////////////////////////////////////////////////////////
    // COMPANION OBJECT
    *///////////////////////////////////////////////////////////
    companion object {
        val DB_NAME = "data_db"
        var dbInstance: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase? {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder<AppDatabase>(context.applicationContext, AppDatabase::class.java, DB_NAME).build()
            }
            return dbInstance
        }
    }

}