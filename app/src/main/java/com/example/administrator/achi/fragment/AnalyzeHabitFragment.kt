package com.example.administrator.achi.fragment

import android.os.Bundle
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
    private var childList = ArrayList<ArrayList<String>>()              // 그룹 리스트 (subitem 목록), body
    private var childListContent = ArrayList<ArrayList<String>>()     // subitem 내용들

    private var weekDates = ArrayList<String>()



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


        //        dataCenter = DataCenter.getInstance()
        var sample_record : Record = Record()
        DataCenter.addRecord(sample_record)


        init_elv()

        getScore()

        getDate()
        addToList()

        // Expandable List View
        val expandableListAdapter = com.example.administrator.achi.fragment.ExpandableListAdapter(this.context!!, lv_weeklyHabbit, groupList, childList)
        lv_weeklyHabbit.setAdapter(expandableListAdapter)

        for (i in 0..expandableListAdapter.getGroupCount()-1)
            lv_weeklyHabbit.expandGroup(i)


        return thisView
    }

    fun init_elv() {
        groupList = ArrayList<String>()                         // 그룹 이름(item), header
        childList = ArrayList<ArrayList<String>>()              // 그룹 리스트 (subitem 목록), body
        childListContent = ArrayList<ArrayList<String>>()     // subitem 내용들

        weekDates = ArrayList<String>()
    }

    // TODO 점수 받아와서 보여주기
    fun getScore() {
        val random = Random()
        val num = random.nextInt(101)

        tv_weeklyScore.text = num.toString() + " / 100 점"
    }

    fun getDate(){
        var today = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE

        var dates = ArrayList<LocalDateTime>()

        for (i in 0..6) {
            dates.add(today.minusDays(i.toLong()))
            weekDates.add(dates[i].format(formatter))
        }
    }

    // TODO
    fun addToList() {

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

        for (i in 0..6)
            groupList.add(weekDates[i])
    }



    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}