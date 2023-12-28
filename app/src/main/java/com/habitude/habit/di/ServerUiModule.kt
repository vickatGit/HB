package com.habitude.habit.di

import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.ActionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.ActionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HabitCarousalSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HabitCarousalSectionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HabitSectionItemFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HabitSectionItemFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HeaderSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HeaderSectionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HomeElemFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HomeElemFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.HomeSectionsFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.NavItemFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.NavItemFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.NavSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.NavSectionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.QouteCarousalSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.QouteCarousalSectionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.TypographyFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.TypographyFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserInfoSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserInfoSectionFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserProfileImageFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserProfileImageFactoryImpl
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserProgressSectionFactory
import com.habitude.habit.data.network.model.UiModels.HomePageModels.factories.UserProgressSectionFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServerUiModule {

    @Binds
    abstract fun bindNavItemFactory(impl:NavItemFactoryImpl):NavItemFactory

    @Binds
    abstract fun bindNavSectionFactory(impl:NavSectionFactoryImpl):NavSectionFactory

    @Binds
    abstract fun bindProfileImageFactory(impl:UserProfileImageFactoryImpl):UserProfileImageFactory

    @Binds
    abstract fun bindTypographyFactory(impl:TypographyFactoryImpl):TypographyFactory


    @Binds
    abstract fun bindHeaderSectionFactory(impl: HeaderSectionFactoryImpl):HeaderSectionFactory

    @Binds
    abstract fun bindQuoteCarousalFactory(impl:QouteCarousalSectionFactoryImpl):QouteCarousalSectionFactory

    @Binds
    abstract fun bindUserProgressSection(impl:UserProgressSectionFactoryImpl):UserProgressSectionFactory

    @Binds
    abstract fun bindUserInfoSectionFactory(impl: UserInfoSectionFactoryImpl): UserInfoSectionFactory

    @Binds
    abstract fun bindHomeElemFactory(impl: HomeElemFactoryImpl):HomeElemFactory

    @Binds
    abstract fun bindHomeElementFactory(impl:HomeSectionsFactoryImpl):HomeSectionsFactory

    @Binds
    abstract fun bindActionFactory(impl:ActionFactoryImpl):ActionFactory

    @Binds
    abstract fun bindHabitSectionItemFactory(impl:HabitSectionItemFactoryImpl):HabitSectionItemFactory

    @Binds
    abstract fun bindHabitCarousalSectionFactory(impl:HabitCarousalSectionFactoryImpl):HabitCarousalSectionFactory
}