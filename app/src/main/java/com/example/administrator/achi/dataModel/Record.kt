package com.example.administrator.achi.dataModel

import java.time.LocalDateTime
import android.util.Log

data class Record(var date : LocalDateTime, var duration : Double, var cnt_per_tooth : Array<Int>,
                  var section_time : Array<Int>, var high_pressure: Int, var low_pressure : Int,
                  var score : Int, var comment : String) {

    val TAG = "RECORD"

    companion object {
        fun instance(data: String) : Record? {
            val dataArray:List<String> = data.split("/")
            if (dataArray.size != 8) {
                return null
            }

            val date = LocalDateTime.parse(dataArray[0])
            val duration = dataArray[1].toDouble()

            val sCPT:String = dataArray[2].toString()
            val temp = sCPT.split(",")
            val cnt_per_tooth = Array<Int>(50, {0})
            var ctr = 0
            for (index in Analyzer.TEETH_INDICES) {
                cnt_per_tooth[index] = temp[ctr].toInt()
            }

            val sST = dataArray[3].toString()
            val temp2 = sST.split(",")
            val section_time = Array<Int>(6, {0})
            ctr = 0
            for (i in temp2) {
                section_time[ctr] = i.toInt()
            }

            val high_pressure = dataArray[4].toInt()
            val low_pressure = dataArray[5].toInt()
            val score: Int = dataArray[6].toInt()
            val comment :String = dataArray[7].toString()

            return Record(date, duration, cnt_per_tooth, section_time, high_pressure, low_pressure, score, comment)
        }
    }

    override fun toString() : String {
        var s = String()
        s += date.toString() + "/"
        s += duration.toString() + "/"
//        s += cnt_per_tooth.toString() + "/"

        for (count in cnt_per_tooth) {
            s += "$count,"
        }
        s = s.dropLast(1)
        s += "/"

//        s += section_time.toString() + "/"
        for (time in section_time) {
            s += "$time,"
        }
        s = s.dropLast(1)
        s += "/"

        s += high_pressure.toString() + "/"
        s += low_pressure.toString() + "/"
        s += score.toString() + "/"
        s += comment
        Log.i(com.example.administrator.achi.dataModel.TAG, "comment: $comment")

        return s
    }

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

    enum class ContentIndex {
        DATE, DURATION, CNT_PER_TOOTH, SECTION_TIME, HIGH_PRESSURE, LOW_PRESSURE, SCORE, COMMENT
    }

}