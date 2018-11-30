package com.example.administrator.achi.dataModel

import java.time.LocalDateTime

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