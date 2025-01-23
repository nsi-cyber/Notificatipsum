package com.nsicyber.notificatipsum.data.service

import android.app.NotificationManager
import android.content.Context
import androidx.work.*
import com.nsicyber.notificatipsum.domain.model.Notification
import com.nsicyber.notificatipsum.domain.model.RepeatInterval
import com.nsicyber.notificatipsum.domain.model.WeekDay
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import com.nsicyber.notificatipsum.worker.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationSchedulerServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : NotificationSchedulerService {

    override suspend fun scheduleNotification(notification: Notification) {
         withContext(Dispatchers.IO) {
            val now = LocalDateTime.now()
            val delay = Duration.between(now, notification.dateTime)

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
                .setInputData(createInputData(notification))
                .build()

            workManager.enqueueUniqueWork(
                getWorkName(notification.id),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override suspend fun scheduleRepeatingNotification(notification: Notification) = withContext(Dispatchers.IO) {
        when (notification.repeatInterval) {
            RepeatInterval.DAILY -> scheduleDailyNotification(notification)
            RepeatInterval.WEEKLY -> scheduleWeeklyNotification(notification)
            RepeatInterval.MONTHLY -> scheduleMonthlyNotification(notification)
            RepeatInterval.YEARLY -> scheduleYearlyNotification(notification)
            RepeatInterval.CUSTOM_DAYS -> scheduleCustomDaysNotification(notification)
            RepeatInterval.NONE -> return@withContext
        }
    }

    private fun scheduleDailyNotification(notification: Notification) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInputData(createInputData(notification))
            .build()

        enqueueRepeatingWork(notification.id, workRequest)
    }

    private fun scheduleWeeklyNotification(notification: Notification) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(7, TimeUnit.DAYS)
            .setInputData(createInputData(notification))
            .build()

        enqueueRepeatingWork(notification.id, workRequest)
    }

    private fun scheduleMonthlyNotification(notification: Notification) {
        // Sadece bir sonraki ayın bildirimini planla
        val nextDateTime = notification.dateTime.plusMonths(1)
        if (isWithinRepeatBounds(nextDateTime, notification)) {
            val delay = Duration.between(LocalDateTime.now(), nextDateTime)
            
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
                .setInputData(createInputData(notification))
                .build()

            workManager.enqueueUniqueWork(
                getWorkName(notification.id, nextDateTime),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun scheduleYearlyNotification(notification: Notification) {
        // Sadece bir sonraki yılın bildirimini planla
        val nextDateTime = notification.dateTime.plusYears(1)
        if (isWithinRepeatBounds(nextDateTime, notification)) {
            val delay = Duration.between(LocalDateTime.now(), nextDateTime)
            
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
                .setInputData(createInputData(notification))
                .build()

            workManager.enqueueUniqueWork(
                getWorkName(notification.id, nextDateTime),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun scheduleCustomDaysNotification(notification: Notification) {
        val now = LocalDateTime.now()
        
        // Sadece bir sonraki tekrarı planla
        notification.repeatDays.forEach { weekDay ->
            val nextDateTime = notification.dateTime.with(
                TemporalAdjusters.next(convertWeekDayToDayOfWeek(weekDay))
            )

            if (isWithinRepeatBounds(nextDateTime, notification)) {
                val delay = Duration.between(now, nextDateTime)
                
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
                    .setInputData(createInputData(notification))
                    .build()

                workManager.enqueueUniqueWork(
                    getWorkName(notification.id, nextDateTime),
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
        }
    }

    private fun isWithinRepeatBounds(dateTime: LocalDateTime, notification: Notification): Boolean {
        return notification.repeatUntil?.let { until ->
            dateTime.isBefore(until) || dateTime.isEqual(until)
        } ?: true
    }

    private fun convertWeekDayToDayOfWeek(weekDay: WeekDay): DayOfWeek {
        return when (weekDay) {
            WeekDay.MONDAY -> DayOfWeek.MONDAY
            WeekDay.TUESDAY -> DayOfWeek.TUESDAY
            WeekDay.WEDNESDAY -> DayOfWeek.WEDNESDAY
            WeekDay.THURSDAY -> DayOfWeek.THURSDAY
            WeekDay.FRIDAY -> DayOfWeek.FRIDAY
            WeekDay.SATURDAY -> DayOfWeek.SATURDAY
            WeekDay.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

    private fun enqueueRepeatingWork(notificationId: Long, workRequest: WorkRequest) {
        workManager.enqueueUniqueWork(
            getRepeatingWorkName(notificationId),
            ExistingWorkPolicy.REPLACE,
            workRequest as OneTimeWorkRequest
        )
    }

    override suspend fun cancelNotification(notificationId: Long) = withContext(Dispatchers.IO) {
        workManager.cancelUniqueWork(getWorkName(notificationId))
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId.toInt())
    }

    override suspend fun cancelRepeatingNotification(notificationId: Long) {
        withContext(Dispatchers.IO) {
            workManager.cancelUniqueWork(getRepeatingWorkName(notificationId))
        }
    }

    private fun createInputData(notification: Notification): Data {
        return Data.Builder()
            .putLong("id", notification.id)
            .putString("title", notification.title)
            .putString("description", notification.description)
            .putString("imageUri", notification.imageUri)
            .build()
    }

    private fun getWorkName(notificationId: Long) = "notification_$notificationId"
    
    private fun getWorkName(notificationId: Long, dateTime: LocalDateTime) = 
        "notification_${notificationId}_${dateTime.toString()}"
    
    private fun getRepeatingWorkName(notificationId: Long) = "repeating_notification_$notificationId"
} 