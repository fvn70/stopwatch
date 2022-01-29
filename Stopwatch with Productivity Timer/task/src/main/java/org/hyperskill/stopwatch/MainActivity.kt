package org.hyperskill.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val textView: TextView by lazy { findViewById(R.id.textView) }
    val startButton: Button by lazy { findViewById(R.id.startButton) }
    val resetButton: Button by lazy { findViewById(R.id.resetButton) }
    var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler(Looper.getMainLooper())

        val startWatch: Runnable = object : Runnable {
            override fun run() {
                time++
                textView.text = String.format("%02d:%02d", time / 60, time % 60)
                handler.postDelayed(this, 1000)
            }
        }

        startButton.setOnClickListener {
            if (time == 0) {
                thread {
                    handler.postDelayed(startWatch, 1000)
                }
            }
        }

        resetButton.setOnClickListener {
            handler.removeCallbacks(startWatch)
            time = 0
            textView.text = "00:00"
        }
    }

}