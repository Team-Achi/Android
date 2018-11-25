package com.example.administrator.achi.dataModel

import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*



object DataCenter {
    var records = ArrayList<Record>()
    var facts = ArrayList<String>()

    fun readFile() {
//        var fr: FileReader? = null
//        var br: BufferedReader? = null
//
//        println("Read File")
//        try {
//            fr = FileReader("records.txt")
//
//            br = BufferedReader(fr)
//
//            var s = String()
//
//            while (true) {
//                s = br!!.readLine()
//                if (s == null)
//                    break
//
//                s = s.replace(" ".toRegex(), "")
//                s = s.replace("\t".toRegex(), "")
//                s = s.replace("\\p{Z}".toRegex(), "")
//
//            }
//
//        } catch (e: FileNotFoundException) {
//            println("Failed to read file.")
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            if (br != null)
//                try {
//                    br!!.close()
//                } catch (e: IOException) {
//                }
//
//            if (fr != null)
//                try {
//                    fr!!.close()
//                } catch (e: IOException) {
//                }
//
//        }

        var file : File = File("record.txt")
        var input : Scanner = Scanner(file)

        while (input.hasNext()) {
            // input의 한 줄 line에 저장 후 공백 제거
            var line = input.nextLine()
            line = line.replace(" ".toRegex(), "")
            line = line.replace("\t".toRegex(), "")
            line = line.replace("\\p{Z}".toRegex(), "")

            val array = line.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            if (array.size == 8) {
                var formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                var date : LocalDateTime = LocalDateTime.parse(array[0], formatter)

                var cnt_per_tooth : Array<Int> = Array<Int>(50, {0})
                var section_time : Array<Int> =  Array<Int>(6,{0})


                var record = Record(date, array[1].toDouble(), cnt_per_tooth, section_time, array[4].toInt(), array[5].toInt(), array[6].toInt(), array[7])
                DataCenter.records.add(record)
            }
        }
    }

    fun writeFile() {
        // 데이터 저장

    }

    // Facts
    fun loadFacts() {
        facts.add("아치의 꿀팁 1: 칫솔에 물을 묻히지 마세요! 치약에 물이 묻게 되면 " +
                "세마제의 농도가 떨어지기 때문에 양치질 효과가 줄어들게 된답니다.")
        facts.add("아치의 꿀팁 2: 탄산음료, 커피 등을 마신 후 30분 후에 양치하기! " +
                "음료에 포함된 산성물질이 치아 표면의 얇은 막을 부식시키기 때문에 " +
                "약간의 시간이 지난 후에 양치하는 것이 좋습니다.")
        facts.add("아치의 꿀팁 3: 어금니, 바깥쪽면, 안쪽면, 씹는면 순으로 닦기!" +
                " 그리고 옆으로 닦아 내리는 것보다 칫솔을 회전시키면서 쓸어내리는 " +
                "느낌으로 양치질하는 것이 좋습니다.")
        facts.add("아치의 꿀팁 4: 잇몸에서 치아 방향으로 쓸어내듯이 닦아줘야 합니다. " +
                "방향을 거꾸로 하면 잇몸에 무리를 주기 대문에 잇몸 사이에 상처가 " +
                "나거나 벌어질 수 있습니다.")
        facts.add("아치의 꿀팁 5: 이와 잇몸뿐 아니라 혓바닥도 깨끗하게 관리해야 합니다. 혓바닥에 낀 설태는 구취의 원인이 되기도 하므로 칫솔을 이용해 혓바닥도 깨끗하게 관리해 주시는 것이 좋습니다.")
        facts.add("아치의 꿀팁 6: 가끔씩 평소의 습관을 깨고 이 닦는 순서 바꿔 주세요! 계속해서 같은 순서로 칫솔질을 한다면 항상 마지막에 닦는 부분은 대충 마무리해 청결하게 관리되지 못할 위험이 큽니다.")
        facts.add("아치의 꿀팁 7: 치약을 짤 때는 칫솔모 길이의 1/2에서 1/3만큼 짜면 됩니다. 칫솔모 위에는 도톰하게 얹지 말고 안으로 스며들도록 눌러 짜는 것이 좋습니다.")
        facts.add("아치의 꿀팁 8: 칫솔을 3개월 주기적으로 교체하기! 오랜 기간 사용 시 더 많은 세균이 번식할 우려가 있으며, 칫솔모가 변형되어 치아가 잘 닦이지 않을 확률이 높습니다.")
        facts.add("아치의 꿀팁 9: 양치 후 10번 이상 헹구기! 치약이 입에 남았을 경우 치아에 착생을 일으킬 수 있습니다.")
        facts.add("아치의 꿀팁 10: 양치 후 찝찝함이 남아있다면 이쑤시개 보다는 치실을 이용하세요. 양치 후 약 1분간 남아있는 이물질을 제거해주면 치아의 수명이 약 5년 정도 늘어날 수 있습니다.")
        facts.add("아치의 꿀팁 11: 의무적으로라도 하루에 8잔 이상씩 미지근한 물을 꾸준히 마셔준다면 입냄새도 예방하고 세균 번식을 막아 구강 관리에도 도움을 줍니다.")
        facts.add("아치의 꿀팁 12: 장시간 양치질 하지 않기! 오래 양치질을 하면 잇몸에 상처가 생기거나 치아의 표면에도 좋지 않습니다.")
        facts.add("아치의 꿀팁 13: 하루에 2번 이상 2분 이상 올바른 방법으로 양치질 하는 것이 좋습니다.")
        facts.add("아치의 꿀팁 14: 좌우로 닦는 것 보다는 45도 정도 뉘어서 상하로 닦아 주시는 것이 좋습니다.")
        facts.add("아치의 꿀팁 15: 칫솔을 선택하실 때 너무 단단하거나 너무 부드럽지 않은 적당한 칫솔을 사용하고 어금니 두개 정도 덮을 크기의 칫솔모를 사용하는 게 좋습니다.")
        facts.add("아치의 꿀팁 16: 너무 세게 칫솔질 하지 마세요. 칫솔질을 너무 세게 할 경우 치아 에나멜을 침식시킬 수 있습니다.")
        facts.add("아치의 꿀팁 17: 야식을 먹은 뒤에는 즉시 양치질! 밤에는 낮보다 침 분비량이 줄어 입안이 말라 세균이 번식하기 쉬워집니다.")
        facts.add("아치의 꿀팁 18: 치킨, 피장 등에 많이 함류된 기름은 치아 표면이나 칫솔이 잘 닿지 않는 곳에 들러붙어 충치를 유발하기 쉬운 성분이기도 하니 유의해서 양치하세요.")
        facts.add("아치의 꿀팁 20: 커피나 홍차를 마신 뒤에는 즉시 양치하기! 커피나 홍차에 들어있는 검정 색소인 '타닌'성분은 치아를 변색시킬 수 있습니다.")
    }

    // just for test
    fun printRecords() {
        for (i in 0 until records.size)
            records[i].printRecord()
    }

    // 오래된 게 더 뒤에 잇도록 - 오래된 것보다 현재 것이 더 규칙적
    fun sampleRecords () {
        // for sample data
        var day = 0
        var num = 2
        var timeFactor = -3

        val random: Random = Random()
        var date : LocalDateTime
        var duration = 0.0
        var high_pressure = 0
        var low_pressure = 0
        var cnt_per_tooth = Array<Int>(50, {0})

        var avgTime = 180 / 28
        
        for (it in 0..39) {

            if (day <= 6 && num <= 0) {       // 최근 일주일
                day++
                num = 2
                timeFactor = 0
            }
            else if (day > 6 && num <= 0) {   // 지난 일주일
                day++
                num = random.nextInt(3)
                timeFactor = 0
            }
            else {
                num--
                timeFactor += 3
            }

            date = LocalDateTime.now().minusDays(day.toLong()).minusHours(timeFactor.toLong())
            high_pressure = random.nextInt(6)
            low_pressure = random.nextInt(6)
            duration = 0.0

            for (i in 11..47) {
                if (i != 18 ||i != 19 ||i != 20 ||i != 28 ||i != 29 ||i != 30 ||i != 38 || i != 39 ||i != 40) {
                    cnt_per_tooth[i] = (avgTime / UNIT_TIME + (random.nextInt(60) - 30)).toInt()
                    duration += cnt_per_tooth[i] * UNIT_TIME
                }
            }

            Analyzer.analyzeSample(date, duration, cnt_per_tooth, high_pressure, low_pressure)
        }
//        printRecords()

    }
}