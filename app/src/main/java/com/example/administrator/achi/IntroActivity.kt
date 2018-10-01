package com.example.administrator.achi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class IntroActivity : AppCompatActivity(){
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        var logoImage = findViewById<ImageView>(R.id.logoimage)
        logoImage.setImageResource(R.drawable.achi_pink)
        val handler = Handler()
        handler.postDelayed(Runnable {
            //startActivity(Intent(this, LoginActivity::class.java))
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}