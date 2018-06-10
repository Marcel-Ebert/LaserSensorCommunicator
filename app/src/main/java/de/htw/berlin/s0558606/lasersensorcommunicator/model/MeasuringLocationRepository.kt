package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

class MeasuringLocationRepository
/**
 * dd a constructor that gets a handle to the database and initializes
 * the member variables.
 */
internal constructor(application: Application) {

    private val measuringLocationDao: MeasuringLocationDao?
    val allLocations: LiveData<List<MeasuringLocation>>

    init {
        val db = AppDatabase.getInstance(application)
        measuringLocationDao = db.locationDao()
        allLocations = measuringLocationDao.getAllLocations()
    }

    fun getAllLocationsSynchronous(): List<MeasuringLocation> {
        return measuringLocationDao!!.getAllLocationsSynchronous()
    }

    fun insert(item: MeasuringLocation) {
        insertAsyncTask(measuringLocationDao!!).execute(item)
    }

    fun delete(item: MeasuringLocation) {
        measuringLocationDao?.deleteLocation(item)
    }

    fun findLocationById(id: Long): MeasuringLocation {
        return measuringLocationDao?.findLocationById(id)!!
    }

    private class insertAsyncTask internal constructor(private val asyncTaskDao: MeasuringLocationDao) : AsyncTask<MeasuringLocation, Void, Void>() {

        override fun doInBackground(vararg params: MeasuringLocation): Void? {
            asyncTaskDao.insertLocation(params[0])
            return null
        }
    }

}
