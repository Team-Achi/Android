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
        val agenda1 = Agenda(2018, 10, 1, Color.parseColor("#E74C3C"))
        val agenda2 = Agenda(2018, 10, 1, Color.parseColor("#3498DB"))
      //  val agenda3 = Agenda(2018, 10, 1, Color.parseColor("#1ABC9D"))

        val agenda4 = Agenda(2018, 10, 2, Color.parseColor("#E74C3C"))
        val agenda5 = Agenda(2018, 10, 2, Color.parseColor("#3498DB"))
        val agenda6 = Agenda(2018, 10, 2, Color.parseColor("#1ABC9D"))

        val agenda7 = Agenda(2018, 10, 3, Color.parseColor("#E74C3C"))
        val agenda8 = Agenda(2018, 10, 3, Color.parseColor("#3498DB"))
      //  val agenda9 = Agenda(2018, 10, 3, Color.parseColor("#1ABC9D"))

        val agenda10 = Agenda(2018, 10, 4, Color.parseColor("#E74C3C"))
        val agenda11 = Agenda(2018, 10, 4, Color.parseColor("#3498DB"))
        val agenda12 = Agenda(2018, 10, 4, Color.parseColor("#1ABC9D"))

        val agenda13 = Agenda(2018, 10, 5, Color.parseColor("#E74C3C"))
        val agenda14 = Agenda(2018, 10, 5, Color.parseColor("#3498DB"))
      //  val agenda15 = Agenda(2018, 10, 5, Color.parseColor("#1ABC9D"))

        val agenda16 = Agenda(2018, 10, 6, Color.parseColor("#E74C3C"))
        val agenda17 = Agenda(2018, 10, 6, Color.parseColor("#3498DB"))
        val agenda18 = Agenda(2018, 10, 6, Color.parseColor("#1ABC9D"))

        val agenda19 = Agenda(2018, 10, 7, Color.parseColor("#E74C3C"))
        val agenda20 = Agenda(2018, 10, 7, Color.parseColor("#3498DB"))
        val agenda21 = Agenda(2018, 10, 7, Color.parseColor("#1ABC9D"))

        val agenda22 = Agenda(2018, 10, 8, Color.parseColor("#E74C3C"))
        val agenda23 = Agenda(2018, 10, 8, Color.parseColor("#3498DB"))
        val agenda24 = Agenda(2018, 10, 8, Color.parseColor("#1ABC9D"))

        val agenda25 = Agenda(2018, 10, 9, Color.parseColor("#E74C3C"))
        val agenda26 = Agenda(2018, 10, 9, Color.parseColor("#3498DB"))
      //  val agenda27 = Agenda(2018, 10, 9, Color.parseColor("#1ABC9D"))

        val agenda28 = Agenda(2018, 10, 10, Color.parseColor("#E74C3C"))
        val agenda29 = Agenda(2018, 10, 10, Color.parseColor("#3498DB"))
    //    val agenda30 = Agenda(2018, 10, 10, Color.parseColor("#1ABC9D"))

        val agenda31 = Agenda(2018, 10, 11, Color.parseColor("#E74C3C"))
        val agenda32 = Agenda(2018, 10, 11, Color.parseColor("#3498DB"))
        val agenda33 = Agenda(2018, 10, 11, Color.parseColor("#1ABC9D"))

        val agenda34 = Agenda(2018, 10, 12, Color.parseColor("#E74C3C"))
        val agenda35 = Agenda(2018, 10, 12, Color.parseColor("#3498DB"))
        val agenda36 = Agenda(2018, 10, 12, Color.parseColor("#1ABC9D"))

        val agenda37 = Agenda(2018, 10, 13, Color.parseColor("#E74C3C"))
        val agenda38 = Agenda(2018, 10, 13, Color.parseColor("#3498DB"))
        val agenda39 = Agenda(2018, 10, 13, Color.parseColor("#1ABC9D"))

        val agenda40 = Agenda(2018, 10, 14, Color.parseColor("#E74C3C"))
        val agenda41 = Agenda(2018, 10, 14, Color.parseColor("#3498DB"))
       // val agenda42 = Agenda(2018, 10, 14, Color.parseColor("#1ABC9D"))

//3 - 8    2 - 6

        agendaList.add(agenda1)
        agendaList.add(agenda2)
    //    agendaList.add(agenda3)
        agendaList.add(agenda4)
        agendaList.add(agenda5)
        agendaList.add(agenda6)
        agendaList.add(agenda7)
        agendaList.add(agenda8)
    //    agendaList.add(agenda9)
        agendaList.add(agenda10)
        agendaList.add(agenda11)
        agendaList.add(agenda12)
        agendaList.add(agenda13)
        agendaList.add(agenda14)
   //     agendaList.add(agenda15)
        agendaList.add(agenda16)
        agendaList.add(agenda17)
        agendaList.add(agenda18)
        agendaList.add(agenda19)
        agendaList.add(agenda20)
        agendaList.add(agenda21)
        agendaList.add(agenda22)
        agendaList.add(agenda23)
        agendaList.add(agenda24)
        agendaList.add(agenda25)
        agendaList.add(agenda26)
     //   agendaList.add(agenda27)
        agendaList.add(agenda28)
        agendaList.add(agenda29)
    //    agendaList.add(agenda30)
        agendaList.add(agenda31)
        agendaList.add(agenda32)
        agendaList.add(agenda33)
        agendaList.add(agenda34)
        agendaList.add(agenda35)
        agendaList.add(agenda36)
        agendaList.add(agenda37)
        agendaList.add(agenda38)
        agendaList.add(agenda39)
        agendaList.add(agenda40)
        agendaList.add(agenda41)
      //  agendaList.add(agenda42)

        calendarView.agendaList = agendaList
            return thisView

        }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}



