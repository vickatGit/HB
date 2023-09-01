package com.example.habit.di

import android.app.Application
import com.example.habit.data.Repository.SocialRepoImpl
import com.example.habit.data.network.SocialApi
import com.example.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactory
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
    fun provideSocialRepo (socialApi: SocialApi,app: Application,homeElementsFactory: HomeSectionsFactory ): SocialRepo {
        return SocialRepoImpl(socialApi,app,homeElementsFactory)
    }
}