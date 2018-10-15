package com.example.administrator.achi.fragment

import android.content.Intent.getIntent
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.Analyzer
import com.example.administrator.achi.dataModel.DataCenter
import com.example.administrator.achi.model3D.MyGLSurfaceView
import com.example.administrator.achi.model3D.demo.SceneLoader
import com.example.administrator.achi.model3D.view.ModelSurfaceView

import kotlinx.android.synthetic.main.fragment_monitoring.*
import java.time.LocalDateTime
import java.util.*

private const val INIT : Boolean = true
private const val RUN : Boolean = false

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private var thisView: View? = null

    // Analyze
    private lateinit var today : LocalDateTime

    // Stopwatch
    private var handler : Handler = Handler()
    private lateinit var runnable : Runnable

    private var curState : Boolean = INIT
    private var baseTime : Long = 0

    private lateinit var mGLView : GLSurfaceView

    // Fact
    private val facts = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        this.paramUri = Uri.parse("dsfkj")


    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")

        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_monitoring, container, false)
        }

        return thisView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (curState == RUN) {
            handler.removeCallbacks(runnable)
            curState = INIT
        }

//        mGLView = MyGLSurfaceView(this.context!!)


        // Create a 3D scenario
        scene = SceneLoader(this)
        scene.init()
//
//        // Create a GLSurfaceView instance
//        gLView.setParent(this);

        tvTime.setText("00:00")

        stopWatch()

        addFacts()
        printFacts()
    }

    // TODO : stopwatch 키고 다른 페이지 갔다가 다시 와서 stop 하면 stop 안되고 시간 계속 감 but 한번 더 누르면 처음으로 돌아감
    // StopWatch
    fun stopWatch() {
        runnable = object : Runnable {
            override fun run() {
                tvTime.text = Analyzer.timeToString(getElapsedTime())

                handler.postDelayed(this, 0)
            }
        }

//        model.setOnClickListener() {
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

        // for test
//        btnRecord.setOnClickListener() {           // record
//            if (curState == RUN) {
//                var curTime = SystemClock.elapsedRealtime()
//                var resultTime : Long = curTime - pauseTime
//                var tooth_num : Int = 0
//
//                pauseTime = curTime
//
//                tvRecord.setText(resultTime.toString())
//                Analyzer.pressure()
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
    fun addFacts() {
        facts.add("아치의 꿀팁 1: 칫솔에 물을 묻히지 마세요! 치약에 물이 묻게 되면 세마제의 농도가 떨어지기 때문에 양치질 효과가 줄어들게 된답니다.")
        facts.add("아치의 꿀팁 2: 탄산음료, 커피 등을 마신 후 30분 후에 양치하기!  음료에 포함된 산성물질이 치아 표면의 얇은 막을 부식시키기 때문에 약간의 시간이 지난 후에 양치하는 것이 좋습니다.")
        facts.add("아치의 꿀팁 3: 어금니, 바깥쪽면, 안쪽면, 씹는면 순으로 닦기! 그리고 옆으로 닦아 내리는 것보다 칫솔을 회전시키면서 쓸어내리는 느낌으로 양치질하는 것이 좋습니다.")

    }

    fun printFacts() {
        val random = Random()
        val num = random.nextInt(facts.size)

        tvFact.text = facts.get(num)

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