package com.nsicyber.notificatipsum.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nsicyber.notificatipsum.R
import com.nsicyber.notificatipsum.data.NotificationDatabase
import com.nsicyber.notificatipsum.data.NotificationEntity
import com.nsicyber.notificatipsum.data.NotificationRepository
import com.nsicyber.notificatipsum.manager.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.UUID

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository
    private val notificationScheduler: NotificationScheduler
    private val context: Context = application
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        val database = NotificationDatabase.getDatabase(application)
        repository = NotificationRepository(database.notificationDao())
        notificationScheduler = NotificationScheduler(application)

        // Clean up expired notifications
        viewModelScope.launch {
            repository.deleteExpiredNotifications()
        }

        // Observe notifications
        viewModelScope.launch {
            repository.getAllNotifications()
                .collect { notifications ->
                    _uiState.update { it.copy(notifications = notifications) }
                }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "notification_image_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun scheduleNotification(
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String? = null
    ) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            showError(context.getString(R.string.error_past_time))
            return
        }

        viewModelScope.launch {
            val savedImagePath = imageUri?.let { uri ->
                saveImageToInternalStorage(Uri.parse(uri))
            }

            val notification = NotificationEntity(
                title = title,
                description = description,
                dateTime = dateTime,
                imageUri = savedImagePath
            )
            val id = repository.insertNotification(notification)
            notificationScheduler.scheduleNotification(notification.copy(id = id))
        }
    }

    fun deleteNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            // Delete the image file if it exists
            notification.imageUri?.let { path ->
                File(path).delete()
            }
            repository.deleteNotification(notification)
            notificationScheduler.cancelNotification(notification.id)
        }
    }

    fun updateNotification(
        id: Long,
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String?
    ) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            showError(context.getString(R.string.error_past_time))
            return
        }

        viewModelScope.launch {
            val savedImagePath = imageUri?.let { uri ->
                if (uri.startsWith("content://")) {
                    // New image selected, save it
                    saveImageToInternalStorage(Uri.parse(uri))
                } else {
                    // Existing image path, keep it
                    uri
                }
            }

            val updatedNotification = NotificationEntity(
                id = id,
                title = title,
                description = description,
                dateTime = dateTime,
                imageUri = savedImagePath
            )
            repository.updateNotification(updatedNotification)
            notificationScheduler.scheduleNotification(updatedNotification)
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000) // Show error for 3 seconds
            _uiState.update { it.copy(error = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class NotificationUiState(
    val notifications: List<NotificationEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 