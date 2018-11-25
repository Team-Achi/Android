package com.example.administrator.achi.dataModel

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Record {
    companion object {
        fun instance(data: String) : Record? {
            val dataArray:List<String> = data.split("/")
            if (dataArray.size != 8) {
                return null
            }
            val date = LocalDateTime.parse(dataArray[ContentIndex.DATE.ordinal])
            val duration = dataArray[ContentIndex.DURATION.ordinal].toDouble()

            val sCPT:String = data[ContentIndex.CNT_PER_TOOTH.ordinal].toString()
            val temp = sCPT.split(",")

            val cnt_per_tooth = Array<Int>(50, {0})
            var ctr = 0
            for (index in Analyzer.TEETH_INDICES) {
                cnt_per_tooth[index] = temp[ctr].toInt()
            }

            val sST = data[ContentIndex.SECTION_TIME.ordinal].toString()
            val temp2 = sST.split(",")
            val section_time = Array<Int>(6, {0})
            ctr = 0
            for (i in temp2) {
                section_time[ctr] = i.toInt()
            }
            val high_pressure = data[ContentIndex.HIGH_PRESSURE.ordinal].toInt()
            val low_pressure = data[ContentIndex.LOW_PRESSURE.ordinal].toInt()
            val score: Int = data[ContentIndex.SCORE.ordinal].toInt()
            val comment :String = data[ContentIndex.COMMENT.ordinal].toString()

            return Record(date, duration, cnt_per_tooth, section_time, high_pressure, low_pressure, score, comment)
        }
    }
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

    enum class ContentIndex {
        DATE, DURATION, CNT_PER_TOOTH, SECTION_TIME, HIGH_PRESSURE, LOW_PRESSURE, SCORE, COMMENT
    }
}

