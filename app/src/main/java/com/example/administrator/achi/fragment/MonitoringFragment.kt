package com.example.administrator.achi.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.*
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.administrator.achi.R
import com.example.administrator.achi.calendar.CalendarView
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

private const val INIT : Boolean = true
private const val RUN : Boolean = false
private const val RECIEVE_MESSAGE = 1;
private var btAdapter : BluetoothAdapter? = null
private var btSocket : BluetoothSocket?= null
private var sb : StringBuilder = StringBuilder()
private var flag : Int = 0;
private var h : Handler ?= null
private var sbprint : String ?=null
private var sbprint_prev : Int ?= null

private var mConnectedThread : ConnectedThread ?= null
private var toothThread : MonitoringFragment.ToothThread ?=null
// SPP UUID service
private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

// MAC-address of Bluetooth module (you must edit this line)
private var address = "98:D3:61:F9:29:CB"

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private var thisView: View? = null

    // Stopwatch
    private var handler : Handler = Handler()
    private lateinit var runnable : Runnable

    private var curState : Boolean = INIT
    private var baseTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        this.paramUri = Uri.parse("nothing")
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")
        var device: BluetoothDevice
        if (btAdapter == null) {
            Log.i(TAG, "btAdapter is null")
            return
        }
        device = btAdapter!!.getRemoteDevice(address)
        try {
            btSocket = createBluetoothSocket(device)
        } catch (e: IOException) {
            Log.d("Fatal Error", "In onResume() and socket create failed: " + e.message + ".")
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter!!.cancelDiscovery()

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...")
        try {
            btSocket?.connect()
            Log.d(TAG, "....Connection ok...")
        } catch (e: IOException) {
            try {
                btSocket?.close()
            } catch (e2: IOException) {
                Log.d("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.message + ".")
            }
        }


        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...")

        mConnectedThread = ConnectedThread(btSocket)
        mConnectedThread!!.start()
        var tThread : Thread = Thread(ToothThread(scene))
        tThread.isDaemon;
        tThread.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")

        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_monitoring, container, false)
        }
        var bttest = thisView!!.findViewById<TextView>(R.id.bttest)

        h = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                when (msg.what) {
                    RECIEVE_MESSAGE -> {
                        val readBuf = msg.obj as ByteArray
                        val strIncom = String(readBuf, 0, msg.arg1)
                        sb.append(strIncom)
                        val endOfLineIndex = sb.indexOf("\r\n")
                        if (endOfLineIndex > 0) {
                            sbprint = sb.substring(0, endOfLineIndex)
                            sb.delete(0, sb.length)
                            bttest.text= sbprint
                        }
                    }
                }
            }
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter()       // get Bluetooth adapter
        checkBTState()

        return thisView
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

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "...In onPause()...")

        try {
            btSocket!!.close()
        } catch (e2: IOException) {
            Log.d("Fatal Error", "In onPause() and failed to close socket." + e2.message + ".")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (curState == RUN) {
            handler.removeCallbacks(runnable)
            curState = INIT
        }

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

        // test
        tvTime.setOnClickListener({
            testHighlight()
        })
    }
    var ctr = 11;
    var color = Color.WHITE
    /**
     * test code to see if teeth model rotates properly
     * teeth model will rotate on clicking tvTime,
     * in the order of the tooth number
     */
    private fun testHighlight() {
        if (ctr in 11..47) {

            if (ctr % 10 == 8) {
                ctr += 3
            }

            if (ctr % 10 < 8 && ctr % 10 != 0) {
                Log.i("MonitoringFragment", "ctr1: $ctr)")
                scene.colorTeeth(ctr.toString(), color)
            }

        } else {
            ctr = 10
            if (color == Color.WHITE)
                color = Color.YELLOW
            else
                color = Color.WHITE
        }

        ctr++
    }

    // TODO : stopwatch 키고 다른 페이지 갔다가 다시 와서 stop 하면 stop 안되고 시간 계속 감 but 한번 더 누르면 처음으로 돌아감
    // StopWatch
    fun stopWatch() {
        runnable = object : Runnable {
            override fun run() {
                tvTime.text = Analyzer.timeToString(getElapsedTime())
                ////

                ////
                handler.postDelayed(this, 0)
            }
        }

//        layout.setOnClickListener() {
//            if (curState == INIT) {                         // 시작
//                baseTime = SystemClock.elapsedRealtime()
//                handler.postDelayed(runnable, 0)
//
//                pauseTime = baseTime
//                curState = RUN
//
//                today = LocalDateTime.now()
//
//            }
//
//            else if (curState == RUN) {                    // 끝
//                curState = INIT
//                handler.removeCallbacks(runnable)
//
//
//                // Analyzer에 최종 전달
//                Analyzer.analyze(today, getElapsedTime())
//
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
    fun printFacts() {
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

    private fun checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            Log.d("Fatal Error", "Bluetooth is not supported on this device.")
        } else {
            if (btAdapter!!.isEnabled()) {
                Log.d(ContentValues.TAG, "...Bluetooth ON...")
            } else {
                Log.d(ContentValues.TAG, "...Bluetooth OFF...")
            }
        }
    }

    class ToothThread(var scene : SceneLoader): Runnable{

        // Thread 때와 마찬가지로 run() 메소드 구현

        override fun run() {
            while(true){
                if (sbprint?.toInt() in 11..47) {
                    if(sbprint_prev == null){
                        Log.d("MonitoringFragment", sbprint)
                        scene.colorTeeth(sbprint.toString(), Color.YELLOW)
                        sbprint_prev = sbprint?.toInt()
                    }
                    else if (sbprint?.toInt() == sbprint_prev) {

                    }
                    else{
                        Log.d("MonitoringFragment", sbprint)
                        scene.colorTeeth(sbprint_prev.toString(), Color.WHITE)
                        scene.colorTeeth(sbprint.toString(), Color.YELLOW)
                        sbprint_prev = sbprint?.toInt()
                    }
                }
                try {
                    Thread.sleep(1000); // 1000ms 단위로 실행
                } catch (e : InterruptedException ) {
                    e.printStackTrace();
                }
            } // end while
        } // end run()
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
                h?.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)?.sendToTarget()     // Send to message queue Handler
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