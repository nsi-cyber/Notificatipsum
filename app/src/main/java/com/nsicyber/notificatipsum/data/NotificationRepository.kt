package com.nsicyber.notificatipsum.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getAllNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.getAllNotifications(LocalDateTime.now())

    fun getUpcomingNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.getUpcomingNotifications(LocalDateTime.now())

    suspend fun getNotificationById(id: Long): NotificationEntity? =
        notificationDao.getNotificationById(id)

    suspend fun insertNotification(notification: NotificationEntity): Long =
        notificationDao.insertNotification(notification)

    suspend fun updateNotification(notification: NotificationEntity) =
        notificationDao.updateNotification(notification)

    suspend fun deleteNotification(notification: NotificationEntity) =
        notificationDao.deleteNotification(notification)

    suspend fun deleteNotificationById(id: Long) =
        notificationDao.deleteNotificationById(id)

    suspend fun deleteExpiredNotifications() =
        notificationDao.deleteExpiredNotifications(LocalDateTime.now())
} 