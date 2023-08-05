package com.example.habit.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.habit.data.Mapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper
import com.example.habit.data.Repository.HabitRepoImpl
import com.example.habit.data.local.HabitDatabase
import com.example.habit.data.network.HabitApi
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.UseCases.DeleteAlarmUseCase
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HabitModule {
    companion object{
        private const val API="api"
    }

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
    fun provideHabitRepo(habitDatabase: HabitDatabase,habitApi: HabitApi,connectivityManager: ConnectivityManager):HabitRepo{
        return HabitRepoImpl(habitDatabase.habitDao,HabitMapper(EntryMapper()),EntryMapper(),habitApi,connectivityManager)
    }

    @Provides
    fun providesScheduleAlarmUseCase(): ScheduleAlarmUseCase {
        return ScheduleAlarmUseCase()
    }

    @Provides
    fun providesDeleteAlarmUseCase(): DeleteAlarmUseCase {
        return DeleteAlarmUseCase()
    }

//    @Provides
//    fun dataHabitMapper(entryMapper: EntryMapper): HabitMapper {
//       return HabitMapper(entryMapper)
//    }

//    @Provides
//    fun dataEntryMapper(): EntryMapper {
//        return EntryMapper()
//    }

    @Provides
    fun uiHabitMapper(entryMapper : com.example.habit.ui.mapper.EntryMapper):com.example.habit.ui.mapper.HabitMapper{
        return com.example.habit.ui.mapper.HabitMapper(entryMapper)
    }

    @Provides
    fun uiEntryMapper():com.example.habit.ui.mapper.EntryMapper{
        return com.example.habit.ui.mapper.EntryMapper()
    }

    @Provides
    @Singleton
    fun provideRetrofit(app: Application, httpClient: OkHttpClient ): Retrofit {
        return Retrofit.Builder().apply {
            client(httpClient)
            baseUrl("https://39fc-2409-4042-4d90-a830-4154-3528-a113-7c22.ngrok-free.app/")
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Provides
    @Singleton
    fun provideApiClient(retrofit: Retrofit): HabitApi {
        return retrofit.create(HabitApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(app: Application): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(Duration.ofSeconds(5))
            addInterceptor { chain ->
                val request = chain.request()
                Log.i(API, "intercept: ${request.url}")
                chain.proceed(request)
            }
            addInterceptor(ChuckerInterceptor(app))
        }.build()
    }


    @Provides
    fun provideConnectivityManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }


}