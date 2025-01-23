package com.nsicyber.notificatipsum.domain.model

enum class RepeatInterval {
    NONE,           // Tekrar yok
    DAILY,          // Her gün
    WEEKLY,         // Her hafta
    MONTHLY,        // Her ay
    YEARLY,         // Her yıl
    CUSTOM_DAYS     // Özel günler (Pazartesi, Salı, vb.)
} 