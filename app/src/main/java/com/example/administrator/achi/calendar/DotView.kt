package com.example.administrator.achi.calendar

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.activity_intro.view.*
import java.util.*

class DotView : View {
    var agendaList: List<Agenda> = ArrayList()

    constructor(context: Context): super(context) {}

    override fun onDraw(canvas: Canvas) {
        val count = agendaList.size
        agendaList.forEachIndexed { index, agenda ->
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = agenda.color
          /*  var bitmap : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.achi_pink_two)
            canvas.drawBitmap(bitmap, width.toFloat() / (count + 1) * (index + 1),5 * height.toFloat() / 6, null);*/

          canvas.drawCircle(width.toFloat() / (count + 1) * (index + 1),5 * height.toFloat() / 6, (width + height).toFloat() / 32, paint)
        }
    }
}

