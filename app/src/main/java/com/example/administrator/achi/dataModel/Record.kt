package com.example.administrator.achi.dataModel

import java.time.LocalDateTime
import java.util.*

class Record {
    var date : LocalDateTime
        private set
    var duration : Int = -1
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

    constructor() {
        val random : Random = Random()

        this.date = LocalDateTime.now()
        this.duration = random.nextInt(240)
        this.high_pressure = random.nextInt(10)
        this.low_pressure = random.nextInt(10)
        this.score = random.nextInt(101)
    }

    constructor(date : LocalDateTime, time : Int, spt : Array<Int>, st : Array<Int>, hp : Int, lp : Int, score : Int, comment : String) {
        this.date = date
        this.duration = time
        this.cnt_per_tooth = spt
        this.section_time = st
        this.high_pressure = hp
        this.low_pressure = lp
        this.score = score
        this.comment = comment
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