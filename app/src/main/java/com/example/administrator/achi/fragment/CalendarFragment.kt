package com.example.administrator.achi.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrator.achi.MainViewModel
import com.example.administrator.achi.R

class CalendarFragment : Fragment(){

    private val TAG = "CalendarFragment"
    private var thisView: View? = null
    private lateinit var mainViewModel : MainViewModel

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
            return thisView

        }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}