package de.htw.berlin.s0558606.lasersensorcommunicator.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class MeasuringLocationViewModel(application: Application) : AndroidViewModel(application) {

    val allLocations: LiveData<List<MeasuringLocation>>

    private val repository: MeasuringLocationRepository = MeasuringLocationRepository(application)

    init {
        allLocations = repository.allLocations
    }

    fun getAllLocationsSynchronous(): List<MeasuringLocation> {
        return repository.getAllLocationsSynchronous()
    }

    fun insert(location: MeasuringLocation) {
        repository.insert(location)
    }

    fun update(location: MeasuringLocation) {
        repository.update(location)
    }

    fun delete(location: MeasuringLocation) {
        repository.delete(location)
    }

    fun findLocationById(id: Long): MeasuringLocation {
        return repository.findLocationById(id)
    }

}
