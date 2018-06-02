package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.content_maps.*
import org.jetbrains.anko.sdk21.coroutines.onClick
import org.jetbrains.anko.toast


/**
 * An activity that displays a map showing the place at the device's current location.
 */
class SaveLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mCameraPosition: CameraPosition? = null

    // The entry point to the Fused Location Provider.
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private var mLocationPermissionGranted: Boolean = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var mLastKnownLocation: Location? = null

    private var locationID: Long = 0
    private lateinit var locationName: String
    private lateinit var locationViewModel: LocationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable<Location>(KEY_LOCATION)
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            locationID = savedInstanceState.getLong(ARG_ITEM_ID)
            locationName = savedInstanceState.getString(ARG_ITEM_NAME)
        } else {
            locationID = intent.extras.getLong(ARG_ITEM_ID)
            locationName = intent.extras.getString(ARG_ITEM_NAME)
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Save Location for $locationName"

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)


        btn_save_location.onClick {
            val location = locationViewModel.findLocationById(locationID)
            val targetlocation = LatLng(mLastKnownLocation?.latitude
                    ?: 0.0, mLastKnownLocation?.longitude ?: 0.0)
            location.location = targetlocation
            locationViewModel.insert(location)
            toast("Location saved!")
        }

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap!!.cameraPosition)
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
            outState.putLong(ARG_ITEM_ID, locationID)
            outState.putString(ARG_ITEM_NAME, locationName)
            super.onSaveInstanceState(outState)
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override// Return null here, so that getInfoContents() is called next.
            fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(R.layout.custom_info_contents,
                        findViewById(R.id.map) as FrameLayout, false)

                val title = infoWindow.findViewById(R.id.title) as TextView
                title.text = marker.title

                val snippet = infoWindow.findViewById(R.id.snippet) as TextView
                snippet.text = marker.snippet

                return infoWindow
            }
        })

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.getLastLocation()
                locationResult.addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(mLastKnownLocation!!.getLatitude(),
                                        mLastKnownLocation!!.getLongitude()), DEFAULT_ZOOM.toFloat()))
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap!!.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap!!.uiSettings.isMyLocationButtonEnabled = false
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap!!.isMyLocationEnabled = false
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    companion object {

        private val TAG = SaveLocationActivity::class.java.simpleName
        private val DEFAULT_ZOOM = 15
        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private val KEY_CAMERA_POSITION = "camera_position"
        private val KEY_LOCATION = "location"

    }
}