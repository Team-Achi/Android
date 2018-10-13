package com.example.administrator.achi.fragment

import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.fragment_analyzehabit.view.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AnalyzeHabitFragment : Fragment(){
    private val TAG = "AnalyzeHabitFragment"
    private var thisView: View? = null


    private lateinit var tv_weeklyScore : TextView
    private lateinit var tv_weelyComment : TextView
    private lateinit var lv_weeklyHabbit : ExpandableListView

    private var groupList = ArrayList<String>()                         // 그룹 이름(item), header
    private var childList = ArrayList<ArrayList<String>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
    private var childListContent = ArrayList<ArrayList<String>>()     // subitem 내용들, record 하나


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
            thisView = inflater.inflate(R.layout.fragment_analyzehabit, container, false)
        }

        tv_weeklyScore = thisView!!.findViewById(R.id.tvWeeklyScore)
        tv_weelyComment = thisView!!. findViewById(R.id.tvWeeklyComment)
        lv_weeklyHabbit = thisView!!.findViewById(R.id.lvWeeklyHabbit)


        var sample_record : Record = Record()
        DataCenter.records.add(0,sample_record)

        init()

        getScore()

//        getDate()
        addToList()

        // Expandable List View
        val expandableListAdapter = com.example.administrator.achi.fragment.ExpandableListAdapter(this.context!!, lv_weeklyHabbit, groupList, childList)
        lv_weeklyHabbit.setAdapter(expandableListAdapter)

        for (i in 0 until expandableListAdapter.getGroupCount())
            lv_weeklyHabbit.expandGroup(i)


        return thisView
    }

    fun init() {
        groupList = ArrayList<String>()                         // 그룹 이름(item), header
        childList = ArrayList<ArrayList<String>>()              // 그룹 리스트 (subitem 목록), body
        childListContent = ArrayList<ArrayList<String>>()     // subitem 내용들
    }

    // TODO 점수 받아와서 보여주기
    fun getScore() {
        val random = Random()
        val num = random.nextInt(101)

        tv_weeklyScore.text = num.toString() + " / 100 점"



    }

//    fun getDate(){
//        var today = LocalDateTime.now()
//        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE
//
//        var dates = ArrayList<LocalDateTime>()
//
//        for (i in 0..6) {
//            dates.add(today.minusDays(i.toLong()))
//            weekDates.add(dates[i].format(formatter))
//        }
//    }

    // TODO
    fun addToList() {

        var dates = ArrayList<LocalDateTime>()
        var weekDates = ArrayList<String>()

        var today = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE

        for (i in 0..6) {
            dates.add(today.minusDays(i.toLong()))
            weekDates.add(dates[i].format(formatter))
        }

        var time =  arrayOf("3:00", "2:30", "3:25", "2:17")    // 양치 시간, 나중에 받아오기
        var analysis = arrayOf("양치의 정석", "굿!!", "왼쪽을 더 열심히 닦도록,,", "엉망이야!",
                "가만히 서서 닦으세요", "너무 오래 닦음", "오른 쪽을 더 열심히 닦도록!!")     // 분석 내용, 나중에 받아오기

        // ArrayList에  값 저장
        for (i in 0..6) {       // 일주일
            var content = ArrayList<String>()   // 하루
            for (j in 0..2) {   // 하루하루 양치 횟수...? - 일단은 모두 3번
                content.add(time[(i+j)%4].toString() + "\n" + analysis[(i*j + i + j)%7])
//                childListContent[i][j] = times[(i*j)%4].toString() + "\n" + analysis[(i+j)%4]
            }
            childListContent.add(content)
            childList.add(childListContent[i])
        }

//        // 오늘부터 일주일 전까지 날짜 찾아서 그에 해당하는 기록들 저장
//        for (i in 0 until DataCenter.records.size) {
//            var record = DataCenter.records[i]
//            var curDate = today.minusDays(i.toLong())
//
//        }

        for (i in 0..6)
            groupList.add(weekDates[i])
    }



    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}