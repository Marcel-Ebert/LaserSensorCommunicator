package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val allLocations: LiveData<List<Location>>

    private val mRepository: LocationRepository = LocationRepository(application)

    init {
        allLocations = mRepository.allLocations
    }

    fun getAllLocationsSynchronous(): List<Location> {
        return mRepository.getAllLocationsSynchronous()
    }

    fun insert(location: Location) {
        mRepository.insert(location)
    }

    fun delete(location: Location) {
        mRepository.delete(location)
    }

    fun findLocationById(id: Long): Location {
        return mRepository.findLocationById(id)
    }

}
