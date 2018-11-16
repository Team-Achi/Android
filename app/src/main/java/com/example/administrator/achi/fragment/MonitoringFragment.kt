package com.example.administrator.achi.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.Analyzer
import com.example.administrator.achi.dataModel.DataCenter
import com.example.administrator.achi.model3D.demo.SceneLoader
import com.example.administrator.achi.model3D.demo.SceneLoader.Color
import com.example.administrator.achi.model3D.view.ModelSurfaceView

import kotlinx.android.synthetic.main.fragment_monitoring.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.util.*

private const val INIT : Int = 0
private const val RUN : Int = 1
private const val PAUSE : Int = 2

private const val RECIEVE_MESSAGE = 1

private var bluetooth_handler : Handler ?= null

// SPP UUID service
private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

// MAC-address of Bluetooth module (you must edit this line)
private var address = "98:D3:61:F9:29:CB"

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private var thisView: View? = null

    // Stopwatch
    private var stopwatch_handler : Handler = Handler()
    private lateinit var runnable : Runnable

    private var curState : Int = INIT
    private var baseTime : Long = 0
    private lateinit var today : LocalDateTime

    // Bluetooth
    private var btAdapter : BluetoothAdapter? = null
    private var btSocket : BluetoothSocket?= null
    private var mConnectedThread : ConnectedThread ?= null
    private lateinit var device: BluetoothDevice

    private var sb : StringBuilder = StringBuilder()
    private var toothNum_prev : Int ?= null
    private var flag : Int = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        this.paramUri = Uri.parse("nothing")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")

        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_monitoring, container, false)
        }
        bluetoothHandler()

        btAdapter = BluetoothAdapter.getDefaultAdapter()       // get Bluetooth adapter
        checkBTState()

        return thisView
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        if (btAdapter == null) {
            Log.i(TAG, "btAdapter is null")
            return
        }
        device = btAdapter!!.getRemoteDevice(address)

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter!!.cancelDiscovery()

//        startBluetooth()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (curState == RUN) {
//            stopwatch_handler.removeCallbacks(runnable)
//            curState = INIT
//        }

        // Create a 3D scenario
        scene = SceneLoader(this)
        scene.init()

        // Create a GLSurfaceView instance
        gLView = ModelSurfaceView(context, this)
        layout.addView(gLView)

        // Initialize view
        stopWatch()
        DataCenter.loadFacts()
        printFacts()

        layout.setOnClickListener() {
            if(curState == INIT) {
                startBluetooth()
                curState = RUN          // just for test
                Log.i("esanghan", ">>>>>>>>>>>>>>> Bluetooth connected")
            }
            else if (curState == RUN || curState == PAUSE) {
                endBluetooth()
                curState = INIT        // just for test
                Log.i("esanghan", ">>>>>>>>>>>>>>> Bluetooth disconnected")
                bttest.text = "Communication Ended"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "...In onPause()...")

        endBluetooth()
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                val m = device.javaClass.getMethod("createInsecureRfcommSocketToServiceRecord", *arrayOf<Class<*>>(UUID::class.java))
                return m.invoke(device, MY_UUID) as BluetoothSocket
            } catch (e: Exception) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e)
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID)
    }

    // Bluetooth
    private fun bluetoothHandler() {
        bluetooth_handler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                when (msg.what) {
                    RECIEVE_MESSAGE -> {
                        val readBuf = msg.obj as ByteArray
                        val strIncom = String(readBuf, 0, msg.arg1)
                        var sbprint : String

                        sb.append(strIncom)
                        val endOfLineIndex = sb.indexOf("\r\n")
                        Log.i("esanghan", "sb : $sb     end : $endOfLineIndex")
                        if (endOfLineIndex > 0) {
                            sbprint = sb.substring(0, endOfLineIndex)
                            Log.i("esanghan", "sb : $sb     sbprint : $sbprint     end : $endOfLineIndex")
                            sb.delete(0, sb.length)
                            bttest.text= sbprint
                            var toothNum = sbprint?.toInt()

                            if (toothNum == toothNum_prev) {

                            }
                            else {
                                if (toothNum_prev != null)
                                    scene.colorTeeth(toothNum_prev!!, Color.WHITE)
                                Log.d("MonitoringFragment", sbprint)
                                scene.colorTeeth(toothNum!!, Color.YELLOW)
                                toothNum_prev = toothNum
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startBluetooth() {
        try {
            btSocket = createBluetoothSocket(device)
        } catch (e: IOException) {
            Log.d("Fatal Error", "In onResume() and socket create failed: " + e.message + ".")
        }

        // Establish the connection.  This will block until it connects.
        Log.i("esanghan", "...Connecting...")
        try {
            btSocket?.connect()
            Log.i("esanghan", "....Connection ok...")
        } catch (e: IOException) {
            Log.i("esanghan", e.message)
            try {
                btSocket?.close()
                Log.i("esanghan", "....Connection not ok...")
            } catch (e2: IOException) {
                Log.d("Fatal Error", "Unable to close socket during connection failure" + e2.message + ".")
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...")
        mConnectedThread = ConnectedThread(btSocket)
        mConnectedThread!!.start()
    }

    private fun endBluetooth() {
        if (btSocket == null) {
            Log.d("Fatal Error", "btSocket is null.")
            return
        }

        try {
            btSocket!!.close()
        } catch (e2: IOException) {
            Log.d("Fatal Error", "Failed to close socket." + e2.message + ".")
        }
        finally {
            btSocket = null
        }
    }

    private fun checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            Log.d("Fatal Error", "Bluetooth is not supported on this device.")
        } else {
            if (btAdapter!!.isEnabled) {
                Log.d(ContentValues.TAG, "...Bluetooth ON...")
            } else {
                Log.d(ContentValues.TAG, "...Bluetooth OFF...")
            }
        }
    }

    // TODO : stopwatch 키고 다른 페이지 갔다가 다시 와서 stop 하면 stop 안되고 시간 계속 감 but 한번 더 누르면 처음으로 돌아감
    // StopWatch
    private fun stopWatch() {
        runnable = object : Runnable {
            override fun run() {
                tvTime.text = Analyzer.timeToString(getElapsedTime())
                stopwatch_handler.postDelayed(this, 0)
            }
        }

//        layout.setOnClickListener() {
//            if (curState == INIT) {                         // 시작
//                baseTime = SystemClock.elapsedRealtime()
//                stopwatch_handler.postDelayed(runnable, 0)
//
//                curState = RUN
//                today = LocalDateTime.now()
//            }
//
//            else if (curState == RUN) {                    // 끝
//                curState = INIT
//                stopwatch_handler.removeCallbacksAndMessages(runnable)
//
//                duration = getElapsedTime()
//                Analyzer.analyze(today, duration)
//            }
//        }
    }

    fun getElapsedTime() : Int {
        var curTime : Long = SystemClock.elapsedRealtime()
        var resultTime : Long = curTime - baseTime
        var sec = (resultTime / 1000).toInt()
        return sec
    }

    // Facts
    private fun printFacts() {
        val random = Random()
        val num = random.nextInt(DataCenter.facts.size)

        tvFact.text = DataCenter.facts[num]
    }

    companion object {
        @JvmStatic
        fun newInstance() = MonitoringFragment()
    }

    //////////////////////////////////////////////////////////////
    // OpenGL Related                                           //
    //////////////////////////////////////////////////////////////
    /**
     * The file to load. Passed as input parameter
     */
    private lateinit var paramUri: Uri
    /**
     * Background GL clear color. Default is light gray
     */
    private var backgroundColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private lateinit var gLView: ModelSurfaceView


    private lateinit var scene: SceneLoader

    fun getParamUri(): Uri {
        return paramUri
    }

    fun getBackgroundColor(): FloatArray {
        return backgroundColor
    }

    fun getScene(): SceneLoader {
        return scene
    }

    fun getGLView(): ModelSurfaceView {
        return gLView
    }
}

private class ConnectedThread() : Thread(){
    private var mmInStream: InputStream? = null
    private var mmOutStream: OutputStream? = null

    constructor(socket: BluetoothSocket?) : this() {
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket?.inputStream
            tmpOut = socket?.outputStream
        }
        catch (e: IOException) {
        }

        mmInStream = tmpIn
        mmOutStream = tmpOut
    }

    override fun run() {
        val buffer = ByteArray(256)  // buffer store for the stream
        var bytes: Int // bytes returned from read()

        if (mmInStream == null) {
            // TODO
            Log.i("TODO", "mmInStream is null")
            return
        }
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream!!.read(buffer)        // Get number of bytes and message in "buffer"
                bluetooth_handler?.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)?.sendToTarget()     // Send to message queue Handler
            } catch (e: IOException) {
                break
            }

        }
    }

    /* Call this from the main activity to send data to the remote device */
    fun write(message: String) {
        Log.d(ContentValues.TAG, "...Data to send: $message...")
        val msgBuffer = message.toByteArray()
        try {
            mmOutStream!!.write(msgBuffer)
        } catch (e: IOException) {
            Log.d(ContentValues.TAG, "...Error data send: " + e.message + "...")
        }

    }
}

///////////////////////////////////////////////////////////////////////
// stopwatch code
///////////////////////////////////////////////////////////////////////
//        btn_start.setOnClickListener() {thisView->
//
//
//            if (curState == INIT) {         // RUN
//                baseTime = SystemClock.elapsedRealtime()
//                btn_start.setText("PAUSE")
//                btn_record.setEnabled(true)
//                curState = RUN
//
//                handler.postDelayed(runnable, 0)
//
//            }
//            else if (curState == RUN) {     // PAUSE
//                pauseTime = SystemClock.elapsedRealtime()
//                btn_start.setText("START")
//                btn_record.setText("RESET")
//                curState = PAUSE
//                handler.removeCallbacks(runnable)
//            }
//            else if (curState == PAUSE) {       // RUN
//                var curTime : Long = SystemClock.elapsedRealtime()
//                baseTime += curTime - pauseTime
//                btn_start.setText("PAUSE")
//                btn_record.setText("RECORD")
//                curState = RUN
//
//                handler.postDelayed(runnable, 0)
//            }
//        }
//
//        btn_record.setOnClickListener() {thisView->
//            if (curState == RUN) {      // Record
//                second = getElapsedTime()
//                minute = second / 60
//                second = second % 60
//
////                handler.postDelayed(runnable, 0)
//            }
//            else if (curState == PAUSE) {   // Reset
//                btn_start.setText("START")
//                btn_record.setText("RECORD")
//
//                tv_minute.setText("00")
//                tv_second.setText("00")
//
//                curState = INIT
//                btn_record.setEnabled(false)
//                handler.removeCallbacks(runnable)
//            }
//        }