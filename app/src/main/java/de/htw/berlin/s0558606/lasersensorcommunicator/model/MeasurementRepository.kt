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

    fun update(item: Measurement) {
        updateAsyncTask(measurementDao!!).execute(item)
    }

    fun delete(item: Measurement){
        measurementDao?.deleteMeasurement(item)
    }

    fun getMeasurementsByLocationID(id: Long): LiveData<List<Measurement>> {
        return measurementDao?.findMeasurementsByLocationID(id)!!
    }

    fun getMeasurementID(id: Long): Measurement {
        return measurementDao?.findMeasurementById(id)!!
    }

    private class insertAsyncTask internal constructor(private val asyncTaskDao: MeasurementDao) : AsyncTask<Measurement, Void, Void>() {

        override fun doInBackground(vararg params: Measurement): Void? {
            asyncTaskDao.insertMeasurement(params[0])
            return null
        }
    }

    private class updateAsyncTask internal constructor(private val asyncTaskDao: MeasurementDao) : AsyncTask<Measurement, Void, Void>() {

        override fun doInBackground(vararg params: Measurement): Void? {
            asyncTaskDao.updateMeasurement(params[0])
            return null
        }
    }


}
