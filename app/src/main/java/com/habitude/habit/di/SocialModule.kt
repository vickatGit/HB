package com.habitude.habit.di

import android.app.Application
import com.habitude.habit.data.Repository.SocialRepoImpl
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.data.network.SocialApi
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactory
import com.habitude.habit.domain.Repository.SocialRepo
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
    @HabitModule.MainRetrofit
    @Provides
    fun provideSocialApiClient( @HabitModule.MainRetrofit retrofit: Retrofit): SocialApi {
        return retrofit.create(SocialApi::class.java)
    }

    @Singleton
    @HabitModule.WithoutAuthRetrofit
    @Provides
    fun provideSocialApiClientWithoutAuth( @HabitModule.WithoutAuthRetrofit retrofit: Retrofit): SocialApi {
        return retrofit.create(SocialApi::class.java)
    }




    @Singleton
    @Provides
    fun provideSocialRepo (@HabitModule.MainRetrofit socialApi: SocialApi, @HabitModule.WithoutAuthRetrofit noAuthSocialApi: SocialApi, app: Application, homeElementsFactory: HomeSectionsFactory, authPref: AuthPref): SocialRepo {
        return SocialRepoImpl(socialApi,noAuthSocialApi,app,homeElementsFactory,authPref)
    }


}