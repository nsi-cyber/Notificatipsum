package com.nsicyber.notificatipsum.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val cleanupExpiredNotificationsUseCase: CleanupExpiredNotificationsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        cleanupExpiredNotifications()
        observeNotifications()
    }

    private fun cleanupExpiredNotifications() {
        viewModelScope.launch {
            cleanupExpiredNotificationsUseCase()
                .onFailure { error ->
                    emitEvent(UiEvent.ShowError(error.message ?: "Failed to cleanup notifications"))
                }
        }
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    emitEvent(UiEvent.ShowError(error.message ?: "Failed to load notifications"))
                }
                .collect { notifications ->
                    _uiState.update { 
                        it.copy(
                            notifications = notifications,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.Schedule -> scheduleNotification(
                event.title,
                event.description,
                event.dateTime,
                event.imageUri
            )
            is NotificationEvent.Update -> updateNotification(
                event.id,
                event.title,
                event.description,
                event.dateTime,
                event.imageUri
            )
            is NotificationEvent.Delete -> deleteNotification(event.notification)
            is NotificationEvent.DismissDialog -> dismissDialog()
            is NotificationEvent.ShowAddDialog -> showAddDialog()
            is NotificationEvent.ShowEditDialog -> showEditDialog(event.notification)
        }
    }

    private fun scheduleNotification(
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            scheduleNotificationUseCase(title, description, dateTime, imageUri)
                .onSuccess { 
                    emitEvent(UiEvent.ShowSuccess("Notification scheduled successfully"))
                    dismissDialog()
                }
                .onFailure { error ->
                    emitEvent(UiEvent.ShowError(error.message ?: "Failed to schedule notification"))
                }
        }
    }

    private fun updateNotification(
        id: Long,
        title: String,
        description: String,
        dateTime: LocalDateTime,
        imageUri: String?
    ) {
        viewModelScope.launch {
            updateNotificationUseCase(id, title, description, dateTime, imageUri)
                .onSuccess { 
                    emitEvent(UiEvent.ShowSuccess("Notification updated successfully"))
                    dismissDialog()
                }
                .onFailure { error ->
                    emitEvent(UiEvent.ShowError(error.message ?: "Failed to update notification"))
                }
        }
    }

    private fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            deleteNotificationUseCase(notification)
                .onSuccess { 
                    emitEvent(UiEvent.ShowSuccess("Notification deleted successfully"))
                }
                .onFailure { error ->
                    emitEvent(UiEvent.ShowError(error.message ?: "Failed to delete notification"))
                }
        }
    }

    private fun dismissDialog() {
        _uiState.update { it.copy(
            showAddDialog = false,
            editingNotification = null
        )}
    }

    private fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    private fun showEditDialog(notification: Notification) {
        _uiState.update { it.copy(editingNotification = notification) }
    }

    private fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val editingNotification: Notification? = null
)

sealed class NotificationEvent {
    data class Schedule(
        val title: String,
        val description: String,
        val dateTime: LocalDateTime,
        val imageUri: String?
    ) : NotificationEvent()

    data class Update(
        val id: Long,
        val title: String,
        val description: String,
        val dateTime: LocalDateTime,
        val imageUri: String?
    ) : NotificationEvent()

    data class Delete(val notification: Notification) : NotificationEvent()
    data class ShowEditDialog(val notification: Notification) : NotificationEvent()
    object ShowAddDialog : NotificationEvent()
    object DismissDialog : NotificationEvent()
}

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    data class ShowSuccess(val message: String) : UiEvent()
} 