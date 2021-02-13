package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.EXTRA_ID
import com.udacity.utils.EXTRA_SUCCESS
import com.udacity.utils.EXTRA_TITLE
import com.udacity.utils.cancelNotification
import com.udacity.utils.cancelNotifications

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_detail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        if (intent.hasExtra(EXTRA_ID)) {
            val id = intent.getIntExtra(EXTRA_ID, -1)
            getSystemService(NotificationManager::class.java).cancelNotification(id)
        }

        if (intent.hasExtra(EXTRA_SUCCESS)) {
            if (intent.getBooleanExtra(EXTRA_SUCCESS, false)) {
                binding.contentDetail.status.text = getString(R.string.success)
                binding.contentDetail.status.setTextColor(
                    ContextCompat.getColor(this, R.color.colorPrimaryDark)
                )
            } else {
                binding.contentDetail.status.text = getString(R.string.fail)
                binding.contentDetail.status.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_red_dark)
                )
            }
        }

        if (intent.hasExtra(EXTRA_TITLE)) {
            binding.contentDetail.fileName.text = intent.getStringExtra(EXTRA_TITLE)
        }

        binding.contentDetail.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }
}
