package com.example.administrator.achi.fragment

import java.time.LocalDateTime
import java.util.*

class Record {
    var date : LocalDateTime
        get() {return date}
        private set
    var duration : Int = -1
        get() {return duration}
        private set
    var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
        get() {return sec_per_tooth}
        private set
    var bad_pressure : Int = 0
        get() {return bad_pressure}
        private set
    var score : Int = 100
        get() {return score}
        private set
    var comment : String = "여기는 양치에 대한 코멘트입니당"
        get() {return comment}
        private set

    constructor() {
        val random : Random = Random()

        this.date = LocalDateTime.now()
        this.duration = random.nextInt(300000)
//        this.sec_per_tooth = spt
        this.bad_pressure = random.nextInt(29)
        this.score = random.nextInt(101)
//        this.comment = comment
    }

    constructor(date : LocalDateTime, time : Int, spt : Array<Int>, bp : Int, score : Int, comment : String) {
        this.date = date
        this.duration = time
        this.sec_per_tooth = spt
        this.bad_pressure = bp
        this.score = score
        this.comment = comment
    }

}