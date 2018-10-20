package com.example.administrator.achi.fragment

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.administrator.achi.MainViewModel
import com.example.administrator.achi.R
import com.example.administrator.achi.calendar.CalendarView
import com.example.administrator.achi.calendar.Agenda
import com.example.administrator.achi.dataModel.DataCenter
import kotlinx.android.synthetic.main.fragment_calendar.*


class CalendarFragment : Fragment(){

    private val TAG = "CalendarFragment"
    private var thisView: View? = null
    private lateinit var mainViewModel : MainViewModel
    private  var previousYear : Int = 0;
    private  var previousMonth : Int = 0;
    private  var previousDay : Int = 0;
    private var onetime : Int = 0;
    private var twotime : Int = 0;
    private var threetime : Int = 0;
    private var alltime : Int = 0;
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "thisView == null")



        if(thisView == null) {
            thisView = inflater.inflate(R.layout.fragment_calendar, container, false)

        }


        var calendarView = thisView!!.findViewById<CalendarView>(R.id.calendarView)
        val agendaList = ArrayList<Agenda>()

        calendarView.onCalendarSwipedListener = (object : CalendarView.OnCalendarSwipedListener {
            override fun onCalendarSwiped(year: Int, month: Int) {
                Log.i("swiped", year.toString() + "/" + month)
                onetime = 0;
                twotime = 0;
                threetime = 0;
                alltime = 0;

                for (i in 0 until agendaList.size) {
                    if (agendaList[i].year == year && agendaList[i].month == month) {
                        if (agendaList[i].color == Color.parseColor("#1ABC9D")) {
                            threetime++
                        }
                        if (agendaList[i].color == Color.parseColor("#3498DB")) {
                            twotime++
                        }
                        if (agendaList[i].color == Color.parseColor("#E74C3C")) {
                            onetime++
                        }
                    }
                }
                twotime = twotime - threetime
                onetime = onetime - threetime - twotime
                alltime = onetime + (2*twotime) + (3*threetime)

                var comment = thisView!!.findViewById<TextView>(R.id.calendarcoment)
                var three = "1일 3회 양치 횟수 : "
                var two = "\n1일 2회 양치 횟수 : "
                var all = "\n총 양치횟수 : "
                var comments = three + threetime.toString()+"회" + two + twotime.toString()+"회" + all + alltime.toString()+"회"
                comment.text = comments
                calendarView.agendaList = agendaList
            }
        })

        calendarView.onCalendarClickedListener = (object : CalendarView.OnCalendarClickedListener{
            override fun onCalendarClicked(year: Int, month: Int, day: Int) {
                Log.i("clicked", year.toString() + "/" + month + "/" + day)
                calendarView.resetColor(previousYear, previousMonth, previousDay)
                calendarView.highlight(year, month, day, Color.WHITE, Color.parseColor("#3498DB"))
                previousYear = year
                previousMonth = month
                previousDay = day

                if (calendarView.getCurrentYear() < year) {
                    calendarView.moveToNext()
                } else if (calendarView.getCurrentYear() > year) {
                    calendarView.moveToPrevious()
                } else if (calendarView.getCurrentMonth() < month) {
                    calendarView.moveToNext()
                } else if (calendarView.getCurrentMonth() > month) {
                    calendarView.moveToPrevious()
                }
            }
        })

        for (i in 0 until DataCenter.records.size) {
            var record = DataCenter.records[i]
            if(i == 0){
                val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#E74C3C"));
                agendaList.add(agenda);
            }
            else if(i ==1){
                var prevrecord = DataCenter.records[i-1];
                if(prevrecord.date.year == record.date.year && prevrecord.date.monthValue == record.date.monthValue && prevrecord.date.dayOfMonth == record.date.dayOfMonth){
                    val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#3498DB"));
                    agendaList.add(agenda);
                }
               else{
                    val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#E74C3C"));
                    agendaList.add(agenda);
                }
            }
            else{
                var prevrecord = DataCenter.records[i-1];
                var prev2record = DataCenter.records[i-2];
                if(prev2record.date.year == record.date.year && prev2record.date.monthValue == record.date.monthValue && prev2record.date.dayOfMonth == record.date.dayOfMonth){
                    val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#1ABC9D"));
                    agendaList.add(agenda);
                }
                else if(prevrecord.date.year == record.date.year && prevrecord.date.monthValue == record.date.monthValue && prevrecord.date.dayOfMonth == record.date.dayOfMonth){
                    val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#3498DB"));
                    agendaList.add(agenda);
                }
                else{
                    val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#E74C3C"));
                    agendaList.add(agenda);
                }



            }
            //val agenda = Agenda(record.date.year, record.date.monthValue, record.date.dayOfMonth, Color.parseColor("#E74C3C"));
            //agendaList.add(agenda);
        }
   /*
        val agenda1 = Agenda(2018, 10, 1, Color.parseColor("#E74C3C"))
        val agenda2 = Agenda(2018, 10, 1, Color.parseColor("#3498DB"))
      //  val agenda3 = Agenda(2018, 10, 1, Color.parseColor("#1ABC9D"))


*/
        onetime = 0;
        twotime = 0;
        threetime = 0;
        alltime = 0;

        for (i in 0 until agendaList.size) {
            if (agendaList[i].year == calendarView.getCurrentYear() && agendaList[i].month == calendarView.getCurrentMonth()) {
                if (agendaList[i].color == Color.parseColor("#1ABC9D")) {
                    threetime++
                }
                if (agendaList[i].color == Color.parseColor("#3498DB")) {
                    twotime++
                }
                if (agendaList[i].color == Color.parseColor("#E74C3C")) {
                    onetime++
                }
            }
        }
        twotime = twotime - threetime
        onetime = onetime - threetime - twotime
        alltime = onetime + (2*twotime) + (3*threetime)

        var comment = thisView!!.findViewById<TextView>(R.id.calendarcoment)
        var three = "1일 3회 양치 횟수 : "
        var two = "\n1일 2회 양치 횟수 : "
        var all = "\n총 양치횟수 : "
        var comments = three + threetime.toString()+"회" + two + twotime.toString()+"회" + all + alltime.toString()+"회"
        comment.text = comments

        calendarView.agendaList = agendaList
            return thisView

        }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}



