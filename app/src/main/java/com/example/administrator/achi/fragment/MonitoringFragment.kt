package com.example.administrator.achi.fragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

private const val INIT : Boolean = false
private const val RUN : Boolean = true
private const val RECIEVE_MESSAGE = 1

private var bluetooth_handler : Handler ?= null

// SPP UUID service
private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

// MAC-address of Bluetooth module (you must edit this line)
private var address = "98:D3:61:F9:29:CB"

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private val REQUEST_ENABLE_BT = 1

    private var thisView: View? = null

    // Stopwatch
    private var curState : Boolean = INIT
    private lateinit var today : LocalDateTime
    private lateinit var soundPool : SoundPool
    private var soundID : Int = 0

    // Bluetooth
    private var btAdapter : BluetoothAdapter? = null
    private var btSocket : BluetoothSocket?= null
    private var mConnectedThread : ConnectedThread ?= null
    private lateinit var device: BluetoothDevice

    private var sb : StringBuilder = StringBuilder()
    private var toothNum_prev : Int = 0
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
//        checkBTState()

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

        tvTime.text = "00:00"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a 3D scenario
        scene = SceneLoader(this)
        scene.init()

        // Create a GLSurfaceView instance
        gLView = ModelSurfaceView(context, this)
        layout.addView(gLView)

        // Initialize view
        DataCenter.loadFacts()
        printFacts()

        // sound init
        soundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        soundID = soundPool.load(context, R.raw.bamboo, 1)


        layout.setOnClickListener() {
            if (btAdapter == null)
                Log.d("Fatal Error", "Bluetooth is not supported on this device.")
            else {
                if (!btAdapter!!.isEnabled()) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            }
            if(curState == INIT) {
                startBluetooth()
                curState = RUN
                today = LocalDateTime.now()
                Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_SHORT).show()
            }

            else if (curState == RUN) {
                endBluetooth()
                curState = INIT
                bttest.text = "Communication Ended"
                Toast.makeText(context, "Bluetooth disconnected", Toast.LENGTH_SHORT).show()

                Analyzer.analyze(today)
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
                            Log.i(TAG, "sb : $sb     sbprint : $sbprint     end : $endOfLineIndex")
                            sb.delete(0, sb.length)
                            bttest.text= sbprint
                            var toothNum = sbprint?.toInt()

                            // TODO: error handling, do checksum here
                            if (toothNum == null)
                                return

                            // if tooth index is valid, update view
                            if (Analyzer.TEETH_INDICES.contains(toothNum!!)) {
                                // Update time
                                Analyzer.countTooth(toothNum)
                                tvTime.text = Analyzer.timeToString(Analyzer.elapsed_time)

                                if (Analyzer.elapsed_time == 180) {
                                    soundPool.play(soundID, 1f, 1f, 0, 0,  0.5f)

                                }

                                // highlight current tooth
                                scene.colorTeethAndRotate(toothNum, Color.YELLOW)

                                // Check if
                                if (toothNum != toothNum_prev) {
                                    if (Analyzer.isDone(toothNum_prev))
                                        scene.colorTeeth(toothNum_prev, Color.WHITE)
                                    else if (Analyzer.isHalfWayDone(toothNum_prev))
                                        scene.colorTeeth(toothNum_prev, Color.LIGHTBLUE)
                                    else
                                        scene.colorTeeth(toothNum_prev, Color.BLUE)
                                }
                                toothNum_prev = toothNum
                            }
                            else if (toothNum == -1) {
                            }
                        }
                        else if (endOfLineIndex == 0) {
                            sb.delete(0, sb.length)
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
        Log.i(TAG, "...Connecting...")
        try {
            btSocket?.connect()
            Log.i(TAG, "....Connection ok...")
        } catch (e: IOException) {
            Log.i(TAG, e.message)
            try {
                btSocket?.close()
                Log.i(TAG, "....Connection not ok...")
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

    // Facts
    private fun printFacts() {
        val random = Random()
        val num = random.nextInt(DataCenter.facts.size)

        tvFact.text = DataCenter.facts[num]
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

    companion object {
        @JvmStatic
        fun newInstance() = MonitoringFragment()
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