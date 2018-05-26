package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

class SensorDataRepository
/**
 * Add a constructor that gets a handle to the database and initializes
 * the member variables.
 */
internal constructor(application: Application) {

    private val sensorDataDao: SensorDataDao?
    val allData: LiveData<List<SensorData>>

    init {
        val db = AppDatabase.getInstance(application)
        sensorDataDao = db.sensorDataDao()
        allData = sensorDataDao.getAllData()
    }

    fun insert(item: SensorData) {
        insertAsyncTask(sensorDataDao!!).execute(item)
    }

    fun getDataByMeasurementID(id: Long): LiveData<List<SensorData>> {
        return sensorDataDao?.findDataByMeasurementId(id)!!
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: SensorDataDao) : AsyncTask<SensorData, Void, Void>() {

        override fun doInBackground(vararg params: SensorData): Void? {
            mAsyncTaskDao.insertData(params[0])
            return null
        }
    }

}
