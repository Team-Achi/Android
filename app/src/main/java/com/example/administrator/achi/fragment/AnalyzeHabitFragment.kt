package com.example.administrator.achi.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.DataCenter
import com.example.administrator.achi.dataModel.Record
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
    private lateinit var expandableListAdapter : com.example.administrator.achi.fragment.ExpandableListAdapter

    private var groupList = ArrayList<String>()                         // 그룹 이름(item), header
    private var childList = ArrayList<ArrayList<String>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
    private var childListContent = ArrayList<ArrayList<String>>()     // subitem 내용들, record 하나

    private var today = LocalDateTime.now()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        for (i in 0 until expandableListAdapter.getGroupCount())
            lv_weeklyHabbit.expandGroup(i)
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
        DataCenter.records.add(sample_record)

        init()

        getScore()

        addToList()

        // Expandable List View
        expandableListAdapter = com.example.administrator.achi.fragment.ExpandableListAdapter(this.context!!, lv_weeklyHabbit, groupList, childList)
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

    fun getScore() {
        var lastWeekDate = today.minusDays(7)
        var avg = 0
        var num = 0

        for (i in 0 until DataCenter.records.size) {
            var record = DataCenter.records[i]
            if (record.date.isAfter(lastWeekDate)) {
                avg += record.score
                num++
            }
            else
                break
        }
        avg = avg / num
        tv_weeklyScore.text = avg.toString() + " / 100 점"
    }

    fun addToList() {

        var dates = ArrayList<LocalDateTime>()
        var weekDates = ArrayList<String>()

        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE
        var checkFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


        // 전체 기록 날짜별로 저장
        var j = 0
        var curDate = today
        var curDateFormat = curDate.format(checkFormatter)
        var content = ArrayList<String>()
        weekDates.add(today.format(formatter))

        for (i in 0 until DataCenter.records.size) {
            var record = DataCenter.records[i]

            if (record.date.format(checkFormatter).compareTo(curDateFormat) != 0) {
                curDate = curDate.minusDays(1)
                curDateFormat =curDate.format(checkFormatter)

                weekDates.add(curDate.format(formatter))
                childListContent.add(content)
                childList.add(childListContent[j])
                j++
                content = ArrayList<String>()
            }

            // second 표현하는 방법 간단하게 찾기
            var second = record.duration % 60
            var strSecond : String
            if (second < 10)
                strSecond = "0" + second.toString()
            else
                strSecond = second.toString()

            content.add((record.duration/60).toString() + ":" + strSecond +
                    "\t\t\t\t\t\t\t\t점수 : " + record.score.toString() + "\n" + record.comment)
        }
        childListContent.add(content)
        childList.add(childListContent[j])

        for (i in 0 until weekDates.size)
            groupList.add(weekDates[i])
    }



    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}