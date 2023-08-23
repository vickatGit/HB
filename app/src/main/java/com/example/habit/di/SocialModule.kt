package com.example.habit.di

import com.example.habit.data.Repository.SocialRepoImpl
import com.example.habit.data.network.AuthApi
import com.example.habit.data.network.SocialApi
import com.example.habit.domain.Repository.SocialRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocialModule {

    @Singleton
    @Provides
    fun provideSocialApiClient(retrofit: Retrofit): SocialApi {
        return retrofit.create(SocialApi::class.java)
    }




    @Singleton
    @Provides
    fun provideSocialRepo (socialApi: SocialApi ): SocialRepo {
        return SocialRepoImpl(socialApi)
    }
}