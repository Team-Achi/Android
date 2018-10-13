package com.example.administrator.achi.fragment

class DataCenter {
    private lateinit var instance : DataCenter
    private var record_day = ArrayList<Record>()

    constructor() {
        // 나중에 파일입출력 여기서 해
    }

    fun getInstance() : DataCenter {
        if (instance == null) {
            instance = DataCenter()
        }
        return instance
    }

    fun saveData () {
        // 데이터 저장

    }



}