package com.nsicyber.notificatipsum.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE dateTime >= :currentTime ORDER BY dateTime DESC")
    fun getAllNotifications(currentTime: LocalDateTime = LocalDateTime.now()): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Long): NotificationEntity?

    @Insert
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("DELETE FROM notifications WHERE dateTime < :currentTime")
    suspend fun deleteExpiredNotifications(currentTime: LocalDateTime = LocalDateTime.now())

    @Query("SELECT * FROM notifications WHERE dateTime > :currentTime AND isActive = 1 ORDER BY dateTime ASC")
    fun getUpcomingNotifications(currentTime: LocalDateTime): Flow<List<NotificationEntity>>
} 