package com.example.administrator.achi.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.Analyzer
import com.example.administrator.achi.dataModel.DataCenter
import com.example.administrator.achi.dataModel.Record
import com.example.administrator.achi.expandableList.ChildContentFormat
import com.example.administrator.achi.expandableList.ExpandableListAdapter
import kotlinx.android.synthetic.main.fragment_analyzehabit.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AnalyzeHabitFragment : Fragment(){
    private val TAG = "AnalyzeHabitFragment"
    private var thisView: View? = null


    private lateinit var expandableListAdapter : ExpandableListAdapter

    private var groupList = ArrayList<String>()                         // 그룹 이름(item), header
    private var childList = ArrayList<ArrayList<ChildContentFormat>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
    private var childListContent = ArrayList<ArrayList<ChildContentFormat>>()     // subitem 내용들, record 하나

    private var today = LocalDateTime.now()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        for (i in 0 until expandableListAdapter.getGroupCount())
            lvWeeklyHabbit.expandGroup(i)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")

        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_analyzehabit, container, false)
        }

        return thisView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var sample_record : Record = Record()
        DataCenter.records.add(sample_record)

        init()

        getScore()
        addToList()

        // Expandable List View
        expandableListAdapter = ExpandableListAdapter(this.context!!, lvWeeklyHabbit, groupList, childList)
        lvWeeklyHabbit.setAdapter(expandableListAdapter)

        for (i in 0 until expandableListAdapter.getGroupCount())
            lvWeeklyHabbit.expandGroup(i)

    }

    fun init() {
        groupList = ArrayList<String>()                         // 그룹 이름(item), header
        childList = ArrayList<ArrayList<ChildContentFormat>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
        childListContent = ArrayList<ArrayList<ChildContentFormat>>()     // subitem 내용들, record 하나
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
        tvWeeklyScore.text = avg.toString() + " / 100 점"
    }

    fun addToList() {

        var weekDates = ArrayList<String>()

        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE
        var checkFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var timeFormatter = DateTimeFormatter.ofPattern("hh:mm")


        // 전체 기록 날짜별로 저장
        var j = 0
        var curDate = today
        var curDateFormat = curDate.format(checkFormatter)
        var content = ArrayList<ChildContentFormat>()
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
                content = ArrayList<ChildContentFormat>()
            }

            content.add(ChildContentFormat(Analyzer.timeToString(record.duration) , record.score, record.comment, record.date.format(timeFormatter)))
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