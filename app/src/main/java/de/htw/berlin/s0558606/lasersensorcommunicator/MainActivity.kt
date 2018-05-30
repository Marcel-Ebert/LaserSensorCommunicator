package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*


const val ARG_ITEM_ID = "item_id"
const val ARG_ITEM_NAME = "item_name"

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var mLocationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.title_activity_main)


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

    }

    private fun showAddLocationdialog() {
        alert {
            title = "Add new Location"
            lateinit var etLocation: EditText
            customView {
                linearLayout {
                    textView("Enter location name:")
                    etLocation = editText("")
                    padding = dip(16)
                }
            }
            positiveButton("Cool") { addNewLocation(etLocation.text.toString(), LatLng(0.0, 0.0)) }
        }.show()
    }

    private fun addNewLocation(name: String, location: LatLng) {
        mLocationViewModel.insert(Location(name, location))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add_location -> {
                showAddLocationdialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
