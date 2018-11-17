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
import com.example.administrator.achi.dataModel.WeeklyFeature
import com.example.administrator.achi.expandableList.ExpandableListAdapter
import com.example.administrator.achi.expandableList.ExpandableListRecords
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
    private var dayList = ArrayList<ExpandableListRecords>()             // 하루 record

    private lateinit var thisWeek : WeeklyFeature
    private lateinit var lastWeek : WeeklyFeature

    private var today = LocalDateTime.now()


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")

        for (i in 0 until expandableListAdapter.groupCount)
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

//        val random: Random = Random()
//        var date : LocalDateTime
//        var elapsed_time = 0
//        var bad_pressure = 0
//        var sec_per_tooth = Array<Int>(50, {0})
//        var avgTime = 0
//
//        date = LocalDateTime.now()
//        elapsed_time = random.nextInt(180) + 60
//        bad_pressure = random.nextInt(6)
//
//        avgTime = elapsed_time / 28
//
//        for (i in 11..47) {
//            if (i != 18 ||i != 19 ||i != 20 ||i != 28 ||i != 29 ||i != 30 ||i != 38 || i != 39 ||i != 40) {
//                sec_per_tooth[i] = avgTime + (random.nextInt(7) - 3)
//            }
//        }
//
//        Analyzer.analyzeSample(date, elapsed_time, sec_per_tooth, bad_pressure)

        getScore()
        compareWeeks()

        init()
        addToList()

        // Expandable List View
        expandableListAdapter = ExpandableListAdapter(this.context!!, groupList, dayList)
        lvWeeklyHabit.setAdapter(expandableListAdapter)

        for (i in 0 until expandableListAdapter.groupCount)
            lvWeeklyHabit.expandGroup(i)

    }

    private fun init() {
        groupList = ArrayList<String>()                         // 그룹 이름(item), header
        dayList = ArrayList<ExpandableListRecords>()            // 하루
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
        avg /= num
        tvWeeklyScore.text = avg.toString() + " / 100 점"
    }

    // 횟수, 시간 평균, 평균 점수
    private fun getWeeklyFeature(weekNum : Int) {
        var firstWeekDate = today
        var lastWeekDate = today

        if (weekNum == 1) {
            firstWeekDate = today.plusDays(1)
            lastWeekDate = today.minusDays(7)
        }
        else if (weekNum == 2) {
            firstWeekDate = today.minusDays(6)
            lastWeekDate = today.minusDays(14)
        }

        var avgScore = 0
        var avgTime = 0
        var num = 0
        var check = false

        for (i in 0 until DataCenter.records.size) {
            var record = DataCenter.records[i]
            if (record.date.isBefore(firstWeekDate) && record.date.isAfter(lastWeekDate)) {
                avgScore += record.score
                avgTime += record.duration
                num++
                check = true
            }
            else if (check)
                break
        }
        avgScore /= num
        avgTime /= num

        if (weekNum == 1) {
            thisWeek = WeeklyFeature(num, avgScore, avgTime)
        }
        else if (weekNum == 2) {
            lastWeek = WeeklyFeature(num, avgScore, avgTime)
        }
    }

    private fun compareWeeks() {
        var comment : String = ""
        getWeeklyFeature(1)
        getWeeklyFeature(2)

        when {
            thisWeek.avgScore < lastWeek.avgScore -> comment += "올바른 양치 횟수가 줄었어요.\n"
            else -> comment += "올바른 양치 횟수가 늘고 있네요!\n"
        }

        when {
            thisWeek.num < thisWeek.num -> comment += "저번주보다 양치 횟수가 줄었네요."
            else -> comment += "저번주보다 양치 횟수가 늘었어요."
        }

        when {
            thisWeek.avgTime < lastWeek.avgTime -> comment += ""
            else -> comment += ""
        }

        tvWeeklyComment.text = comment

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
                dayList.add(ExpandableListRecords(startIdx, i - 1))

                curDate = curDate.minusDays(1)
                curDateFormat =curDate.format(checkFormatter)
                startIdx = i
            }
        }
        groupList.add(curDate.format(formatter))
        dayList.add(ExpandableListRecords(startIdx, DataCenter.records.size - 1))
    }

    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}