package com.example.administrator.achi.fragment

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrator.achi.MainViewModel
import com.example.administrator.achi.R
import com.example.administrator.achi.calendar.CalendarView
import com.example.administrator.achi.calendar.Agenda


class CalendarFragment : Fragment(){

    private val TAG = "CalendarFragment"
    private var thisView: View? = null
    private lateinit var mainViewModel : MainViewModel
    private  var previousYear : Int = 0;
    private  var previousMonth : Int = 0;
    private  var previousDay : Int = 0;
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

        calendarView.onCalendarSwipedListener = (object : CalendarView.OnCalendarSwipedListener {
            override fun onCalendarSwiped(year: Int, month: Int) {
                Log.i("swiped", year.toString() + "/" + month)
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
        val agendaList = ArrayList<Agenda>()
        val agenda1 = Agenda(2018, 10, 3, Color.parseColor("#E74C3C"))
        val agenda2 = Agenda(2018, 10, 3, Color.parseColor("#3498DB"))
        val agenda3 = Agenda(2018, 10, 5, Color.parseColor("#1ABC9D"))
        val agenda4 = Agenda(2018, 10, 7, Color.parseColor("#E74C3C"))
        val agenda5 = Agenda(2018, 10, 7, Color.parseColor("#3498DB"))
        val agenda6 = Agenda(2018, 10, 7, Color.parseColor("#1ABC9D"))
        agendaList.add(agenda1)
        agendaList.add(agenda2)
        agendaList.add(agenda3)
        agendaList.add(agenda4)
        agendaList.add(agenda5)
        agendaList.add(agenda6)
        calendarView.agendaList = agendaList
            return thisView

        }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}



