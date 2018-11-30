package com.example.administrator.achi.fragment

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
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
import java.io.ByteArrayOutputStream
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

    private var mmOutputStream : OutputStream = ByteArrayOutputStream(1024)

    // fact
    private var count : Int = 10

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        curState = RUN
        this.paramUri = Uri.parse("nothing")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")

        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_monitoring, container, false)
        }
        return thisView
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        bluetoothHandler()
        btAdapter = BluetoothAdapter.getDefaultAdapter()       // get Bluetooth adapter

        if (btAdapter == null) {
            Log.i(TAG, "btAdapter is null")
            return
        }
        device = btAdapter!!.getRemoteDevice(address)
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
        printFact()
        tvTime.text = "00:00"

        // sound init
        soundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        soundID = soundPool.load(context, R.raw.bamboo, 1)


        layout.setOnClickListener() {
            if (btAdapter == null)
                Log.d("Fatal Error", "Bluetooth is not supported on this device.")
            else {
                if (!btAdapter!!.isEnabled) {
//                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    Toast.makeText(context, "블루투스를 키고 칫솔과 페어링 하세요.", Toast.LENGTH_LONG).show()
                }
                else {
                    if(curState == INIT) {
                        startBluetooth()
                        today = LocalDateTime.now()
                        Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_SHORT).show()
                    }

                    else if (curState == RUN) {
                        endBluetooth()
                        bttest.text = "Communication Ended"
                        Toast.makeText(context, "Bluetooth disconnected", Toast.LENGTH_SHORT).show()

                        Analyzer.analyze(today)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "...In onPause()...")

        endBluetooth()
        Analyzer.init()

        if (curState == RUN ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("알림")
                    .setMessage("모니터링 화면에서 벗어나 모니터링이 중단되었습니다.")
                    .setPositiveButton("확인") { _, _ -> }

            val dialog = builder.create()
            dialog.show()
        }

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
                        Log.i("bluetooth", "sb : $sb  end : $endOfLineIndex")
                        if (endOfLineIndex > 0) {
                            sbprint = sb.substring(0, endOfLineIndex)
                            Log.i("bluetooth", "bprint : $sbprint")
                            sb.delete(0, sb.length)
                            bttest.text= sbprint

                            var inputs = sbprint.split("/")
                            var toothNum = 0
                            var yourCheckSum = 0
                            var myCheckSum = -1
                            var pressure = 0
                            if (inputs.size == 3) {
                                toothNum = inputs[0].toInt()
                                yourCheckSum = inputs[1].toInt()
                                myCheckSum = toothNum % 7
                                pressure = inputs[2].toInt()
                            }

                            if (toothNum == 0)
                                return

                            if ((toothNum == -1) && (yourCheckSum == myCheckSum)) {
                                Analyzer.addTime()
                                tvTime.text = Analyzer.timeToString(Analyzer.elapsed_time.toInt())
                            }

                            Log.i("bluetooth", "toothNum : $toothNum   mine : $myCheckSum   yours : $yourCheckSum")
                            // if tooth index is valid, update view
                            if (Analyzer.TEETH_INDICES.contains(toothNum!!) && (yourCheckSum == myCheckSum)) {
                                Log.i("bluetooth", "Success")

                                // Update time
                                Analyzer.countTooth(toothNum)
                                tvTime.text = Analyzer.timeToString(Analyzer.elapsed_time.toInt())

                                // pressure
                                if (pressure == 3)
                                    Analyzer.highPressure()
                                else if (pressure == 1)
                                    Analyzer.lowPressure()

                                // sound alarm
                                if (Analyzer.elapsed_time == 150.0)
                                    soundPool.play(soundID, 1f, 1f, 0, 0,  0.5f)

                                if (Analyzer.elapsed_time == 180.0)
                                    soundPool.play(soundID, 1f, 1f, 0, 0,  0.5f)

                                // fact
                                count --
                                if ((Analyzer.elapsed_time.toInt() % 10 == 0) && (count <= 0) ) {
                                    printFact()
                                    count = 10
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
                            else if (yourCheckSum != myCheckSum) {

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

//        sendData()
        curState = RUN
    }

    private fun endBluetooth() {
        if (btSocket == null) {
            Log.d("Fatal Error", "btSocket is null.")
            return
        }
        try {
//            sendData()
            mmOutputStream.close()
            btSocket!!.close()
        } catch (e2: IOException) {
            Log.d("Fatal Error", "Failed to close socket." + e2.message + ".")
        }
        finally {
            btSocket = null
            curState = INIT
        }
    }

    private fun sendData(){
        mmOutputStream = btSocket!!.outputStream
        var msg : String = ""

        if (curState == INIT) {     // 통신 시작
            msg += "start"
        }
        else if (curState == RUN) {     // 통신 끝
            msg += "end"
        }

        try{
            mmOutputStream.write(msg.toByteArray())
        }catch (e : IOException) {
            Log.i("Fatal Error", "Failed to send value to socket" + e.message)
        }

    }

    // Facts
    private fun printFact() {
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