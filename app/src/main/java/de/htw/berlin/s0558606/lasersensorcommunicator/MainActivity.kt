package de.htw.berlin.s0558606.lasersensorcommunicator

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.io.CSVWriter
import de.htw.berlin.s0558606.lasersensorcommunicator.model.AppDatabase
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasuringLocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasuringLocation
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk21.coroutines.onClick
import java.io.File


const val ARG_ITEM_ID = "item_id"
const val ARG_ITEM_NAME = "item_name"

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val REQUEST_PERMISSION_WRITE_STORAGE = 1000

    private lateinit var measuringLocationViewModel: MeasuringLocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = getString(R.string.title_activity_main)

        locationAdapter = LocationAdapter(this)
        rv_locations.adapter = locationAdapter

        measuringLocationViewModel = ViewModelProviders.of(this).get(MeasuringLocationViewModel::class.java)
        measuringLocationViewModel.allLocations?.observe(this, Observer<List<MeasuringLocation>> { locations ->
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
        measuringLocationViewModel.insert(MeasuringLocation(name, location))
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
        ActivityCompat.requestPermissions(this, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(), REQUEST_PERMISSION_WRITE_STORAGE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pb_export_data.visibility = View.VISIBLE
                    doAsync {
                        val filePathString = CSVWriter.getDatabaseContentCSVFileName(applicationContext)
                        val fileLocation = File(filePathString)
                        val fileUri = FileProvider.getUriForFile(applicationContext, "de.htw.berlin.s0558606.lasersensorcommunicator.fileprovider", fileLocation)

                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.data = Uri.parse("mailto:")
                        emailIntent.type = "text/plain"
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorData")
                        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)

                        uiThread {
                            pb_export_data.visibility = View.INVISIBLE
                            startActivity(Intent.createChooser(emailIntent, "Send collected sensordata as .csv"))

                        }
                    }

                } else {
                    Toast.makeText(applicationContext, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.getInstance(applicationContext).close()
    }

}
