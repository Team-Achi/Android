package com.example.administrator.achi.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.DataCenter
import com.example.administrator.achi.dataModel.Record
import com.example.administrator.achi.expandableList.ExpandableListAdapter
import com.example.administrator.achi.expandableList.ExpandableRecords
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
//    private var childList = ArrayList<ArrayList<ChildContentFormat>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
//    private var childListContent = ArrayList<ArrayList<ChildContentFormat>>()     // subitem 내용들, record 하나


    private var dayList = ArrayList<ExpandableRecords>()                     // 하루 record

    private var today = LocalDateTime.now()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        for (i in 0 until expandableListAdapter.getGroupCount())
            lvWeeklyHabit.expandGroup(i)
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

        getScore()

        init()
        addToList()

        // Expandable List View
        expandableListAdapter = ExpandableListAdapter(this.context!!, lvWeeklyHabit, groupList, dayList)
        lvWeeklyHabit.setAdapter(expandableListAdapter)

        for (i in 0 until expandableListAdapter.getGroupCount())
            lvWeeklyHabit.expandGroup(i)

//        rlvHabbit.adapter = RecyclerAdapter(dayList, context)
//        rlvHabbit.layoutManager = LinearLayoutManager(context)

    }

    private fun init() {
        groupList = ArrayList<String>()                         // 그룹 이름(item), header
//        childList = ArrayList<ArrayList<ChildContentFormat>>()              // 그룹 리스트 (subitem 목록), body, 하루 record
//        childListContent = ArrayList<ArrayList<ChildContentFormat>>()     // subitem 내용들, record 하나
        dayList = ArrayList<ExpandableRecords>()                                 // 하루
    }

    private fun getScore() {
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

    private fun addToList() {

        var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)      // ISO_LOCAL_DATE
        var checkFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


        // 전체 기록 날짜별로 저장
        var j = 0
        var curDate = today
        var curDateFormat = curDate.format(checkFormatter)
        var startIdx = 0

        for (i in 0 until DataCenter.records.size) {
            var record = DataCenter.records[i]

            if (record.date.format(checkFormatter).compareTo(curDateFormat) != 0) {
                groupList.add(curDate.format(formatter))
                dayList.add(ExpandableRecords(startIdx, i - 1))

                curDate = curDate.minusDays(1)
                curDateFormat =curDate.format(checkFormatter)
                startIdx = i
            }
        }
        groupList.add(curDate.format(formatter))
        dayList.add(ExpandableRecords(startIdx, DataCenter.records.size - 1))
    }

    private fun addToList2() {
        var checkFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dayList = ArrayList<ExpandableRecords>()

        var curDate = DataCenter.records[0].date
        var curDateFormat = curDate.format(checkFormatter)
        var startIdx = 0

        DataCenter.printRecords()

        for(i in 0 until DataCenter.records.size) {
            if (DataCenter.records[i].date.format(checkFormatter).compareTo(curDateFormat) != 0) {
                dayList.add(ExpandableRecords(startIdx, i - 1))

                curDate = curDate.minusDays(1)
                curDateFormat =curDate.format(checkFormatter)
                startIdx = i
            }
        }
        dayList.add(ExpandableRecords(startIdx, DataCenter.records.size - 1))
    }



    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}