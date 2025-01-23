package com.nsicyber.notificatipsum.domain.usecase

import android.net.Uri
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import com.nsicyber.notificatipsum.domain.service.ImageStorageService
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import java.time.LocalDateTime
import javax.inject.Inject

class ScheduleNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
    private val schedulerService: NotificationSchedulerService,
    private val imageStorageService: ImageStorageService
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String?
    ): Result<Long> = runCatching {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Cannot schedule notifications in the past")
        }

        val savedImagePath = imageUri?.let { uri ->
            imageStorageService.saveImage(Uri.parse(uri))
        }

        val notification = Notification(
            title = title,
            description = description,
            dateTime = dateTime,
            imageUri = savedImagePath
        )

        val id = repository.insertNotification(notification)
        schedulerService.scheduleNotification(notification.copy(id = id))
        id
    }
} 