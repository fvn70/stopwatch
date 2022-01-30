package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val textView: TextView by lazy { findViewById(R.id.textView) }
    val startButton: Button by lazy { findViewById(R.id.startButton) }
    val resetButton: Button by lazy { findViewById(R.id.resetButton) }
    val settingsButton: Button by lazy { findViewById(R.id.settingsButton) }
    val progressBar: ProgressBar by lazy { findViewById(R.id.progressBar) }
    var time = 0
    var upperLimit = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler(Looper.getMainLooper())

        val startWatch: Runnable = object : Runnable {
            override fun run() {
                if (time >= upperLimit) {
                    textView.setTextColor(ColorStateList.valueOf(Color.RED))
                }
                time++
                if (time % 2 == 0) {
                    progressBar.indeterminateTintList = ColorStateList.valueOf(Color.CYAN)
                } else {
                    progressBar.indeterminateTintList = ColorStateList.valueOf(Color.GREEN)
                }
                textView.text = String.format("%02d:%02d", time / 60, time % 60)
                handler.postDelayed(this, 1000)
            }
        }

        startButton.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE
            settingsButton.isEnabled = false
            if (time == 0) {
                thread {
                    handler.postDelayed(startWatch, 1000)
                }
            }
        }

        resetButton.setOnClickListener {
            progressBar.visibility = ProgressBar.INVISIBLE
            settingsButton.isEnabled = true
            textView.setTextColor(ColorStateList.valueOf(Color.BLACK))
            handler.removeCallbacks(startWatch)
            time = 0
            textView.text = "00:00"
        }

        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.dialog_main, null, false)
            AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                    upperLimit = editText.text.toString().toInt()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

}