package com.nsicyber.notificatipsum.domain.usecase

import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> {
        return repository.getAllNotifications()
    }
} 