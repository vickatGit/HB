package com.example.habit.data.network.model.UiModels.HomePageModels

import kotlinx.serialization.Serializable

@Serializable
sealed class HomeElements : Section {

    @Serializable
    data class NavSection(
        override val id: String,
        override val sectionType: String,
        val navItems: List<NavSectionItem>,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,
    ) : HomeElements()

    @Serializable
    data class Typography(
        override val id: String,
        override val sectionType: String,
        val headerText: String?,
        val headerTextSize: Float,
        val headerTextStyle: String,
        val verticalPosition: String,
        val horizontalPosition: String,
        val headerFont: String?,
        val textColor:String
    ) : HomeElements()

    @Serializable
    data class QuoteSection(
        override val id: String,
        override val sectionType: String,
        val quotes: List<String>,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,
    ) : HomeElements()

    @Serializable
    data class NavSectionItem(
        override val id: String,
        override val sectionType: String,
        val icon: String,
        val iconSizeInDp: Float,
        val iconColor: String,
        val iconVerticalPosition: String,
        val iconHorizontalPosition: String,
        val action: HomeActionScreen?
    ) : HomeElements()

    @Serializable
    data class UserProfileImage(
        override val id: String,
        override val sectionType: String,
        val url: String?,
        val shape: String,
        val sizeIndDp: Float,
        val verticalPosition: String,
        val horizontalPosition: String,
        val cornerRadius:Float
    ) : HomeElements()

    @Serializable
    data class UserInfoSection(
        override val id: String,
        override val sectionType: String,
        val elements: List<HomeElements?>,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,

        ) : HomeElements()

    @Serializable
    data class HeaderSection(
        override val id: String,
        override val sectionType: String,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,
        val element: Typography,


    ) : HomeElements()

    @Serializable
    data class QuoteCarousalSection(
        override val id: String,
        override val sectionType: String,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,
        val images:List<String>
    ):HomeElements()

    @Serializable
    data class UserProgressSection(
        override val id: String,
        override val sectionType: String,
        val paddingTop: Float,
        val paddingBottom: Float,
        val paddingLeft: Float,
        val paddingRight: Float,
        val marginTop: Float,
        val marginBottom: Float,
        val marginLeft: Float,
        val marginRight: Float,
        val progressTypographyProperties: Typography,
        val progressType: String,
        val isAnimationOn: Boolean,
        val animationSpeed: Float,
        val shouldShowProgressPercentage: Boolean,
        val progressPrimaryColor: String,
        val progressSecondaryColor: String,
        val progressPercentageTypographyProperties: Typography,
        val habitProgressType: String,
        val habitPercentageTypographyProperties: Typography,
        val progressSize: Float,
        val progressBackgroundColor: String,

        ):HomeElements()


}
