package com.example.administrator.achi.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.fragment_analyzehabit.view.*

class AnalyzeHabitFragment : Fragment(){
    private val TAG = "AnalyzeHabitFragment"
    private var thisView: View? = null

    private lateinit var tv_weeklyScore : TextView
    private lateinit var lv_weeklyHabbit : ExpandableListView

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
        lv_weeklyHabbit = thisView!!.findViewById(R.id.lvWeeklyHabbit)

        return thisView
    }



    companion object {
        @JvmStatic
        fun newInstance() = AnalyzeHabitFragment()
    }

}