package com.qzwx.diary.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.qzwx.diary.R
import java.text.SimpleDateFormat
import java.util.*

class XieRiJi : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("M月d日 HH:mm", Locale.getDefault())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, 1000) // 每秒钟更新一次
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xie_ri_ji)

        timeTextView = findViewById(R.id.current_time_text)
        startUpdatingTime()
    }

    private fun startUpdatingTime() {
        handler.post(updateTimeRunnable)
    }

    private fun updateTime() {
        val currentTime = timeFormat.format(Date())
        timeTextView.text = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable) // 确保在销毁时停止更新
    }
}

