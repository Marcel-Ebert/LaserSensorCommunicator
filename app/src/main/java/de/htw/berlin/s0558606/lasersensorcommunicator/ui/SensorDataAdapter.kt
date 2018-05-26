package de.htw.berlin.s0558606.lasersensorcommunicator.ui

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import de.htw.berlin.s0558606.lasersensorcommunicator.MapsActivity
import de.htw.berlin.s0558606.lasersensorcommunicator.R
import de.htw.berlin.s0558606.lasersensorcommunicator.UsbActivity
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sensor_list_item.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class SensorDataAdapter() : RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder>(), AnkoLogger {

    var dataList: List<SensorData> = listOf()

    private val clickListener = View.OnClickListener { view ->
        val item = view.tag as SensorData
        warn { "Clicked on SensorData: $item" }
//        startActivity(view.context, Intent(view.context, UsbActivity::class.java), null)
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
    }

    override fun getItemCount(): Int = dataList.size

    inner class SensorDataViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer


}