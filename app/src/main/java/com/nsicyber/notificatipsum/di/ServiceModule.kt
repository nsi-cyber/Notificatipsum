package com.nsicyber.notificatipsum.di

import com.nsicyber.notificatipsum.data.service.ImageStorageServiceImpl
import com.nsicyber.notificatipsum.data.service.NotificationSchedulerServiceImpl
import com.nsicyber.notificatipsum.domain.service.ImageStorageService
import com.nsicyber.notificatipsum.domain.service.NotificationSchedulerService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindNotificationSchedulerService(
        impl: NotificationSchedulerServiceImpl
    ): NotificationSchedulerService

    @Binds
    @Singleton
    abstract fun bindImageStorageService(
        impl: ImageStorageServiceImpl
    ): ImageStorageService
} 