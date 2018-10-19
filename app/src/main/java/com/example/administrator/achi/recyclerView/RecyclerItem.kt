package com.example.administrator.achi.recyclerView

import com.example.administrator.achi.dataModel.DataCenter
import java.time.format.DateTimeFormatter
import java.util.*

// 하루 record
class RecyclerItem {
    var date: String
        private set
    var startIdx : Int
        private set
    var endIdx : Int
        private set

    private var formatter = DateTimeFormatter.ofPattern("MM/dd EEE", Locale.KOREAN)

    constructor(startIdx : Int, lastIdx : Int) {
        this.startIdx = startIdx
        this.endIdx = lastIdx
        this.date = DataCenter.records[startIdx].date.format(formatter)
    }
}