package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasuringLocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasuringLocation
import org.jetbrains.anko.startActivity

class ShowLocationsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var measuringLocationViewModel: MeasuringLocationViewModel

    private var showOnlyOneLocation: Boolean = false
    private lateinit var location: MeasuringLocation

    companion object {

        private val TAG = ShowLocationsActivity::class.java.simpleName
        private val DEFAULT_ZOOM = 10
        private val DEEP_ZOOM = 15

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_locations)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        measuringLocationViewModel = ViewModelProviders.of(this).get(MeasuringLocationViewModel::class.java)

        if (intent.extras != null) {
            showOnlyOneLocation = true
            val id = intent.getLongExtra(ARG_ITEM_ID, 0)
            location = measuringLocationViewModel.findLocationById(id)

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (showOnlyOneLocation) {
            addLocationToMap()
        } else {
            addLocationsToMap()

            map.setOnInfoWindowClickListener {
                val id = it.tag as Long
                val name = it.title

                // start location activity with information from clicked marker
                startActivity<LocationActivity>(ARG_ITEM_ID to id, ARG_ITEM_NAME to name)

            }
        }
    }

    private fun addLocationToMap() {
        map.addMarker(MarkerOptions().position(location.location).title("${location.name}"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.location, DEEP_ZOOM.toFloat()))
    }

    private fun addLocationsToMap() {
        val list = measuringLocationViewModel.getAllLocationsSynchronous()

        list.forEach {
            map.addMarker(MarkerOptions().position(it.location).title(it.name)).tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.location, DEFAULT_ZOOM.toFloat()))

        }
    }
}
