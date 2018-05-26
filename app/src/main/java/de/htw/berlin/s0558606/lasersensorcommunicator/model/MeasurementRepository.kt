package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

class MeasurementRepository
/**
 * dd a constructor that gets a handle to the database and initializes
 * the member variables.
 */
internal constructor(application: Application) {

    private val measurementDao: MeasurementDao?
    val allMeasurements: LiveData<List<Measurement>>

    init {
        val db = AppDatabase.getInstance(application)
        measurementDao = db.measurementDao()
        allMeasurements = measurementDao.getAllMeasurements()
    }

    fun insert(item: Measurement) {
        insertAsyncTask(measurementDao!!).execute(item)
    }

    fun getMeasurementsByLocationID(id: Long): LiveData<List<Measurement>> {
        return measurementDao?.findMeasurementsByLocationID(id)!!
    }

    fun getMeasurementID(id: Long): Measurement {
        return measurementDao?.findMeasurementById(id)!!
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: MeasurementDao) : AsyncTask<Measurement, Void, Void>() {

        override fun doInBackground(vararg params: Measurement): Void? {
            mAsyncTaskDao.insertMeasurement(params[0])
            return null
        }
    }


}
