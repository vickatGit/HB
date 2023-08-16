package com.example.habit.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.habit.data.Mapper.HabitMapper.EntryMapper
import com.example.habit.data.Mapper.HabitMapper.HabitMapper
import com.example.habit.data.Repository.HabitRepoImpl
import com.example.habit.data.SyncManager
import com.example.habit.data.local.HabitDatabase
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.HabitApi
import com.example.habit.domain.Repository.HabitRepo
import com.example.habit.domain.UseCases.HabitUseCase.DeleteAlarmUseCase
import com.example.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideHabitRepo(app: Application,habitDatabase: HabitDatabase,habitApi: HabitApi,connectivityManager: ConnectivityManager,syncRequest: OneTimeWorkRequest):HabitRepo{
        return HabitRepoImpl(habitDatabase.habitDao,
            HabitMapper(EntryMapper()),
            EntryMapper(),
            habitApi,
            connectivityManager,
            syncRequest,
            WorkManager.getInstance(app)
        )
    }

    @Provides
    fun providesScheduleAlarmUseCase(): ScheduleAlarmUseCase {
        return ScheduleAlarmUseCase()
    }

    @Provides
    fun providesDeleteAlarmUseCase(): DeleteAlarmUseCase {
        return DeleteAlarmUseCase()
    }

    @Provides
    fun uiHabitMapper(entryMapper : com.example.habit.ui.mapper.HabitMapper.EntryMapper): com.example.habit.ui.mapper.HabitMapper.HabitMapper {
        return com.example.habit.ui.mapper.HabitMapper.HabitMapper(entryMapper)
    }

    @Provides
    fun uiEntryMapper(): com.example.habit.ui.mapper.HabitMapper.EntryMapper {
        return com.example.habit.ui.mapper.HabitMapper.EntryMapper()
    }

    @Provides
    @Singleton
    fun provideRetrofit(app: Application, httpClient: OkHttpClient ): Retrofit {
        return Retrofit.Builder().apply {
            client(httpClient)
            baseUrl("http://192.168.43.53:8080/")
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Provides
    @Singleton
    fun provideHabitApiClient(retrofit: Retrofit): HabitApi {
        return retrofit.create(HabitApi::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthPreference(app: Application): AuthPref {
        return AuthPref(app)
    }

    @Provides
    @Singleton
    fun provideHttpClient(app: Application,auth:AuthPref): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(Duration.ofSeconds(5))
            addInterceptor { chain ->
                val request = chain.request()
                request.newBuilder().apply {
                    header("Authorization","Bearer ${auth.getToken()}")
                }.build()
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

    @Provides
    @Singleton
    fun getSyncRequest(): OneTimeWorkRequest {

        val syncRequest= OneTimeWorkRequestBuilder<SyncManager>().apply {
            setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            setInitialDelay(Duration.ofSeconds(0))
        }.build()
        return syncRequest
    }



//    @Provides
//    @Singleton
//    fun provideWorkManager(app: Application): WorkManager {
//        return WorkManager.getInstance(app)
//    }

}