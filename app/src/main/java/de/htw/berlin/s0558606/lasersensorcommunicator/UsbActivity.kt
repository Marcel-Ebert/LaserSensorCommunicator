package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import de.htw.berlin.s0558606.lasersensorcommunicator.model.Location
import de.htw.berlin.s0558606.lasersensorcommunicator.model.LocationViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorDataViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.serial.UsbService
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.LocationAdapter
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.SensorDataAdapter
import kotlinx.android.synthetic.main.content_usb.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.jetbrains.anko.warn

const val ARG_ITEM_ID = "location_id"
const val ARG_ITEM_NAME = "location_name"

class UsbActivity : AppCompatActivity(), AnkoLogger {

    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbService.ACTION_USB_PERMISSION_GRANTED
                -> toast("USB Ready")
                UsbService.ACTION_USB_PERMISSION_NOT_GRANTED
                -> toast("USB Permission not granted")
                UsbService.ACTION_NO_USB
                -> toast("No USB connected")
                UsbService.ACTION_USB_DISCONNECTED
                -> toast("USB disconnected")
                UsbService.ACTION_USB_NOT_SUPPORTED
                -> toast("USB device not supported")
            }
        }
    }

    private val usbConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, arg1: IBinder) {
            usbService = (arg1 as UsbService.UsbBinder).service
            usbService?.setHandler(mHandler)
            warn { "usb service connected" }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
        }
    }

    private var usbService: UsbService? = null
    private lateinit var mHandler: MyHandler


    private lateinit var mSensorDataViewModel: SensorDataViewModel
    private lateinit var dataAdapter: SensorDataAdapter

    private var locationID: Long = 0
    private var locationName: String = "Location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)

        mHandler = MyHandler(this)

        if (savedInstanceState == null) {
            locationID = intent.extras.getLong(ARG_ITEM_ID)
            locationName = intent.extras.getString(ARG_ITEM_NAME)
            warn { "LocationID = ${locationID}" }

            dataAdapter = SensorDataAdapter()
            rv_sensor_items.adapter = dataAdapter

            mSensorDataViewModel = ViewModelProviders.of(this).get(SensorDataViewModel::class.java)
            mSensorDataViewModel.getDataByLocationID(locationID)?.observe(this, Observer<List<SensorData>> { data ->
                // Update the cached copy of the words in the adapter.
                data?.run {
                    dataAdapter.dataList = data
                    dataAdapter.notifyDataSetChanged()
                    warn { "onchanged called" }
                }
            })


            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.title = locationName


            rv_sensor_items.setHasFixedSize(true)
            rv_sensor_items.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            addNewSensorData("0", "10")
        }
    }

    private fun addNewSensorData(pm25: String, pm10: String) {
        warn { "Function called" }
        mSensorDataViewModel.insert(SensorData(pm25, pm10, locationID))
    }


    override fun onResume() {
        super.onResume()
        setFilters()  // Start listening notifications from UsbService
        startService(UsbService::class.java, usbConnection, null) // Start UsbService(if it was not started before) and Bind it
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mUsbReceiver)
        unbindService(usbConnection)
    }

    private fun startService(service: Class<*>, serviceConnection: ServiceConnection, extras: Bundle?) {
        if (!UsbService.SERVICE_CONNECTED) {
            val startService = Intent(this, service)
            if (extras != null && !extras.isEmpty) {
                val keys = extras.keySet()
                for (key in keys) {
                    val extra = extras.getString(key)
                    startService.putExtra(key, extra)
                }
            }
            startService(startService)
        }
        val bindingIntent = Intent(this, service)
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setFilters() {
        val filter = IntentFilter()
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED)
        filter.addAction(UsbService.ACTION_NO_USB)
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED)
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED)
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)
        registerReceiver(mUsbReceiver, filter)
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler(activity: UsbActivity) : Handler(), AnkoLogger {
        private val mActivity: UsbActivity = activity

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UsbService.MESSAGE_FROM_SERIAL_PORT ->
                    mActivity.textView2?.text = bytesToHex(msg.obj as ByteArray)
                UsbService.CTS_CHANGE ->
                    Toast.makeText(mActivity, "CTS_CHANGE", Toast.LENGTH_LONG).show()
                UsbService.DSR_CHANGE ->
                    Toast.makeText(mActivity, "DSR_CHANGE", Toast.LENGTH_LONG).show()

            }
        }

        fun bytesToHex(bytes: ByteArray): String {
            var string = ""
            for (b in bytes) {
                val st = String.format("%02X", b)
                string = string.plus(st)
            }
            return string
        }
    }
}
