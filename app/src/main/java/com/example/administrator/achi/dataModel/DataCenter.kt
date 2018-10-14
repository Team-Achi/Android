package com.example.administrator.achi.dataModel

object DataCenter {
    var records = ArrayList<Record>()

    // for sample data
    var i = 0
    var j = 0


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