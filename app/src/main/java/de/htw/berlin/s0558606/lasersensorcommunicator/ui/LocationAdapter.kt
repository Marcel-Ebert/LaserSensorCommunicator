package de.htw.berlin.s0558606.lasersensorcommunicator.ui

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import de.htw.berlin.s0558606.lasersensorcommunicator.ARG_ITEM_ID
import de.htw.berlin.s0558606.lasersensorcommunicator.ARG_ITEM_NAME
import de.htw.berlin.s0558606.lasersensorcommunicator.R
import de.htw.berlin.s0558606.lasersensorcommunicator.UsbActivity
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.location_list_item.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn


/**
 * Created by Marcel Ebert S0558606 on 20.05.18.
 */
class LocationAdapter() : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(), AnkoLogger {

    var dataList: List<Location> = listOf()

    private val clickListener = View.OnClickListener { view ->
        val item = view.tag as Location
        warn { "Clicked on Location: $item" }

        val intent = Intent(view.context, UsbActivity::class.java)
        intent.putExtra(ARG_ITEM_ID, item.id)
        intent.putExtra(ARG_ITEM_NAME, item.name)
        startActivity(view.context, intent, null)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, type: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.location_list_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val data = dataList.get(position)
        holder.tv_name.text = "${data.name}"
        holder.tv_location.text = "${data.location}"

        // can be retrieved from click listener
        holder.itemView.tag = data
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int = dataList.size

    inner class LocationViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer


}