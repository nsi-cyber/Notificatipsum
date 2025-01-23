package com.nsicyber.notificatipsum.domain.usecase

import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CleanupExpiredNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = runCatching {
        repository.deleteExpiredNotifications(LocalDateTime.now())
    }
} 