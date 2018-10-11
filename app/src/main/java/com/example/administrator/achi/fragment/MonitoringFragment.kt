package com.example.administrator.achi.fragment

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.administrator.achi.R
import android.widget.Chronometer
import android.widget.ImageView

import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_monitoring.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

private const val INIT :Int = 0
private const val RUN : Int = 1
private const val PAUSE : Int = 2

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private var thisView: View? = null

    // Stopwatch
    private var handler : Handler = Handler()
    private lateinit var runnable : Runnable

    private var minute : Int = 0
    private var second : Int = 0
    private var curState : Int = INIT
    private var baseTime : Long = 0
    private var pauseTime : Long = 0

    private lateinit var tv_time : TextView
    private lateinit var iv_model : ImageView

//    private lateinit var btn_start : Button
//    private lateinit var btn_record : Button

    // Fact
    private val facts = ArrayList<String>()

    private lateinit var tv_fact : TextView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

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

        tv_time = thisView!!.findViewById<TextView>(R.id.tvTime)
        iv_model = thisView!!.findViewById<ImageView>(R.id.model)
        tv_fact = thisView!!.findViewById(R.id.tvFact)

//        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>curState : " + curState)
        if (curState == INIT)
            tv_time.setText("00:00")

        stopWatch()

        addFacts()
        printFacts()


        return thisView
    }

    // Error : stopwatch 키고 다른 페이지 갔다가 다시 와서 stop 하면 stop 안되고 시간 계속 감 but 한번 더 누르면 처음으로 돌아감
    // StopWatch
    fun stopWatch() {
        runnable = object : Runnable {
            override fun run() {
                second = getElapsedTime()
                minute = second / 60
                second = second % 60

                var strMin : String
                var strSecond : String

                if (minute < 10)
                    strMin = "0" + minute.toString()
                else
                    strMin = minute.toString()

                if (second < 10)
                    strSecond = "0" + second.toString()
                else
                    strSecond = second.toString()
                tv_time.setText(strMin + ":" + strSecond)

                handler.postDelayed(this, 0)
            }
        }

        iv_model.setOnClickListener() {
            if (curState == INIT) {
                baseTime = SystemClock.elapsedRealtime()
                curState = RUN

                handler.postDelayed(runnable, 0)
            }

            else if (curState == RUN) {
                pauseTime = SystemClock.elapsedRealtime()
                curState = INIT

                handler.removeCallbacks(runnable)
            }
        }
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
        facts.add("아치의  꿀팁 2: 탄삼음료, 커피 등을 마신 후 30분 후에 양치하기!  음료에 포함왼 산성물질이 치아 표면의 얇은 막을 부식시키기 때문에 약간의 시간이 지난 후에 양치하는 것이 좋습니다.")
        facts.add("아치의 꿀팁 3: 어금니, 바깥쪽면, 안쪽면, 씹는면 순으로 닦기! 그리고 엽으로 닦아 내리는 것보다 칫솔을 회전시키면서 쓸어내리는 느낌으로 양치질하는 것이 좋습니다.")

    }

    fun printFacts() {
        val random = Random()
        val num = random.nextInt(facts.size)

        tv_fact.text = facts.get(num)

    }

    companion object {
        @JvmStatic
        fun newInstance() = MonitoringFragment()
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
//                // Later TODO : 기록하는 textView
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