package com.nsicyber.notificatipsum.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nsicyber.notificatipsum.R
import com.nsicyber.notificatipsum.domain.model.RepeatInterval
import com.nsicyber.notificatipsum.domain.model.WeekDay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationForm(
    initialTitle: String = "",
    initialDescription: String = "",
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    initialImageUri: String? = null,
    initialRepeatInterval: RepeatInterval = RepeatInterval.NONE,
    initialRepeatDays: Set<WeekDay> = emptySet(),
    initialRepeatUntil: LocalDateTime? = null,
    onSubmit: (String, String, LocalDateTime, String?, RepeatInterval, Set<WeekDay>, LocalDateTime?) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var dateTime by remember { mutableStateOf(initialDateTime) }
    var imageUri by remember { mutableStateOf(initialImageUri) }
    var repeatInterval by remember { mutableStateOf(initialRepeatInterval) }
    var repeatDays by remember { mutableStateOf(initialRepeatDays) }
    var repeatUntil by remember { mutableStateOf(initialRepeatUntil) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRepeatUntilDatePicker by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title and Description fields
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.label_title)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.label_description)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Date and Time Selection
        Column {
            Text(
                text = stringResource(R.string.select_date_time),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.select_date_time))
            }
        }

        // Image Selection and Preview
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.cd_notification_image),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (imageUri != null) 
                            stringResource(R.string.change_image)
                        else 
                            stringResource(R.string.add_image)
                    )
                }
            }
        }

        // Repeat Options
        Text(
            text = stringResource(R.string.repeat_options),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.selectableGroup()
        ) {
            RepeatInterval.values().forEach { interval ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = repeatInterval == interval,
                            onClick = { repeatInterval = interval }
                        )
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = repeatInterval == interval,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (interval) {
                            RepeatInterval.NONE -> stringResource(R.string.repeat_none)
                            RepeatInterval.DAILY -> stringResource(R.string.repeat_daily)
                            RepeatInterval.WEEKLY -> stringResource(R.string.repeat_weekly)
                            RepeatInterval.MONTHLY -> stringResource(R.string.repeat_monthly)
                            RepeatInterval.YEARLY -> stringResource(R.string.repeat_yearly)
                            RepeatInterval.CUSTOM_DAYS -> stringResource(R.string.repeat_custom)
                        }
                    )
                }
            }
        }

        // Custom Days Selection (visible only when CUSTOM_DAYS is selected)
        if (repeatInterval == RepeatInterval.CUSTOM_DAYS) {
            Column {
                Text(
                    text = stringResource(R.string.select_days),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                WeekDay.values().forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = repeatDays.contains(day),
                            onCheckedChange = { checked ->
                                repeatDays = if (checked) {
                                    repeatDays + day
                                } else {
                                    repeatDays - day
                                }
                            }
                        )
                        Text(
                            text = when (day) {
                                WeekDay.MONDAY -> stringResource(R.string.monday)
                                WeekDay.TUESDAY -> stringResource(R.string.tuesday)
                                WeekDay.WEDNESDAY -> stringResource(R.string.wednesday)
                                WeekDay.THURSDAY -> stringResource(R.string.thursday)
                                WeekDay.FRIDAY -> stringResource(R.string.friday)
                                WeekDay.SATURDAY -> stringResource(R.string.saturday)
                                WeekDay.SUNDAY -> stringResource(R.string.sunday)
                            }
                        )
                    }
                }
            }
        }

        // Repeat Until Date (visible for all repeat options except NONE)
        if (repeatInterval != RepeatInterval.NONE) {
            Column {
                Text(
                    text = stringResource(R.string.repeat_until),
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = repeatUntil?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) 
                            ?: stringResource(R.string.no_end_date)
                    )
                    Button(onClick = { showRepeatUntilDatePicker = true }) {
                        Text(stringResource(R.string.select_end_date))
                    }
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                onSubmit(title, description, dateTime, imageUri, repeatInterval, repeatDays, repeatUntil)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && description.isNotBlank()
        ) {
            Text(stringResource(R.string.save))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                dateTime = dateTime.with(selectedDate)
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                dateTime = dateTime.withHour(hour).withMinute(minute)
                showTimePicker = false
            }
        )
    }

    // Repeat Until Date Picker
    if (showRepeatUntilDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showRepeatUntilDatePicker = false },
            onDateSelected = { selectedDate ->
                repeatUntil = selectedDate.atTime(23, 59)
                showRepeatUntilDatePicker = false
            }
        )
    }
} 