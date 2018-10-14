package com.example.administrator.achi.dataModel

import java.time.LocalDateTime
import java.util.*

class Record {
    var date : LocalDateTime
        private set
    var duration : Int = -1
        private set
    var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
        private set
    var bad_pressure : Int = 0
        private set
    var score : Int = 100
        private set
    var comment : String = "여기는 양치에 대한 코멘트입니당"
        private set

    constructor() {
        val random : Random = Random()

        DataCenter.j++
        if (DataCenter.j > 2) {
            DataCenter.i++
            DataCenter.j = 0
        }

        this.date = LocalDateTime.now().minusDays(DataCenter.i.toLong())
        this.duration = random.nextInt(300)
//        this.sec_per_tooth = spt
        this.bad_pressure = random.nextInt(29)
        this.score = random.nextInt(101)
        this.comment = date.toString() + "\t\t\t" + DataCenter.j.toString()
    }

    constructor(date : LocalDateTime, time : Int, spt : Array<Int>, bp : Int, score : Int, comment : String) {
        this.date = date
        this.duration = time
        this.sec_per_tooth = spt
        this.bad_pressure = bp
        this.score = score
        this.comment = comment
    }

    // just for test
    fun printRecord() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println(this.date.toString())
        println(this.duration)
        println(this.sec_per_tooth[20])
        println(this.bad_pressure)
        println(this.score)
        println(this.comment)
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    }

}