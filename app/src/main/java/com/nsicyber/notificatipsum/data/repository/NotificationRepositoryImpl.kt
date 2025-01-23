package com.nsicyber.notificatipsum.data.repository

import com.nsicyber.notificatipsum.data.local.NotificationDao
import com.nsicyber.notificatipsum.data.mapper.toEntity
import com.nsicyber.notificatipsum.data.mapper.toNotification
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<Notification>> {
        return dao.getAllNotifications().map { entities ->
            entities.map { it.toNotification() }
        }
    }

    override fun getUpcomingNotifications(): Flow<List<Notification>> {
        return dao.getUpcomingNotifications(LocalDateTime.now()).map { entities ->
            entities.map { it.toNotification() }
        }
    }

    override suspend fun getNotificationById(id: Long): Notification? {
        return dao.getNotificationById(id)?.toNotification()
    }

    override suspend fun insertNotification(notification: Notification): Long {
        return dao.insertNotification(notification.toEntity())
    }

    override suspend fun updateNotification(notification: Notification) {
        dao.updateNotification(notification.toEntity())
    }

    override suspend fun deleteNotification(notification: Notification) {
        dao.deleteNotification(notification.toEntity())
    }

    override suspend fun deleteNotificationById(id: Long) {
        dao.deleteNotificationById(id)
    }

    override suspend fun deleteExpiredNotifications(currentTime: LocalDateTime) {
        dao.deleteExpiredNotifications(currentTime)
    }
} 