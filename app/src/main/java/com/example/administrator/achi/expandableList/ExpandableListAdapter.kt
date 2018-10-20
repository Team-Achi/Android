package com.example.administrator.achi.expandableList

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.*
import com.example.administrator.achi.R


class ExpandableListAdapter(var context : Context, var elv: ExpandableListView, var groupList : ArrayList<String>,
                            var dayList : ArrayList<ExpandableRecords>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        //To change body of created functions use File | Settings | File Templates.
        return groupList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return groupPosition.toLong()
    }

    override fun getGroup(groupPosition: Int): String {
        //To change body of created functions use File | Settings | File Templates.
        return groupList[groupPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View? {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_date, null)
        }

        val groupTitle = convertView!!.findViewById<TextView>(R.id.tvDateTitle)
        groupTitle?.text = getGroup(groupPosition)

        Log.i("dExpandableListAdapter", "=== ${groupPosition} ===")

//        groupTitle?.setOnClickListener {
//            if (elv.isGroupExpanded(groupPosition))
//                elv.collapseGroup(groupPosition)
//            else
//                elv.expandGroup(groupPosition)
//        }

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        //To change body of created functions use File | Settings | File Templates.
        return 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return childPosition.toLong()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ArrayList<Int> {
        //To change body of created functions use File | Settings | File Templates.
        var dayItem : ExpandableRecords = dayList[groupPosition]
        var indices = ArrayList<Int>()

        for (i in dayItem.startIdx..dayItem.endIdx)
            indices.add(i)

        return indices
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View? {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_onedayrecords, null)
        }
//        val dayExpandableListView = DayExpandableListView(context)

        var elvOneDay = convertView!!.findViewById<ExpandableListView>(R.id.elvOneDay)

        var elva = DayExpandableListAdapter(this.context, getChild(groupPosition, childPosition))
//        var test = convertView!!.findViewById<TextView>(R.id.tvTest)
//        test.text = "왜 얘는 나오는가....???"
//
        elvOneDay.setAdapter(elva)

//        dayExpandableListView.setAdapter(elva)
//        dayExpandableListView.setGroupIndicator(null)
//        dayExpandableListView.setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener(){
//            fun onGroupExpanded(groupPosition: Int) {
//                super.onGroupExpanded(groupPosition)
//            }
//        })

//        dayExpandableListView.setAdapter(elva)

        return elvOneDay
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        //To change body of created functions use File | Settings | File Templates.
        return true
    }

    override fun hasStableIds(): Boolean {
        //To change body of created functions use File | Settings | File Templates.
        return false
    }

}