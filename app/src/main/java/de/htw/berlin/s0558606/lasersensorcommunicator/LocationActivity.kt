package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Measurement
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasurementViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.MeasurementAdapter
import kotlinx.android.synthetic.main.activity_save_location.*
import kotlinx.android.synthetic.main.content_save_location.*
import org.jetbrains.anko.*

class LocationActivity : AppCompatActivity(), AnkoLogger {

    var locationID: Long = 0
    var locationName: String = ""

    private lateinit var measurementViewModel: MeasurementViewModel
    private lateinit var dataAdapter: MeasurementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_location)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            locationID = intent.extras.getLong(ARG_ITEM_ID)
            locationName = intent.extras.getString(ARG_ITEM_NAME)

            warn { "LocationID = ${locationID}" }

            dataAdapter = MeasurementAdapter(this)
            rv_measurements.adapter = dataAdapter

            measurementViewModel = ViewModelProviders.of(this).get(MeasurementViewModel::class.java)
            measurementViewModel.getMeasurementsByLocationID(locationID)?.observe(this, Observer<List<Measurement>> { data ->
                data?.run {
                    dataAdapter.dataList = data
                    dataAdapter.notifyDataSetChanged()
                }
            })


            supportActionBar?.title = locationName


            rv_measurements.setHasFixedSize(true)
            rv_measurements.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rv_measurements.addItemDecoration(DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL))

        }

    }

    private fun addNewMeasurement() {
        measurementViewModel.insert(Measurement(locationID = locationID))
    }

    private fun showAddMeasurementDialog() {
        alert {
            title = "Add new Measurement?"
            positiveButton(getString(android.R.string.yes)) { addNewMeasurement() }
            negativeButton(getString(android.R.string.cancel)) { }
        }.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.location_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add_measurement -> {
                showAddMeasurementDialog()
                true
            }
            R.id.set_position -> {
                val intent = Intent(applicationContext, SaveLocationActivity::class.java)

                intent.putExtra(ARG_ITEM_ID, locationID)
                intent.putExtra(ARG_ITEM_NAME, locationName)
                ContextCompat.startActivity(applicationContext, intent, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
