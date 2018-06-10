package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class MeasurementViewModel(application: Application) : AndroidViewModel(application) {

    val allMeasurements: LiveData<List<Measurement>>

    private val repository: MeasurementRepository = MeasurementRepository(application)

    init {
        allMeasurements = repository.allMeasurements
    }

    fun insert(measurement: Measurement) {
        repository.insert(measurement)
    }

    fun update(measurement: Measurement){
        repository.update(measurement)
    }

    fun delete(measurement: Measurement) {
        repository.delete(measurement)
    }

    fun getMeasurementByID(id: Long): Measurement {
        return repository.getMeasurementID(id)
    }

    fun getMeasurementsByLocationID(id: Long): LiveData<List<Measurement>> {
        return repository.getMeasurementsByLocationID(id)
    }

}
