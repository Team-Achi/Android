package com.example.administrator.achi.expandableList

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.popup_comment.view.*


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

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
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

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var convertView = view
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_child, null, false)
        }

        var content = getChild(groupPosition, childPosition)

        val startTime = convertView!!.findViewById<TextView>(R.id.tvStartTime)
        val duration = convertView.findViewById<TextView>(R.id.tvDuration)
        val score = convertView.findViewById<TextView>(R.id.tvScore)

        startTime.text = content.startTime
        duration.text = content.elapsedTime
        score.text = "${content.score.toString()} 점"



        convertView.setOnClickListener() {

            val layoutInflater : LayoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView : View = layoutInflater.inflate(R.layout.popup_comment, null)

            val popUp : PopupWindow = PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

            popUp.showAtLocation(popupView, Gravity.CENTER, 0, 0)

            popupView.tvComment.text = content.comment

            popupView.btn_close_popup.setOnClickListener() {
                popUp.dismiss()
            }

            // 다른 곳 눌렀을 때 닫히게 해야함
            popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popUp.isOutsideTouchable = true
            popUp.isFocusable = true


//            var intent : Intent = Intent(context, PopupComment::class.java)
//            intent.putExtra("comment", content.comment)
//
//            context.startActivity(intent)

        }

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