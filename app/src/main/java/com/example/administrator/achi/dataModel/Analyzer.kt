package com.example.administrator.achi.dataModel

import android.util.Log
import java.time.LocalDateTime

const val TAG = "Analyzer"

private val THREE_MINUETS = 180
private val ONE_MINUTE = 60
private val NUMBER_OF_TEETH = 28
private const val UNIT_TIME : Int = 1

const val LESS = 0
const val OKAY = 1
const val MORE = 2


object Analyzer{
    val TEETH_INDICES = intArrayOf(11, 12, 13, 14, 15, 16, 17,
            21, 22, 23, 24, 25, 26, 27,
            31, 32, 33, 34, 35, 36, 37,
            41, 42, 43, 44, 45, 46, 47)

    private lateinit var today : LocalDateTime
    var elapsed_time : Int = 0
        private set
    private var count_per_tooth : Array<Int> = Array<Int>(50,{0})
    private var section_time : Array<Int> = Array<Int>(6,{0})
    private var bad_pressure : Int = 0
    private var score : Int = 100
    private var comment : String = ""

    private var expected_time_per_tooth : Int = THREE_MINUETS / (NUMBER_OF_TEETH)
    private var expected_count_per_tooth : Int = 0
    private var sumDiff : Array<Int> = Array<Int>(6,{0})

    var teethTimeComment : String = ""
    private val sectionName = arrayOf("위쪽 앞니", "위쪽 왼쪽 어금니", "위쪽 오른쪽 어금니",
            "아래쪽 앞니", "아래쪽 왼쪽 어금니", "아래쪽 오른쪽 어금니")


    // 이 함수는 어디에다가 넣어야 좋을까,,,,
    fun timeToString(time : Int) : String {
        var min = time / 60
        var sec =  time % 60

        var strMin : String
        var strSecond : String

        if (min < 10)
            strMin = "0" + min.toString()
        else
            strMin = min.toString()

        if (sec < 10)
            strSecond = "0" + sec.toString()
        else
            strSecond = sec.toString()

        return "$strMin:$strSecond"
    }

    private fun init() {
        today = LocalDateTime.now()
        elapsed_time = -1
        count_per_tooth = Array<Int>(50,{0})
        section_time = Array<Int>(6,{0})
        bad_pressure = 0
        score = 100
        comment = ""
        teethTimeComment = ""
    }


    fun countTooth(toothNum : Int) {
        count_per_tooth[toothNum]++
        elapsed_time += UNIT_TIME
    }
    fun isDone(tooth:Int) : Boolean {
        return count_per_tooth[tooth] >= expected_time_per_tooth
    }

    fun isHalfWayDone(tooth: Int) : Boolean {
        return count_per_tooth[tooth] >= (expected_time_per_tooth / 2)
    }

    fun pressure() {
        bad_pressure++
    }

    fun analyze(date : LocalDateTime) {
        today = date
        expected_count_per_tooth = elapsed_time / (NUMBER_OF_TEETH* UNIT_TIME)

        /* 인제 시간과 치아당 시간, bad_pressure을 기준으로
           점수 매기고 comment 저장 */

        calculate_duration()        // 총 양치 시간
        calculate_sec_per_tooth()   // 각 이빨 계산
        calculate_pressure()        // 압력
        set_comment()               // comment 저장

        if (score < 0)
            score = 0


        // record를 Record와 DataCenter에 저장
        var record = Record(today, elapsed_time, count_per_tooth, section_time, bad_pressure, score, comment)
        DataCenter.records.add(0, record)
        init()
    }

    // for sampleRecord
    fun analyzeSample(date : LocalDateTime, time : Int, spt : Array<Int>, bp : Int) {
        today = date
        elapsed_time = time
        expected_count_per_tooth = elapsed_time / (NUMBER_OF_TEETH* UNIT_TIME)
        count_per_tooth = spt
        bad_pressure = bp

        calculate_duration()
        calculate_pressure()
        calculate_sec_per_tooth()
        set_comment()

        if (score < 0)
            score = 0

        // record를 Record와 DataCenter에 저장
        var record = Record(today, elapsed_time, count_per_tooth, section_time, bad_pressure, score, comment)
        DataCenter.records.add(record)
        init()

    }

    private fun calculate_duration() {
        var minusScore : Int
        if (elapsed_time < 150) { // 2분 30초
            minusScore = (150 - elapsed_time)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
        else if (elapsed_time > 210) {   // 3분 30초
            minusScore = (elapsed_time - 210)     // 초 차이
            minusScore = (minusScore / 10 + 1) * 5

            score -= minusScore
        }
    }

    private fun calculate_pressure() {
        score -= bad_pressure * 3
    }

    private fun calculate_sec_per_tooth() {
        for (i in 0..5) {
            sumDiff[i] = 0
        }

        for (i in 11..47) {
            // 위
            if (i in 11..13 || i in 21..23) {
                sumDiff[0] += count_per_tooth[i] - expected_count_per_tooth
            }
            else if (i in 14..17) {
                sumDiff[1] += count_per_tooth[i] - expected_count_per_tooth
            }
            else if (i in 24..27) {
                sumDiff[2] += count_per_tooth[i] - expected_count_per_tooth
            }

            // 아래
            else if (i in 31..33 || i in 41..43) {
                sumDiff[3] += count_per_tooth[i] - expected_count_per_tooth
            }
            else if (i in 34..37) {
                sumDiff[4] += count_per_tooth[i] - expected_count_per_tooth
            }
            else if (i in 44..47) {
                sumDiff[5] += count_per_tooth[i] - expected_count_per_tooth
            }
        }
        Log.i("sumDiff", "${sumDiff[0]} ${sumDiff[1]} ${sumDiff[2]} ${sumDiff[3]} ${sumDiff[4]} ${sumDiff[5]}")
        setCommentOfSection()

    }

    private fun setCommentOfSection() {
        var moreTeeth : String = ""
        var lessTeeth : String = ""

        var more : Boolean = false
        var less : Boolean = false

        for (i in 0..5) {
            if (sumDiff[i] > 6) {       // 수 정해야 함 - 지금은 10초
                if (more)
                    moreTeeth += "와 "
                moreTeeth += sectionName[i]
                section_time[i] = MORE
                more = true
                score -= 5
            }
            else if (sumDiff[i] < -6) {     // 수 정해야함
                if(less)
                    lessTeeth += "와 "
                lessTeeth += sectionName[i]
                section_time[i] = LESS
                less = true
                score -= 5
            }
            else
                section_time[i] = OKAY
        }

        if (more)
            moreTeeth += "를 오래 양치했습니다. "
        if (less)
            lessTeeth += "를 조금 더 집중적으로 양치해 주세요. "

        teethTimeComment = moreTeeth + lessTeeth
    }

    private fun set_comment() {
        if(elapsed_time in 150..210) {
            comment += "양치를 적절한 시간 동안 했습니다. "
        }
        else if (elapsed_time < 150) {
            comment += "양치 시간이 부족하네요! 다음부터는 조금 더 구석구석 닦아 보도록 해봐요. "
        }
        else
            comment += "양치를 너무 오래하셨네요. 너무 오래 양치하면 오히려 잇몸과 치아가 상할 수 있습니다. "

        comment += teethTimeComment

        if (bad_pressure >= 5)
            comment += "양치를 세게 하는 경향이 있습니다. 살살 양치하세요. "
    }


}