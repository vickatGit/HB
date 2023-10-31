package com.example.habit.di

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.habit.data.Repository.ChatRepoImpl
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.ChatApi
import com.example.habit.domain.Repository.ChatRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Singleton
    @Provides
    fun getSocket(): Socket {
        return IO.socket("https://hb-chat.onrender.com")!!
    }

    @Singleton
    @Provides
    fun provideChatApi(app: Application, auth: AuthPref): ChatApi {
        return Retrofit.Builder().apply {
            client(OkHttpClient.Builder().apply {
                connectTimeout(Duration.ofSeconds(5))
                addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer ${auth.getToken()}")
                        .build()
                    chain.proceed(request)
                }
                addInterceptor(ChuckerInterceptor(app))
            }.build())
            baseUrl("https://hb-chat.onrender.com/")
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(ChatApi::class.java)
    }

    @Singleton
    @Provides
    fun provideChatRepo(chatApi: ChatApi,auth: AuthPref):ChatRepo{
        return ChatRepoImpl(chatApi,auth.getUserId())
    }
}