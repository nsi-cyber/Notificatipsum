package com.nsicyber.notificatipsum.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nsicyber.notificatipsum.R
import com.nsicyber.notificatipsum.data.NotificationEntity
import com.nsicyber.notificatipsum.ui.NotificationViewModel
import com.nsicyber.notificatipsum.ui.components.NotificationForm
import com.nsicyber.notificatipsum.ui.components.NotificationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NotificationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNotification by remember { mutableStateOf<NotificationEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_notification_scheduler)) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
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
            if (uiState.notifications.isEmpty()) {
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
                    Button(onClick = { showAddDialog = true }) {
                        Text(stringResource(R.string.add_notification))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onEdit = { editingNotification = notification },
                            onDelete = { viewModel.deleteNotification(notification) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.add_notification)) },
            text = {
                NotificationForm(
                    onSubmit = { title, description, dateTime, imageUri ->
                        viewModel.scheduleNotification(title, description, dateTime, imageUri)
                        showAddDialog = false
                    }
                )
            },
            confirmButton = { },
            dismissButton = { }
        )
    }

    editingNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { editingNotification = null },
            title = { Text(stringResource(R.string.edit_notification)) },
            text = {
                NotificationForm(
                    initialTitle = notification.title,
                    initialDescription = notification.description,
                    initialDateTime = notification.dateTime,
                    initialImageUri = notification.imageUri,
                    onSubmit = { title, description, dateTime, imageUri ->
                        viewModel.updateNotification(
                            notification.id,
                            title,
                            description,
                            dateTime,
                            imageUri
                        )
                        editingNotification = null
                    }
                )
            },
            confirmButton = { },
            dismissButton = { }
        )
    }
} 