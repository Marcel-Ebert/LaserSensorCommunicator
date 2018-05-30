package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class SensorDataViewModel(application: Application) : AndroidViewModel(application) {

    val allData: LiveData<List<SensorData>>

    private val mRepository: SensorDataRepository = SensorDataRepository(application)

    init {
        allData = mRepository.allData
    }

    fun insert(data: SensorData) {
        mRepository.insert(data)
    }

    fun delete(data: SensorData) {
        mRepository.delete(data)
    }

    fun getDataByMeasurementID(id: Long): LiveData<List<SensorData>> {
        return mRepository.getDataByMeasurementID(id)
    }

}
