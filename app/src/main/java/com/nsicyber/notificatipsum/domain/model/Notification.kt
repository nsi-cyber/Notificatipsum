package com.nsicyber.notificatipsum.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: Long = 0,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val imageUri: String?,
    val isActive: Boolean = true
) 