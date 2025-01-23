package com.nsicyber.notificatipsum.di

import android.content.Context
import android.app.NotificationManager
import androidx.room.Room
import androidx.work.WorkManager
import com.nsicyber.notificatipsum.data.local.NotificationDao
import com.nsicyber.notificatipsum.data.local.NotificationDatabase
import com.nsicyber.notificatipsum.data.repository.NotificationRepositoryImpl
import com.nsicyber.notificatipsum.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationDatabase(
        @ApplicationContext context: Context
    ): NotificationDatabase {
        return Room.databaseBuilder(
            context,
            NotificationDatabase::class.java,
            "notifications.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: NotificationDatabase): NotificationDao = database.notificationDao()

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository = notificationRepositoryImpl

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
} 