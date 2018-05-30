package de.htw.berlin.s0558606.lasersensorcommunicator.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.substring
import android.view.LayoutInflater
import android.view.View
import de.htw.berlin.s0558606.lasersensorcommunicator.ARG_ITEM_ID
import de.htw.berlin.s0558606.lasersensorcommunicator.R
import de.htw.berlin.s0558606.lasersensorcommunicator.SensorActivity
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Measurement
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasurementViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.measurement_list_item.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class MeasurementAdapter() : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>(), AnkoLogger {

    var dataList: List<Measurement> = listOf()

    private val clickListener = View.OnClickListener { view ->
        val item = view.tag as Measurement
        warn { "Clicked on Measurement: $item" }

        val intent = Intent(view.context, SensorActivity::class.java)
        intent.putExtra(ARG_ITEM_ID, item.id)
        startActivity(view.context, intent, null)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, type: Int): MeasurementViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.measurement_list_item, parent, false)
        return MeasurementViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val data = dataList.get(position)
        holder.tv_starttime.text = "${data.getStartAsString()}"
        holder.tv_endtime.text = "${data.getEndAsString()}"
        holder.tv_pm25.text = "%.4f".format(data.pm25.toDouble())
        holder.tv_pm10.text = "%.4f".format(data.pm10.toDouble())
        holder.tv_id.text = "# ${data.id}"

        // can be retrieved from click listener
        holder.itemView.tag = data
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int = dataList.size

    inner class MeasurementViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer


}