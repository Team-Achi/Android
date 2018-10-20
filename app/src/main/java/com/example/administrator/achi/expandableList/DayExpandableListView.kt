package com.example.administrator.achi.expandableList

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ExpandableListView

class DayExpandableListView : ExpandableListView {

    constructor(context : Context) : super(context)

    override fun onMeasure(widthMeasureSpec : Int, height : Int) {
        var heightMeasureSpec = MeasureSpec.makeMeasureSpec(9999999, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }
}
