package com.nsicyber.notificatipsum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nsicyber.notificatipsum.R
import com.nsicyber.notificatipsum.presentation.NotificationEvent
import com.nsicyber.notificatipsum.presentation.NotificationViewModel
import com.nsicyber.notificatipsum.ui.components.NotificationForm
import com.nsicyber.notificatipsum.ui.components.NotificationItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect UI events
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is com.nsicyber.notificatipsum.presentation.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is com.nsicyber.notificatipsum.presentation.UiEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_notification_scheduler)) },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(NotificationEvent.ShowAddDialog) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_notification)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.notifications.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_notifications),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.onEvent(NotificationEvent.ShowAddDialog) }) {
                        Text(stringResource(R.string.add_notification))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.notifications.sortedByDescending { it.dateTime },
                        key = { it.id }
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onEdit = { viewModel.onEvent(NotificationEvent.ShowEditDialog(notification)) },
                            onDelete = { viewModel.onEvent(NotificationEvent.Delete(notification)) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(NotificationEvent.DismissDialog) },
            title = { Text(stringResource(R.string.add_notification)) },
            text = {
                NotificationForm(
                    onSubmit = { title, description, dateTime, imageUri, repeatInterval, repeatDays, repeatUntil ->
                        viewModel.onEvent(
                            NotificationEvent.Schedule(
                                title = title,
                                description = description,
                                dateTime = dateTime,
                                imageUri = imageUri,
                                repeatInterval = repeatInterval,
                                repeatDays = repeatDays,
                                repeatUntil = repeatUntil
                            )
                        )
                    }
                )
            },
            confirmButton = { },
            dismissButton = { }
        )
    }

    uiState.editingNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(NotificationEvent.DismissDialog) },
            title = { Text(stringResource(R.string.edit_notification)) },
            text = {
                NotificationForm(
                    initialTitle = notification.title,
                    initialDescription = notification.description,
                    initialDateTime = notification.dateTime,
                    initialImageUri = notification.imageUri,
                    initialRepeatInterval = notification.repeatInterval,
                    initialRepeatDays = notification.repeatDays,
                    initialRepeatUntil = notification.repeatUntil,
                    onSubmit = { title, description, dateTime, imageUri, repeatInterval, repeatDays, repeatUntil ->
                        viewModel.onEvent(
                            NotificationEvent.Update(
                                id = notification.id,
                                title = title,
                                description = description,
                                dateTime = dateTime,
                                imageUri = imageUri,
                                repeatInterval = repeatInterval,
                                repeatDays = repeatDays,
                                repeatUntil = repeatUntil
                            )
                        )
                    }
                )
            },
            confirmButton = { },
            dismissButton = { }
        )
    }
} 