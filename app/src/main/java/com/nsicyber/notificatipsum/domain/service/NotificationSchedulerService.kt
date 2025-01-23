package com.nsicyber.notificatipsum.domain.service

import com.nsicyber.notificatipsum.domain.model.Notification

interface NotificationSchedulerService {
    suspend fun scheduleNotification(notification: Notification)
    suspend fun scheduleRepeatingNotification(notification: Notification)
    suspend fun cancelNotification(notificationId: Long)
    suspend fun cancelRepeatingNotification(notificationId: Long)
} 