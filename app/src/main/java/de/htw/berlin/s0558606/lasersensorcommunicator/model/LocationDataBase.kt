package io.satoshipay.experiments.satoshipaywallet.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationDao

@Database(entities = arrayOf(Location::class), version = 1)
abstract class LocationDataBase : RoomDatabase() {

    abstract fun locationDao(): LocationDao


}
