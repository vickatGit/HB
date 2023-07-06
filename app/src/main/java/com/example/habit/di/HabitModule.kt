package com.example.habit.di

import android.app.Application
import androidx.room.Room
import com.example.habit.data.Repository.HabitRepoImpl
import com.example.habit.data.local.HabitDatabase
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HabitModule {
    @Provides
    @Singleton
    fun provideHabitDb(app: Application): HabitDatabase {
        return Room.databaseBuilder(
            app,
            HabitDatabase::class.java,
            HabitDatabase.dbName
        ).fallbackToDestructiveMigration().build();
    }

    @Provides
    @Singleton
    fun provideHabitRepo(habitDatabase: HabitDatabase):HabitRepo{
        return HabitRepoImpl(habitDatabase.habitDao)
    }

    @Provides
    fun providesScheduleAlarmUseCase(): ScheduleAlarmUseCase {
        return ScheduleAlarmUseCase()
    }


}