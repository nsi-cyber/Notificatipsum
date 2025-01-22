package com.nsicyber.notificatipsum.manager

import android.content.Context
import androidx.work.*
import com.nsicyber.notificatipsum.data.NotificationEntity
import com.nsicyber.notificatipsum.worker.NotificationWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleNotification(notification: NotificationEntity) {
        val now = LocalDateTime.now()
        val delay = Duration.between(now, notification.dateTime)
        
        if (delay.isNegative) {
            return
        }

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
            .setInputData(createInputData(notification))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                getWorkName(notification.id),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun cancelNotification(notificationId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(getWorkName(notificationId))
    }

    private fun createInputData(notification: NotificationEntity): Data {
        return Data.Builder()
            .putLong(NotificationWorker.NOTIFICATION_ID, notification.id)
            .putString(NotificationWorker.NOTIFICATION_TITLE, notification.title)
            .putString(NotificationWorker.NOTIFICATION_DESCRIPTION, notification.description)
            .putString(NotificationWorker.NOTIFICATION_IMAGE_URI, notification.imageUri)
            .build()
    }

    private fun getWorkName(notificationId: Long) = "notification_$notificationId"
} 