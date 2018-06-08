package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.model.AppDatabase
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import org.jetbrains.anko.*
import de.htw.berlin.s0558606.lasersensorcommunicator.R.id.btn_show_locations
import de.htw.berlin.s0558606.lasersensorcommunicator.io.CSVWriter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.sdk21.coroutines.onClick
import java.io.File
import android.os.StrictMode




const val ARG_ITEM_ID = "item_id"
const val ARG_ITEM_NAME = "item_name"

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var locationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.title_activity_main)


        locationAdapter = LocationAdapter(this)
        rv_locations.adapter = locationAdapter

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        locationViewModel.allLocations?.observe(this, Observer<List<Location>> { locations ->
            // Update the cached copy of the words in the adapter.
            locations?.run {
                locationAdapter.dataList = locations
                locationAdapter.notifyDataSetChanged()
                warn { "onchanged called" }
            }
        })

        rv_locations.setHasFixedSize(true)
        rv_locations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        btn_show_locations.onClick {
            startActivity<ShowLocationsActivity>()
        }

    }

    private fun showAddLocationDialog() {
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
            positiveButton(getString(android.R.string.yes)) { addNewLocation(etLocation.text.toString(), LatLng(0.0, 0.0)) }
        }.show()
    }

    private fun addNewLocation(name: String, location: LatLng) {
        locationViewModel.insert(Location(name, location))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add_location -> {
                showAddLocationDialog()
                true
            }
            R.id.export_data -> {
                exportData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportData() {
        val fileLocation = File(Environment.getExternalStorageDirectory().absolutePath,
                CSVWriter.getDatabaseContentAsCSV(applicationContext))

        val path = Uri.fromFile(fileLocation)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorData")

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.getInstance(applicationContext).close()
    }

}
