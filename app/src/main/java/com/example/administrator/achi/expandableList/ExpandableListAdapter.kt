package com.example.administrator.achi.expandableList

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.administrator.achi.R


class ExpandableListAdapter(var context : Context, var elv: ExpandableListView, var groupList : ArrayList<String>, var childList : ArrayList<ArrayList<ChildContentFormat>>) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): String {
        //To change body of created functions use File | Settings | File Templates.
        return groupList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        //To change body of created functions use File | Settings | File Templates.
        return true
    }

    override fun hasStableIds(): Boolean {
        //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_group, null)
        }

        val groupTitle = convertView!!.findViewById<TextView>(R.id.tvGroupTitle)
        groupTitle?.text = getGroup(groupPosition)

        groupTitle?.setOnClickListener {
            if (elv.isGroupExpanded(groupPosition))
                elv.collapseGroup(groupPosition)
            else
                elv.expandGroup(groupPosition)
        }

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        //To change body of created functions use File | Settings | File Templates.
        return childList[groupPosition]. size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ChildContentFormat {
        //To change body of created functions use File | Settings | File Templates.
        return childList[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_child, null, false)
        }

        var content = getChild(groupPosition, childPosition)

        val startTime = convertView!!.findViewById<TextView>(R.id.tvStartTime)
        val duration = convertView!!.findViewById<TextView>(R.id.tvDuration)
        val score = convertView!!.findViewById<TextView>(R.id.tvScore)

        startTime.text = content.startTime
        duration.text = content.elapsedTime
        score.text = "${content.score.toString()} 점"



        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
         //To change body of created functions use File | Settings | File Templates.
        return groupList.size
    }

}