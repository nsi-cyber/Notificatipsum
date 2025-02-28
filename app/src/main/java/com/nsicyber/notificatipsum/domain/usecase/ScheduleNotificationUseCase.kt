package com.nsicyber.notificatipsum.domain.usecase

import android.net.Uri
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.model.RepeatInterval
import com.nsicyber.notificatipsum.domain.model.WeekDay
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
        imageUri: String? = null,
        repeatInterval: RepeatInterval = RepeatInterval.NONE,
        repeatDays: Set<WeekDay> = emptySet(),
        repeatUntil: LocalDateTime? = null
    ): Result<Long> = runCatching {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Cannot schedule notifications in the past")
        }

        if (repeatInterval == RepeatInterval.CUSTOM_DAYS && repeatDays.isEmpty()) {
            throw IllegalArgumentException("Must specify days for custom repeat interval")
        }

        if (repeatInterval != RepeatInterval.NONE && repeatUntil != null && repeatUntil.isBefore(dateTime)) {
            throw IllegalArgumentException("Repeat until date must be after initial date")
        }

        val savedImagePath = imageUri?.let { uri ->
            imageStorageService.saveImage(Uri.parse(uri))
        }

        val notification = Notification(
            title = title,
            description = description,
            dateTime = dateTime,
            imageUri = savedImagePath,
            repeatInterval = repeatInterval,
            repeatDays = repeatDays,
            repeatUntil = repeatUntil
        )

        val id = repository.insertNotification(notification)
        val savedNotification = notification.copy(id = id)
        
        // Schedule the initial notification
        schedulerService.scheduleNotification(savedNotification)

        // If it's a repeating notification, schedule the repeats
        if (repeatInterval != RepeatInterval.NONE) {
            schedulerService.scheduleRepeatingNotification(savedNotification)
        }

        id
    }
} 