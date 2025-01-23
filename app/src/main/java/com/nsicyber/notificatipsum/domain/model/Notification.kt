package com.nsicyber.notificatipsum.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: Long = 0,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val imageUri: String?,
    val isActive: Boolean = true,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val repeatDays: Set<WeekDay> = emptySet(),    // Haftanın hangi günleri tekrarlanacak
    val repeatUntil: LocalDateTime? = null,        // Ne zamana kadar tekrarlanacak
    val parentNotificationId: Long? = null         // Tekrarlanan bildirimin ana bildirimi
) 