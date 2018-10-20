package com.example.administrator.achi.expandableList

import android.content.Context
import android.view.*
import android.widget.*
import com.example.administrator.achi.R


class ExpandableListAdapter(var context : Context, var groupList : ArrayList<String>,
                            var dayList : ArrayList<ExpandableRecords>) : BaseExpandableListAdapter() {

    private lateinit var elva : DayExpandableListAdapter

    override fun getGroupCount(): Int {

        return groupList.size
    }

    override fun getGroupId(groupPosition: Int): Long {

        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): String {

        return groupList[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View? {

        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_date, null)
        }

        val groupTitle = convertView!!.findViewById<TextView>(R.id.tvDateTitle)
        groupTitle?.text = getGroup(groupPosition)

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {

        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {

        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ArrayList<Int> {

        var dayItem : ExpandableRecords = dayList[groupPosition]
        var indices = ArrayList<Int>()

        for (i in dayItem.startIdx..dayItem.endIdx)
            indices.add(i)

        return indices
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View? {

        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_onedayrecords, null)
        }
        val dayExpandableListView = DayExpandableListView(context)

//        var elvOneDay = convertView!!.findViewById<ExpandableListView>(R.id.elvOneDay)

        elva = DayExpandableListAdapter(this.context, dayExpandableListView, getChild(groupPosition, childPosition))
//        elvOneDay.setAdapter(elva)

        dayExpandableListView.setAdapter(elva)
        dayExpandableListView.setGroupIndicator(null)
        dayExpandableListView.divider = null
        dayExpandableListView.dividerHeight = 10

        return dayExpandableListView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {

        return true
    }

    override fun hasStableIds(): Boolean {

        return false
    }

}