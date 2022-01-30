package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

const val CHANNEL_ID = "org.hyperskill"
const val NOTIF_ID = 393939

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
                if (time == upperLimit) {
                    textView.setTextColor(Color.RED)
                    addNotification()
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
            textView.setTextColor(Color.BLACK)
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

    fun addNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification"
            val descriptionText = "Your notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIF_ID, notificationBuilder.build())
    }

}