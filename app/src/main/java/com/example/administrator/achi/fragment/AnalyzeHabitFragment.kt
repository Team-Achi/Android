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

    // TODO 점수 받아와서 보여주기
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

    // TODO
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

            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            println(record.date.format(checkFormatter) + "\t\t" +record.duration)
            println(curDateFormat)
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

            if (record.date.format(checkFormatter).compareTo(curDateFormat) != 0) {
                println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
                curDate = curDate.minusDays(1)
                curDateFormat =curDate.format(checkFormatter)

                weekDates.add(curDate.format(formatter))
                childListContent.add(content)
                childList.add(childListContent[j])
                j++
                content = ArrayList<String>()

                println("_______________________________")
                println(curDateFormat)
                println("_______________________________")
            }
            content.add((record.duration/60).toString() + ":" + (record.duration%60).toString() +
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