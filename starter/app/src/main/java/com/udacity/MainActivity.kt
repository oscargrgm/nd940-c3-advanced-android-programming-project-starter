package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.cancelNotifications
import com.udacity.utils.sendNotification
import com.udacity.utils.toast

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private var downloadID: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val success =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = (success == DownloadManager.STATUS_SUCCESSFUL)
                    val downloadTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

                    sendNotification(isSuccess, downloadTitle)
                }
                binding.contentMain.customButton.downloadCompleted()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        with(binding.contentMain) {
            customButton.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createChannel(
                        getString(R.string.download_notification_channel_id),
                        getString(R.string.notification_channel)
                    )
                }

                if (radioGroup.checkedRadioButtonId == -1) {
                    toast(getString(R.string.error_message_select_project))
                } else {
                    when (radioGroup.checkedRadioButtonId) {
                        radioGlide.id -> download(GitHubRepository.GLIDE)
                        radioLoadApp.id -> download(GitHubRepository.UDACITY)
                        radioRetrofit.id -> download(GitHubRepository.RETROFIT)
                    }
                }
            }
        }
    }

    private fun download(repository: GitHubRepository) {
        val request = DownloadManager.Request(Uri.parse(repository.url))
            .setTitle(repository.title)
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // Enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        channelId: String = CHANNEL_ID,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_HIGH
    ) {
        val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(false)
            enableLights(true)
            lightColor = Color.GREEN
        }

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(notificationChannel)
    }

    private fun sendNotification(isSuccessful: Boolean, downloadingTitle: String) {
        (ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager).run {
            cancelNotifications()
            sendNotification(
                context = this@MainActivity,
                destination = DetailActivity::class.java,
                title = downloadingTitle,
                isSuccessful = isSuccessful
            )
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }
}
