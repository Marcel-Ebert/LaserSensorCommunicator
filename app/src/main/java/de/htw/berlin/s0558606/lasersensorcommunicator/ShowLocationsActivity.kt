package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import org.jetbrains.anko.startActivity

class ShowLocationsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var locationViewModel: LocationViewModel

    private var showOnlyOneLocation: Boolean = false
    private lateinit var location: Location

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

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        if (intent.extras != null) {
            showOnlyOneLocation = true
            val id = intent.getLongExtra(ARG_ITEM_ID, 0)
            location = locationViewModel.findLocationById(id)

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
        val list = locationViewModel.getAllLocationsSynchronous()

        list.forEach {
            map.addMarker(MarkerOptions().position(it.location).title(it.name)).tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.location, DEFAULT_ZOOM.toFloat()))

        }
    }
}
