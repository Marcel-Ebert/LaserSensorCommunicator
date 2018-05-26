package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.SensorDataAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val locationList: MutableList<Location> = mutableListOf()

    private lateinit var mLocationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        rv_locations.setHasFixedSize(true)
        rv_locations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = LocationAdapter(locationList.toList())
        rv_locations.adapter = adapter

        addItemsToList(3)
        rv_locations.adapter.notifyDataSetChanged()

        mLocationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        mLocationViewModel.allLocations?.observe(this, object : Observer<List<Location>> {
            override fun onChanged(locations: List<Location>?) {
                // Update the cached copy of the words in the adapter.
                locations?.run { adapter.dataList = locations }
            }
        })
    }

    private fun addItemsToList(amount: Int) {
        warn { "Function called" }
    }

}
