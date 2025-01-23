package com.nsicyber.notificatipsum.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nsicyber.notificatipsum.domain.model.RepeatInterval
import com.nsicyber.notificatipsum.domain.model.WeekDay
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val imageUri: String?,
    val isActive: Boolean = true,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val repeatDays: Set<WeekDay> = emptySet(),
    val repeatUntil: LocalDateTime? = null,
    val parentNotificationId: Long? = null
) 