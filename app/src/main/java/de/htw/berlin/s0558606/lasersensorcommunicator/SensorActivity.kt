package de.htw.berlin.s0558606.lasersensorcommunicator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasurementViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorDataViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.serial.UsbService
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.SensorDataAdapter
import kotlinx.android.synthetic.main.content_usb.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk21.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.warn

class SensorActivity : AppCompatActivity(), AnkoLogger {

    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
//                UsbService.ACTION_USB_PERMISSION_GRANTED
//                -> toast("USB Ready")
                UsbService.ACTION_USB_PERMISSION_NOT_GRANTED
                -> toast("USB Permission not granted")
//                UsbService.ACTION_NO_USB
//                -> toast("No USB connected")
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
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
        }
    }

    private var usbService: UsbService? = null
    private lateinit var mHandler: MyHandler

    private var serviceRunning: Boolean = false

    private lateinit var mSensorDataViewModel: SensorDataViewModel
    private lateinit var mMeasurementViewModel: MeasurementViewModel
    private lateinit var dataAdapter: SensorDataAdapter

    private var measurementID: Long = 0

    private lateinit var mService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)

        mHandler = MyHandler(this)

        btn_start.onClick {
            setFilters()
            startService(UsbService::class.java, usbConnection, null)
            serviceRunning = true
            btn_stop.apply { visibility = View.VISIBLE }
            btn_start.apply { visibility = View.INVISIBLE }
        }
        btn_stop.onClick {
            unregisterReceiver(mUsbReceiver)
            stopService(mService)
            unbindService(usbConnection)
            serviceRunning = false

            btn_start.apply { visibility = View.VISIBLE }
            btn_stop.apply { visibility = View.INVISIBLE }

        }

        btn_end.onClick {
            // perform stop
            if (serviceRunning)
                btn_stop.performClick()

            var list = dataAdapter.dataList
            if (list?.isNotEmpty()) {
                val end = list?.get(0)?.timestamp
                val start = list?.get(list.lastIndex)?.timestamp

                // calculate average values
                var averagePM25: Double = 0.0
                var averagePM10: Double = 0.0

                list.forEach {
                    averagePM25 = averagePM25.plus(it.pm25.toDouble())
                    averagePM10 = averagePM10.plus(it.pm10.toDouble())
                }
                averagePM25 /= list.size
                averagePM10 /= list.size

                var measurement = mMeasurementViewModel.getMeasurementByID(measurementID)
                measurement.start = start!!
                measurement.end = end!!
                measurement.pm10 = averagePM10.toString()
                measurement.pm25 = averagePM25.toString()
                mMeasurementViewModel.insert(measurement)
            }
            // exit here
            finish()
        }

        if (savedInstanceState == null) {
            measurementID = intent.extras.getLong(ARG_ITEM_ID)
            warn { "LocationID = ${measurementID}" }

            dataAdapter = SensorDataAdapter(this)
            rv_sensor_items.adapter = dataAdapter

            mMeasurementViewModel = ViewModelProviders.of(this).get(MeasurementViewModel::class.java)

            mSensorDataViewModel = ViewModelProviders.of(this).get(SensorDataViewModel::class.java)
            mSensorDataViewModel.getDataByMeasurementID(measurementID)?.observe(this, Observer<List<SensorData>> { data ->
                // Update the cached copy of the words in the adapter.
                data?.run {
                    dataAdapter.dataList = data
                    dataAdapter.notifyDataSetChanged()
                }
            })


            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.title = "Measurement #${measurementID}"


            rv_sensor_items.setHasFixedSize(true)
            rv_sensor_items.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            stopService(mService)
            unbindService(usbConnection)
            unregisterReceiver(mUsbReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewSensorData(data: SensorData) {
        if (data.initializedCorrectly) {
            data.measurementID = measurementID
            mSensorDataViewModel.insert(data)
        }
    }

    private fun startService(service: Class<*>, serviceConnection: ServiceConnection, extras: Bundle?) {
        if (!UsbService.SERVICE_CONNECTED) {
            mService = Intent(this, service)
            if (extras != null && !extras.isEmpty) {
                val keys = extras.keySet()
                for (key in keys) {
                    val extra = extras.getString(key)
                    mService.putExtra(key, extra)
                }
            }
            startService(mService)

            val bindingIntent = Intent(this, service)
            bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
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
    private class MyHandler(activity: SensorActivity) : Handler(), AnkoLogger {
        private val mActivity: SensorActivity = activity

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UsbService.MESSAGE_FROM_SERIAL_PORT ->
                    mActivity.addNewSensorData(SensorData(msg.obj as ByteArray))
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
