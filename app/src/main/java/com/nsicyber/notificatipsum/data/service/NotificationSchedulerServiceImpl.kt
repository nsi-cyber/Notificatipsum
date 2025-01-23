package com.nsicyber.notificatipsum.data.service

import android.app.NotificationManager
import android.content.Context
import androidx.work.*
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import com.nsicyber.notificatipsum.worker.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationSchedulerServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : NotificationSchedulerService {

    override suspend fun scheduleNotification(notification: Notification) {
        val now = LocalDateTime.now()
        val delay = Duration.between(now, notification.dateTime)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
            .setInputData(createInputData(notification))
            .build()

        workManager.enqueueUniqueWork(
            getWorkName(notification.id),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override suspend fun cancelNotification(notificationId: Long) {
        workManager.cancelUniqueWork(getWorkName(notificationId))
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId.toInt())
    }

    private fun createInputData(notification: Notification): Data {
        return Data.Builder()
            .putLong("id", notification.id)
            .putString("title", notification.title)
            .putString("description", notification.description)
            .putString("imageUri", notification.imageUri)
            .build()
    }

    private fun getWorkName(notificationId: Long) = "notification_$notificationId"
} 