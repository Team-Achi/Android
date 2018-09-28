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

import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_monitoring.*
import kotlin.math.min

private const val INIT :Int = 0
private const val RUN : Int = 1
private const val PAUSE : Int = 2

class MonitoringFragment : Fragment(){
    private val TAG = "MonitoringFragment"
    private var thisView: View? = null


    private var handler : Handler = Handler()
//    private lateinit var runnable : Runnable

    private var minute : Int = 0
    private var second : Int = 0
    private var curState : Int = INIT
    private var baseTime : Long = 0
    private var pauseTime : Long = 0

    private lateinit var tv_minute : TextView
    private lateinit var tv_second : TextView
    private lateinit var tv_semi1 : TextView

    private lateinit var btn_start : Button
    private lateinit var btn_record : Button


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

        val runnable = object : Runnable {
            override fun run() {
                second = getElapsedTime()
                minute = second / 60
                second = second % 60

                if (minute < 10)
                    tv_minute.setText("0" + minute.toString())
                else
                    tv_minute.setText(minute.toString())

                if (second < 10)
                    tv_second.setText("0" + second.toString())
                else
                    tv_second.setText(second.toString())

                handler.postDelayed(this, 0)
            }
        }

        btn_start = thisView!!.findViewById<Button>(R.id.btnStart)
        btn_start = thisView!!.findViewById<Button>(R.id.btnStart)
        btn_record = thisView!!.findViewById<Button>(R.id.btnRecord)
        //        btn_pasue = thisView!!.findViewById<Button>(R.id.btnPause)

        tv_minute = thisView!!.findViewById<TextView>(R.id.tvMinute)
        tv_second = thisView!!.findViewById<TextView>(R.id.tvSecond)
        tv_semi1 = thisView!!.findViewById<TextView>(R.id.tvSemi1)

        btn_start.setOnClickListener() {thisView->


            if (curState == INIT) {         // RUN
                baseTime = SystemClock.elapsedRealtime()
                btn_start.setText("PAUSE")
                btn_record.setEnabled(true)
                curState = RUN

                handler.postDelayed(runnable, 0)

            }
            else if (curState == RUN) {     // PAUSE
                pauseTime = SystemClock.elapsedRealtime()
                btn_start.setText("START")
                btn_record.setText("RESET")
                curState = PAUSE
                handler.removeCallbacks(runnable)
            }
            else if (curState == PAUSE) {       // RUN
                var curTime : Long = SystemClock.elapsedRealtime()
                baseTime += curTime - pauseTime
                btn_start.setText("PAUSE")
                btn_record.setText("RECORD")
                curState = RUN

                handler.postDelayed(runnable, 0)
            }
        }

        btn_record.setOnClickListener() {thisView->
            if (curState == RUN) {      // Record
                second = getElapsedTime()
                minute = second / 60
                second = second % 60

                handler.postDelayed(runnable, 0)
                // Later TODO : 기록하는 textView
            }
            else if (curState == PAUSE) {   // Rset
                btn_start.setText("START")
                btn_record.setText("RECORD")

                tv_minute.setText("00")
                tv_second.setText("00")

                curState = INIT
                btn_record.setEnabled(false)
                handler.removeCallbacks(runnable)
            }
        }

        return thisView

    }

    fun getElapsedTime() : Int {
        var curTime : Long = SystemClock.elapsedRealtime()
        var resultTime : Long = curTime - baseTime
        var sec = (resultTime / 1000).toInt()
        return sec
    }

    companion object {
        @JvmStatic
        fun newInstance() = MonitoringFragment()
    }
}