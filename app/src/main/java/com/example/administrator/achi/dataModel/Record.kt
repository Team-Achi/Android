package com.example.administrator.achi.dataModel

import java.time.LocalDateTime
import java.util.*

data class Record(var date : LocalDateTime, var duration : Double, var cnt_per_tooth : Array<Int>, var section_time : Array<Int>, var high_pressure: Int, var low_pressure : Int, var score : Int, var comment : String) {


    // just for test
    fun printRecord() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println(this.date.toString())
        println(this.duration)
        println(this.cnt_per_tooth[21])
        println(this.section_time)
        println(this.high_pressure)
        println(this.low_pressure)
        println(this.score)
        println(this.comment)
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    }

}