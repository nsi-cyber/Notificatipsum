package com.nsicyber.notificatipsum.worker

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nsicyber.notificatipsum.NotificationApp.Companion.CHANNEL_ID
import com.nsicyber.notificatipsum.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val notificationManager: NotificationManager
) : CoroutineWorker(context, params) {

    companion object {
        const val NOTIFICATION_ID = "notificationId"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_DESCRIPTION = "description"
        const val NOTIFICATION_IMAGE_URI = "imageUri"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong("id", 0)
        val title = inputData.getString("title") ?: return Result.failure()
        val description = inputData.getString("description") ?: return Result.failure()
        val imageUri = inputData.getString("imageUri")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Load image if available
        imageUri?.let { uri ->
            val file = File(uri)
            if (file.exists()) {
                val bitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath)
                bitmap?.let {
                    notification.setLargeIcon(it)
                    notification.setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(it)

                    )
                }
            }
        }

        notificationManager.notify(id.toInt(), notification.build())
        return Result.success()
    }
} 