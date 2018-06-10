package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val allLocations: LiveData<List<MeasuringLocation>>

    private val mRepository: LocationRepository = LocationRepository(application)

    init {
        allLocations = mRepository.allLocations
    }

    fun getAllLocationsSynchronous(): List<MeasuringLocation> {
        return mRepository.getAllLocationsSynchronous()
    }

    fun insert(location: MeasuringLocation) {
        mRepository.insert(location)
    }

    fun delete(location: MeasuringLocation) {
        mRepository.delete(location)
    }

    fun findLocationById(id: Long): MeasuringLocation {
        return mRepository.findLocationById(id)
    }

}
