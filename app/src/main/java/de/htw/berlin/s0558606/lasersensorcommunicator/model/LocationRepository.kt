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
    val allLocations: LiveData<List<Location>>

    init {
        val db = AppDatabase.getInstance(application)
        locationDao = db.locationDao()
        allLocations = locationDao.getAllLocations()
    }

    fun getAllLocationsSynchronous(): List<Location> {
        return locationDao!!.getAllLocationsSynchronous()
    }

    fun insert(item: Location) {
        insertAsyncTask(locationDao!!).execute(item)
    }

    fun delete(item: Location) {
        locationDao?.deleteLocation(item)
    }

    fun findLocationById(id: Long): Location {
        return locationDao?.findLocationById(id)!!
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: LocationDao) : AsyncTask<Location, Void, Void>() {

        override fun doInBackground(vararg params: Location): Void? {
            mAsyncTaskDao.insertLocation(params[0])
            return null
        }
    }

    private class findLocationAsyncTask internal constructor(private val mAsyncTaskDao: LocationDao) : AsyncTask<Long, Void, Location>() {
        override fun doInBackground(vararg params: Long?): Location {
            return mAsyncTaskDao.findLocationById(params[0]!!)
        }

    }

}
