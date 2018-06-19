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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import de.htw.berlin.s0558606.lasersensorcommunicator.model.MeasurementViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorData
import de.htw.berlin.s0558606.lasersensorcommunicator.model.SensorDataViewModel
import de.htw.berlin.s0558606.lasersensorcommunicator.serial.UsbService
import de.htw.berlin.s0558606.lasersensorcommunicator.ui.SensorDataAdapter
import kotlinx.android.synthetic.main.content_sensor.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk21.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.warn

class SensorActivity : AppCompatActivity(), AnkoLogger {

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbService.ACTION_USB_PERMISSION_NOT_GRANTED
                -> toast("USB Permission not granted")
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
            usbService?.setHandler(handler)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
        }
    }

    private var usbService: UsbService? = null
    private lateinit var handler: MyHandler

    private var serviceRunning: Boolean = false

    private var collectingInterval = 1
    private var counter = 0

    private lateinit var sensorDataViewModel: SensorDataViewModel
    private lateinit var measurementViewModel: MeasurementViewModel
    private lateinit var dataAdapter: SensorDataAdapter

    private var measurementID: Long = 0

    private lateinit var serviceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        handler = MyHandler(this)

        btn_start.onClick {
            setFilters()
            startService(UsbService::class.java, usbConnection, null)
            serviceRunning = true
            btn_stop.visibility = View.VISIBLE
            btn_start.visibility = View.INVISIBLE

            setWakelock(true)
        }
        btn_stop.onClick {
            unregisterReceiver(usbReceiver)
            stopService(serviceIntent)
            unbindService(usbConnection)
            serviceRunning = false

            btn_start.visibility = View.VISIBLE
            btn_stop.visibility = View.INVISIBLE

            setWakelock(false)

        }

        btn_end.onClick {
            // perform stop
            if (serviceRunning)
                btn_stop.performClick()

            calculateAndInsertDataToDB()
            // exit here
            finish()
        }

        et_interval.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                try {
                    collectingInterval = input.toInt()
                    counter = collectingInterval
                } catch (e: NumberFormatException) {
                }
            }
        })

        if (savedInstanceState == null) {
            measurementID = intent.extras.getLong(ARG_ITEM_ID)
            warn { "MeasurementID = ${measurementID}" }

            dataAdapter = SensorDataAdapter(this)
            rv_sensor_items.adapter = dataAdapter

            measurementViewModel = ViewModelProviders.of(this).get(MeasurementViewModel::class.java)

            sensorDataViewModel = ViewModelProviders.of(this).get(SensorDataViewModel::class.java)
            sensorDataViewModel.getDataByMeasurementID(measurementID)?.observe(this, Observer<List<SensorData>> { data ->
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

    private fun calculateAndInsertDataToDB() {
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

            var measurement = measurementViewModel.getMeasurementByID(measurementID)
            measurement.start = start!!
            measurement.end = end!!
            measurement.pm10 = averagePM10.toString()
            measurement.pm25 = averagePM25.toString()
            measurementViewModel.update(measurement)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            stopService(serviceIntent)
            unbindService(usbConnection)
            unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyNewData(data: SensorData) {
        counter++
        if (counter / collectingInterval >= 1) {
            counter = 0
            saveNewData(data)
        }
    }

    private fun saveNewData(data: SensorData) {
        if (data.initializedCorrectly) {
            data.measurementID = measurementID
            sensorDataViewModel.insert(data)
        }
    }

    private fun startService(service: Class<*>, serviceConnection: ServiceConnection, extras: Bundle?) {
        if (!UsbService.SERVICE_CONNECTED) {
            serviceIntent = Intent(this, service)
            if (extras != null && !extras.isEmpty) {
                val keys = extras.keySet()
                for (key in keys) {
                    val extra = extras.getString(key)
                    serviceIntent.putExtra(key, extra)
                }
            }
            startService(serviceIntent)

            val bindingIntent = Intent(this, service)
            bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun setWakelock(wakelock: Boolean) {
        if (wakelock)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    private fun setFilters() {
        val filter = IntentFilter()
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED)
        filter.addAction(UsbService.ACTION_NO_USB)
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED)
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED)
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)
        registerReceiver(usbReceiver, filter)
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler(activity: SensorActivity) : Handler(), AnkoLogger {
        private val activity: SensorActivity = activity

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UsbService.MESSAGE_FROM_SERIAL_PORT -> {
                    activity.notifyNewData(SensorData(msg.obj as ByteArray))
                }
                UsbService.CTS_CHANGE ->
                    Toast.makeText(activity, "CTS_CHANGE", Toast.LENGTH_LONG).show()
                UsbService.DSR_CHANGE ->
                    Toast.makeText(activity, "DSR_CHANGE", Toast.LENGTH_LONG).show()

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
