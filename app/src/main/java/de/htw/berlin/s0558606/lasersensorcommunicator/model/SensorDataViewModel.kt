package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class SensorDataViewModel(application: Application) : AndroidViewModel(application) {

    val allData: LiveData<List<SensorData>>

    private val repository: SensorDataRepository = SensorDataRepository(application)

    init {
        allData = repository.allData
    }

    fun insert(data: SensorData) {
        repository.insert(data)
    }

    fun delete(data: SensorData) {
        repository.delete(data)
    }

    fun getDataByMeasurementID(id: Long): LiveData<List<SensorData>> {
        return repository.getDataByMeasurementID(id)
    }

}
