package de.htw.berlin.s0558606.lasersensorcommunicator.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import de.htw.berlin.s0558606.lasersensorcommunicator.*
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasuringLocation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.location_list_item.*
import kotlinx.android.synthetic.main.measurement_list_item.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.warn


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class LocationAdapter(val context: AppCompatActivity) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(), AnkoLogger {

    var dataList: List<MeasuringLocation> = listOf()
    val locationViewModel = ViewModelProviders.of(context).get(LocationViewModel::class.java)


    private val clickListener = View.OnClickListener { view ->
        val item = view.tag as MeasuringLocation
        warn { "Clicked on Location: $item" }

        val intent = Intent(view.context, LocationActivity::class.java)
        intent.putExtra(ARG_ITEM_ID, item.id)
        intent.putExtra(ARG_ITEM_NAME, item.name)
        startActivity(view.context, intent, null)
    }

    private val longClickListener = View.OnLongClickListener { view ->
        val item = view.tag as MeasuringLocation
        showDeleteLocationDialog(item)
        true
    }

    private fun showDeleteLocationDialog(item: MeasuringLocation) {
        context.alert {
            title = "Delete this Location?"
            positiveButton(context.getString(android.R.string.yes)) { deleteLocation(item) }
            negativeButton(context.getString(android.R.string.cancel)) { }
        }.show()
    }

    private fun deleteLocation(item: MeasuringLocation) {
        locationViewModel.delete(item)
        warn { "Deleted Location: $item" }
    }


    override fun onCreateViewHolder(parent: android.view.ViewGroup, type: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.location_list_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val data = dataList.get(position)
        holder.tv_name.text = "${data.name}"

        // can be retrieved from click listener
        holder.itemView.tag = data
        holder.itemView.setOnClickListener(clickListener)
        holder.itemView.setOnLongClickListener(longClickListener)
    }

    override fun getItemCount(): Int = dataList.size

    inner class LocationViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer


}