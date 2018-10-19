package com.example.administrator.achi.expandableList

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import com.example.administrator.achi.R
import com.example.administrator.achi.R.id.elvOneDay
import com.example.administrator.achi.recyclerView.DayExpandableListAdapter
import com.example.administrator.achi.recyclerView.RecyclerItem
import kotlinx.android.synthetic.main.popup_comment.view.*


class ExpandableListAdapter(var context : Context, var elv: ExpandableListView, var groupList : ArrayList<String>,
                            var dayList : ArrayList<RecyclerItem>) : BaseExpandableListAdapter() {

    private var indices : ArrayList<Int> = ArrayList<Int>()

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

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View? {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_date, null)
        }

        val groupTitle = convertView!!.findViewById<TextView>(R.id.tvDateTitle)
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
        return indices.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ArrayList<Int> {
        //To change body of created functions use File | Settings | File Templates.
        var dayItem : RecyclerItem = dayList[groupPosition]
        indices = ArrayList<Int>()

        for (i in dayItem.startIdx..dayItem.endIdx)
            indices.add(i)

        return indices
    }

    override fun getGroupId(groupPosition: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_onedayrecords, null)
        }
        val elvOneDay = convertView!!.findViewById<ExpandableListView>(R.id.elvOneDay)

        var elva = DayExpandableListAdapter(this.context, elvOneDay, indices)
        elvOneDay.setAdapter(elva)

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