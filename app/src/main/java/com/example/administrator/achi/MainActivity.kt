package com.example.administrator.achi

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.FrameLayout
import android.support.design.widget.BottomNavigationView
import android.util.Log
import com.example.administrator.achi.fragment.AnalyzeHabitFragment
import com.example.administrator.achi.fragment.CalendarFragment
import com.example.administrator.achi.fragment.MonitoringFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var frameLayout : FrameLayout
    lateinit var active : Fragment
    lateinit var fragmentManager: FragmentManager
    lateinit var mainViewModel : MainViewModel
    lateinit var analyzeHabitFragment: AnalyzeHabitFragment
    lateinit var monitoringFragment: MonitoringFragment
    lateinit var calendarFragment: CalendarFragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if ((item.itemId == R.id.navigation_analyzehabit && active == analyzeHabitFragment) ||
                (item.itemId == R.id.navigation_monitoring && active == monitoringFragment) ||
                (item.itemId == R.id.navigation_calendar && active == calendarFragment)) {
            return@OnNavigationItemSelectedListener false
        }
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        }
        when (item.itemId) {
            R.id.navigation_analyzehabit -> {
                active = analyzeHabitFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_monitoring -> {
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_layout, monitoringFragment, "monitoring")
                        .commit()
                active = monitoringFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_calendar -> {
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_layout, calendarFragment, "calendar")
                        .commit()
                active = calendarFragment
                return@OnNavigationItemSelectedListener true
            }
        }
        navigation.selectedItemId = item.itemId
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        fragmentManager = supportFragmentManager
        mainViewModel.fragmentManager = fragmentManager

        calendarFragment = CalendarFragment.newInstance()
        analyzeHabitFragment = AnalyzeHabitFragment.newInstance()
        monitoringFragment = MonitoringFragment.newInstance()

        setFragmentManager()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        frameLayout = frame_layout
    }

    private fun setFragmentManager() {
        fragmentManager.addOnBackStackChangedListener {
            Log.d(TAG, "back stack change " + fragmentManager.backStackEntryCount)
        }
        fragmentManager.beginTransaction().add(R.id.frame_layout, analyzeHabitFragment, "analyzeHabit").commit()
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
        active = analyzeHabitFragment
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "in back : " + fragmentManager.backStackEntryCount)
        if (fragmentManager.backStackEntryCount == 0) {
            Log.d(TAG, "back key pressed()")
            navigation.selectedItemId = R.id.navigation_analyzehabit
        }
    }

}
