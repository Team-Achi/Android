package com.example.administrator.achi.expandableList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import com.example.administrator.achi.R
import kotlinx.android.synthetic.main.popup_comment.*

class PopupComment : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        var layoutParams : WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.5f
        window.attributes = layoutParams

        setContentView(R.layout.popup_comment)

        // 데이터 가져오기
        var intent : Intent = this.intent
        var comment : String = intent.getStringExtra("comment")
        tvComment.text = comment

        btn_close_popup.setOnClickListener() {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event!!.action != MotionEvent.ACTION_OUTSIDE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return
    }
}
