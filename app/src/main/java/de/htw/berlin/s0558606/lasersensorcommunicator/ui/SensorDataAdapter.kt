package de.htw.berlin.s0558606.lasersensorcommunicator.ui

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import de.htw.berlin.s0558606.lasersensorcommunicator.R
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorDataViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sensor_list_item.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.warn


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class SensorDataAdapter(val context: AppCompatActivity) : RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder>(), AnkoLogger {

    var dataList: List<SensorData> = listOf()
    val sensorDataViewModel = ViewModelProviders.of(context).get(SensorDataViewModel::class.java)

    private val clickListener = View.OnClickListener { view ->
        val item = view.tag as SensorData
        warn { "Clicked on SensorData: $item" }
//        startActivity(view.context, Intent(view.context, SensorActivity::class.java), null)
    }



    private val longClickListener = View.OnLongClickListener { view ->
        val data = view.tag as SensorData
        showDeleteSensorDataDialog(data)
        true
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, type: Int): SensorDataViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.sensor_list_item, parent, false)
        return SensorDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorDataViewHolder, position: Int) {
        val data = dataList[position]
        holder.tv_pm25.text = "PM 2.5: ${data.pm25}"
        holder.tv_pm10.text = "PM 10: ${data.pm10}"
        holder.tv_timestamp.text = data.getDateAsString()

        // can be retrieved from click listener
        holder.itemView.tag = data
        holder.itemView.setOnClickListener(clickListener)
        holder.itemView.setOnLongClickListener(longClickListener)
    }

    override fun getItemCount(): Int = dataList.size

    private fun showDeleteSensorDataDialog(data: SensorData) {
        context.alert {
            title = "Delete this Data Item?"
            positiveButton(context.getString(android.R.string.yes)) { deleteSensorData(data) }
            negativeButton(context.getString(android.R.string.cancel)) { }
        }.show()
    }

    private fun deleteSensorData(data: SensorData){
        sensorDataViewModel.delete(data)
        warn { "Deleted SensorData: $data" }
    }


    inner class SensorDataViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer



}