package com.rishabh.duressstepss.core.di

import android.content.Context
import androidx.room.Room
import com.rishabh.duressstepss.core.data.local.AppDatabase
import com.rishabh.duressstepss.core.data.local.StepCountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "duress_steps_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStepCountDao(appDatabase: AppDatabase): StepCountDao {
        return appDatabase.stepCountDao()
    }
}
