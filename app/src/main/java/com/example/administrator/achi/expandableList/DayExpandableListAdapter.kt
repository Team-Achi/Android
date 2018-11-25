package com.example.administrator.achi.expandableList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.administrator.achi.R
import com.example.administrator.achi.dataModel.Analyzer
import com.example.administrator.achi.dataModel.DataCenter
import java.time.format.DateTimeFormatter

//  record 한 개 : 한 expandableLV에는 하루의 기록이 들어 있다.
class DayExpandableListAdapter (var context : Context, var delv : ExpandableListView, var groupList : ArrayList<Int>) : BaseExpandableListAdapter(){

    override fun getGroupCount(): Int {
        return groupList.size
    }

    override fun getGroupId(groupPosition: Int): Long {

        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): Int {

        return groupList[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {

        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_record, null)
        }

        var indexGroup = getGroup(groupPosition)
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        var record = DataCenter.records[indexGroup]

        val groupTitle = convertView!!.findViewById<LinearLayout>(R.id.layoutELV)
        val startTime = convertView!!.findViewById<TextView>(R.id.tvStartTime)
        val duration = convertView.findViewById<TextView>(R.id.tvDuration)
        val score = convertView.findViewById<TextView>(R.id.tvScore)

        startTime.text = record.date.format(timeFormatter)
        duration.text = Analyzer.timeToString(record.duration.toInt())
        score.text = "${record.score.toString()} 점"

        groupTitle.setOnClickListener {
            if (delv.isGroupExpanded(groupPosition))
                delv.collapseGroup(groupPosition)
            else
                delv.expandGroup(groupPosition)
        }

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {

        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {

        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Int {

        return groupList[groupPosition]
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {

        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_comment, null, false)
        }

        var indexChild = getChild(groupPosition, childPosition)

        val comment = convertView!!.findViewById<TextView>(R.id.tvComment)
        comment.text = DataCenter.records[indexChild].comment

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {

        return true
    }

    override fun hasStableIds(): Boolean {

        return false
    }


}