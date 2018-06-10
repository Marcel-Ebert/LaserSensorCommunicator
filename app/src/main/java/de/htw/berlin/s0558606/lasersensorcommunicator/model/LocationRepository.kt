package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

class LocationRepository
/**
 * dd a constructor that gets a handle to the database and initializes
 * the member variables.
 */
internal constructor(application: Application) {

    private val locationDao: LocationDao?
    val allLocations: LiveData<List<MeasuringLocation>>

    init {
        val db = AppDatabase.getInstance(application)
        locationDao = db.locationDao()
        allLocations = locationDao.getAllLocations()
    }

    fun getAllLocationsSynchronous(): List<MeasuringLocation> {
        return locationDao!!.getAllLocationsSynchronous()
    }

    fun insert(item: MeasuringLocation) {
        insertAsyncTask(locationDao!!).execute(item)
    }

    fun delete(item: MeasuringLocation) {
        locationDao?.deleteLocation(item)
    }

    fun findLocationById(id: Long): MeasuringLocation {
        return locationDao?.findLocationById(id)!!
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: LocationDao) : AsyncTask<MeasuringLocation, Void, Void>() {

        override fun doInBackground(vararg params: MeasuringLocation): Void? {
            mAsyncTaskDao.insertLocation(params[0])
            return null
        }
    }

    private class findLocationAsyncTask internal constructor(private val mAsyncTaskDao: LocationDao) : AsyncTask<Long, Void, MeasuringLocation>() {
        override fun doInBackground(vararg params: Long?): MeasuringLocation {
            return mAsyncTaskDao.findLocationById(params[0]!!)
        }

    }

}
