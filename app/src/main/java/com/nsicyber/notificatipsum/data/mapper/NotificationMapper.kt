package com.nsicyber.notificatipsum.data.mapper

import com.nsicyber.notificatipsum.data.local.NotificationEntity
import com.nsicyber.notificatipsum.domain.model.Notification

fun NotificationEntity.toNotification(): Notification {
    return Notification(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        imageUri = imageUri,
        isActive = isActive,
        repeatInterval = repeatInterval,
        repeatDays = repeatDays,
        repeatUntil = repeatUntil,
        parentNotificationId = parentNotificationId
    )
}

fun Notification.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        imageUri = imageUri,
        isActive = isActive,
        repeatInterval = repeatInterval,
        repeatDays = repeatDays,
        repeatUntil = repeatUntil,
        parentNotificationId = parentNotificationId
    )
} 