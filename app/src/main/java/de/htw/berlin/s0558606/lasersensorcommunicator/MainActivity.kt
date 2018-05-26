package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var mLocationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))



        locationAdapter = LocationAdapter()
        rv_locations.adapter = locationAdapter

        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        mLocationViewModel.allLocations?.observe(this, Observer<List<Location>> { locations ->
            // Update the cached copy of the words in the adapter.
            locations?.run {
                locationAdapter.dataList = locations
                locationAdapter.notifyDataSetChanged()
                warn { "onchanged called" }
            }
        })


        rv_locations.setHasFixedSize(true)
        rv_locations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        addNewLocation("Schöneweide", LatLng(10.0, 10.0))

    }

    private fun addNewLocation(name: String, location: LatLng) {
        warn { "Function called" }
        mLocationViewModel.insert(Location(name, location))
    }

}
