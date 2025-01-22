package com.nsicyber.notificatipsum.worker

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nsicyber.notificatipsum.NotificationApp
import com.nsicyber.notificatipsum.R
import com.nsicyber.notificatipsum.data.NotificationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val NOTIFICATION_ID = "notificationId"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_DESCRIPTION = "description"
        const val NOTIFICATION_IMAGE_URI = "imageUri"
    }

    override suspend fun doWork(): Result {
        val notificationId = inputData.getLong(NOTIFICATION_ID, 0)
        val title = inputData.getString(NOTIFICATION_TITLE) ?: return Result.failure()
        val description = inputData.getString(NOTIFICATION_DESCRIPTION) ?: return Result.failure()
        val imageUri = inputData.getString(NOTIFICATION_IMAGE_URI)

        showNotification(notificationId.toInt(), title, description, imageUri)

        // Mark notification as inactive in database
        val database = NotificationDatabase.getDatabase(context)
        val notification = database.notificationDao().getNotificationById(notificationId)
        notification?.let {
            database.notificationDao().updateNotification(it.copy(isActive = false))
        }

        return Result.success()
    }

    private suspend fun showNotification(
        notificationId: Int,
        title: String,
        description: String,
        imageUri: String?
    ) {
        val builder = NotificationCompat.Builder(context, NotificationApp.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Set icon based on imageUri or default icon
        if (imageUri != null) {
            try {
                val bitmap = getBitmapFromUri(imageUri)
                builder.setSmallIcon(R.drawable.ic_notification) // Fallback icon
                builder.setLargeIcon(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                builder.setSmallIcon(R.drawable.ic_notification)
            }
        } else {
            builder.setSmallIcon(R.drawable.ic_notification)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private suspend fun getBitmapFromUri(imageUri: String): Bitmap = withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUri)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        (result as BitmapDrawable).bitmap
    }
} 