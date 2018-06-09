package de.htw.berlin.s0558606.lasersensorcommunicator

import android.Manifest
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
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import android.content.pm.PackageManager
import android.support.v4.content.FileProvider
import android.view.View


const val ARG_ITEM_ID = "item_id"
const val ARG_ITEM_NAME = "item_name"

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var locationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        ActivityCompat.requestPermissions(this, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(), 1)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pb_export_data.visibility = View.VISIBLE
                    doAsync {
                        val filePathString = CSVWriter.getDatabaseContentAsCSV(applicationContext)

                        val fileLocation = File("", filePathString)

                        val path = FileProvider.getUriForFile(applicationContext, "de.htw.berlin.s0558606.lasersensorcommunicator.fileprovider", fileLocation)

                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.data = Uri.parse("mailto:")
                        emailIntent.type = "text/plain"

                        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SensorData")

                        uiThread {
                            pb_export_data.visibility = View.INVISIBLE
                            startActivity(Intent.createChooser(emailIntent, "Send collected sensordata as .csv ..."))

                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(applicationContext, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.getInstance(applicationContext).close()
    }

}
