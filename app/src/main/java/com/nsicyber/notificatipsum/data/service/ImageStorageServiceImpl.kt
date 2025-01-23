package com.nsicyber.notificatipsum.data.service

import android.content.Context
import android.net.Uri
import com.nsicyber.notificatipsum.domain.service.ImageStorageService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ImageStorageServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageStorageService {

    override suspend fun saveImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Failed to open input stream")

        val fileName = "notification_image_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    }

    override suspend fun deleteImage(path: String) = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
} 