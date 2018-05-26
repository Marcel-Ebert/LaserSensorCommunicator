package de.htw.berlin.s0558606.lasersensorcommunicator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.SensorDataAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val sensorDataList: MutableList<SensorData> = mutableListOf(SensorData("0", "2", "2.0", "3.0"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        rv_sensor_items.setHasFixedSize(true)
        rv_sensor_items.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_sensor_items.adapter = SensorDataAdapter(sensorDataList)

        addItemsToList(3)
        rv_sensor_items.adapter.notifyDataSetChanged()
    }

    private fun addItemsToList(amount: Int) {
        warn { "Function called" }
        sensorDataList.add(SensorData("1", "2", "2.0", "3.0"))
        sensorDataList.add(SensorData("0", "2", "2.0", "3.0"))
        sensorDataList.add(SensorData("0", "2", "2.0", "3.0"))

    }

}
