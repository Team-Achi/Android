package com.example.administrator.achi.fragment

object DataCenter {
    var records = ArrayList<Record>()


    fun saveData () {
        // 데이터 저장

    }

//    fun addRecord(record: Record) {
//        records.add(0,record)
//        printRecord()
//    }

    // just for test
    fun printRecord() {
        for (i in 0 until records.size)
            records[i].printRecord()
    }

    fun sampleRecords () {

    }


}