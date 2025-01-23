package com.nsicyber.notificatipsum.domain.repository

import com.nsicyber.notificatipsum.domain.model.Notification
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    fun getUpcomingNotifications(): Flow<List<Notification>>
    suspend fun getNotificationById(id: Long): Notification?
    suspend fun insertNotification(notification: Notification): Long
    suspend fun updateNotification(notification: Notification)
    suspend fun deleteNotification(notification: Notification)
    suspend fun deleteNotificationById(id: Long)
    suspend fun deleteExpiredNotifications(currentTime: LocalDateTime = LocalDateTime.now())
} 