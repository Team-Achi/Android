package com.example.administrator.achi.fragment

import java.time.LocalDateTime

class Record {
    var date : LocalDateTime
        get() {return date}
    var score : Int = 100
        get() {return score}
    var duration : Int = -1
        get() {return duration}
    var sec_per_tooth : Array<Int> = Array<Int>(50,{0})
        get() {return sec_per_tooth}
    var bad_pressure : Int = 0
        get() {return bad_pressure}
    var comment : String = "여기는 양치에 대한 코멘트입니당"
        get() {return comment}

    constructor(date : LocalDateTime, score : Int, time : Int, spt : Array<Int>, bp : Int, comment : String) {
        this.date = date
        this.score = score
        this.duration = time
        this.sec_per_tooth = spt
        this.bad_pressure = bp
        this.comment = comment
    }

}