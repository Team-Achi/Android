package com.example.administrator.achi.fragment

object DataCenter {
    private var records = ArrayList<Record>()


    fun saveData () {
        // 데이터 저장

    }
    fun addRecord(record: Record) {
        records.add(0,record)
    }


}