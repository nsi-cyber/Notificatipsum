package com.nsicyber.notificatipsum.domain.usecase

import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import com.nsicyber.notificatipsum.domain.service.ImageStorageService
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
    private val schedulerService: NotificationSchedulerService,
    private val imageStorageService: ImageStorageService
) {
    suspend operator fun invoke(notification: Notification): Result<Unit> = runCatching {
        // Delete the image if it exists
        notification.imageUri?.let { path ->
            imageStorageService.deleteImage(path)
        }
        
        repository.deleteNotification(notification)
        schedulerService.cancelNotification(notification.id)
    }
} 