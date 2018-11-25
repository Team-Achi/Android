package com.example.administrator.achi.dataModel

import android.util.Log
import java.time.LocalDateTime
import java.util.*

class Record {
    val TAG = "RECORD"

    var date : LocalDateTime = LocalDateTime.now()
        private set
    var duration : Double = 0.0
        private set
    var cnt_per_tooth : Array<Int> = Array<Int>(50,{0})
        private set
    var section_time : Array<Int> = Array<Int>(6,{0})
        private set
    var high_pressure : Int = 0
        private set
    var low_pressure : Int = 0
        private set
    var score : Int = 100
        private set
    var comment : String = "여기는 양치에 대한 코멘트입니당"
        private set

    constructor(date : LocalDateTime, time : Double, cpt : Array<Int>, st : Array<Int>, hp : Int, lp : Int, score : Int, comment : String) {
        this.date = date
        this.duration = time
        this.cnt_per_tooth = cpt
        this.section_time = st
        this.high_pressure = hp
        this.low_pressure = lp
        this.score = score
        this.comment = comment

        this.toString()
    }

    constructor(dataString: String) {
        // parse string data
    }

    override fun toString() : String {
        var s = String()
        s += date.toString() + "/"
        s += duration.toString() + "/"

//        s += cnt_per_tooth.toString() + "/"
        for (count in cnt_per_tooth) {
            s += "$count,"
        }
        s= s.dropLast(1)
        s += "/"

//        s += section_time.toString() + "/"
        for (time in section_time) {
            s += "$time,"
        }
        s= s.dropLast(1)
        s += "/"

        s += high_pressure.toString() + "/"
        s += low_pressure.toString() + "/"
        s += score.toString() + "/"
        s += comment
        
        Log.i(TAG, s)

        return s
    }


    // just for test
    fun printRecord() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println(this.date.toString())
        println(this.duration)
        println(this.cnt_per_tooth[21])
        println(this.high_pressure)
        println(this.low_pressure)
        println(this.score)
        println(this.comment)
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    }

}