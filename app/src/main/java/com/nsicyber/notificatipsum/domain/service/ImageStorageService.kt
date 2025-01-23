package com.nsicyber.notificatipsum.domain.service

import android.net.Uri

interface ImageStorageService {
    suspend fun saveImage(uri: Uri): String
    suspend fun deleteImage(path: String)
} 