package com.nsicyber.notificatipsum.domain.usecase

import android.net.Uri
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import com.nsicyber.notificatipsum.domain.service.ImageStorageService
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
    private val schedulerService: NotificationSchedulerService,
    private val imageStorageService: ImageStorageService
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String?
    ): Result<Unit> = runCatching {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Cannot schedule notifications in the past")
        }

        val savedImagePath = imageUri?.let { uri ->
            if (uri.startsWith("content://")) {
                imageStorageService.saveImage(Uri.parse(uri))
            } else {
                uri
            }
        }

        val notification = Notification(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime,
            imageUri = savedImagePath
        )

        repository.updateNotification(notification)
        schedulerService.scheduleNotification(notification)
    }
} 