package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class MeasurementViewModel(application: Application) : AndroidViewModel(application) {

    val allMeasurements: LiveData<List<Measurement>>

    private val mRepository: MeasurementRepository = MeasurementRepository(application)

    init {
        allMeasurements = mRepository.allMeasurements
    }

    fun insert(measurement: Measurement) {
        mRepository.insert(measurement)
    }

    fun getMeasurementByID(id: Long): Measurement {
        return mRepository.getMeasurementID(id)
    }

    fun getMeasurementsByLocationID(id: Long): LiveData<List<Measurement>> {
        return mRepository.getMeasurementsByLocationID(id)
    }

}
