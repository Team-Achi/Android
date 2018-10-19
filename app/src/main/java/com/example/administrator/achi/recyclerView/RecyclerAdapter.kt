package com.example.administrator.achi.recyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.item_recyclerview.view.*

class RecyclerAdapter(val items : ArrayList<RecyclerItem>, val context: Context?) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dayItem : RecyclerItem = items[position]
        var indices = ArrayList<Int>()

        holder.tvDate.text = dayItem.date

        for (i in dayItem.startIdx..dayItem.endIdx)
            indices.add(i)

        Log.i("RecyclerAdapter", "${indices[0]} ~ ${indices[indices.size - 1]}")

        var elva = DayExpandableListAdapter(this.context!!, holder.elvOneDay, indices)

        holder.elvOneDay.setAdapter(elva)
    }
}


class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvDate = view.tvDate
    val elvOneDay = view.elvOneDay
}