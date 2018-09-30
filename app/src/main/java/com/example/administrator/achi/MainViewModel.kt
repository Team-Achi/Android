package com.example.administrator.achi

import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentManager

class MainViewModel : ViewModel(){
    val TAG = "MainViewModel"

    lateinit var fragmentManager: FragmentManager

}