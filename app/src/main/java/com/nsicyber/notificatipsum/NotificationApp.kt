package com.nsicyber.notificatipsum

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotificationApp(override val workManagerConfiguration: Configuration= Configuration.Builder()
    .setMinimumLoggingLevel(android.util.Log.INFO)
    .build()) : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for scheduled notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "scheduled_notifications"
        const val CHANNEL_NAME = "Scheduled Notifications"
    }
} 