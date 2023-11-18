package com.example.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.Action
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.UserSecctionLayoutBinding
import com.example.habit.ui.util.DpPxUtils

data class UserInfoSectionEpoxyModel(
    private val navSection: HomeElements.UserInfoSection,
    val onClick:(action:Action, avatarUrl:String?) -> Unit
) : ViewBindingKotlinModel<UserSecctionLayoutBinding>(R.layout.user_secction_layout) {
    override fun UserSecctionLayoutBinding.bind() {
        userCont.layoutParams.apply {
            this as RecyclerView.LayoutParams
            setMargins(
                DpPxUtils.dpToPX(navSection.marginLeft,userCont.context),
                DpPxUtils.dpToPX(navSection.marginTop,userCont.context),
                DpPxUtils.dpToPX(navSection.marginRight,userCont.context),
                DpPxUtils.dpToPX(navSection.marginBottom,userCont.context),
            )
            userCont.setPadding(
                DpPxUtils.dpToPX(navSection.paddingLeft,userCont.context),
                DpPxUtils.dpToPX(navSection.paddingTop,userCont.context),
                DpPxUtils.dpToPX(navSection.paddingRight,userCont.context),
                DpPxUtils.dpToPX(navSection.paddingBottom,userCont.context),
            )
        }
        userCont.requestLayout()
        navSection.elements.forEach {
            when(it){
                is HomeElements.Typography -> {
                    userName.text=it.headerText
//                    userName.setTextColor(Color.parseColor(it.textColor))
                    userName.textSize=it.headerTextSize
                    val userTextStyle=
                        if(it.headerTextStyle.toLowerCase()=="bold") Typeface.BOLD
                        else if(it.headerTextStyle.toLowerCase()=="normal") Typeface.NORMAL
                        else if(it.headerTextStyle.toLowerCase()=="italic") Typeface.ITALIC
                        else if(it.headerTextStyle.toLowerCase()=="bold_italic") Typeface.BOLD_ITALIC
                        else Typeface.NORMAL
                    userName.typeface=Typeface.create(userName.typeface,userTextStyle)
                    userName.layoutParams.apply {
                        this as LinearLayout.LayoutParams
                        val gravityList= mutableListOf<Int>()
                        if(it.horizontalPosition.toLowerCase()=="start") gravityList.add(Gravity.START)
                        if(it.horizontalPosition.toLowerCase()=="end") gravityList.add(Gravity.END)
                        if(it.verticalPosition.toLowerCase()=="top") gravityList.add(Gravity.TOP)
                        if(it.verticalPosition.toLowerCase()=="bottom") gravityList.add(Gravity.BOTTOM)
                        gravity=gravityList.reduce { a, b -> a xor b } ?: Gravity.NO_GRAVITY
                    }

                }
                is HomeElements.UserProfileImage -> {
                    when(it.shape.toLowerCase()) {
                        "circle" -> {
                            userCircleAvatar.isVisible=true
                            if(!it.url.isNullOrBlank())
                                Glide.with(userCircleAvatar).load(it.url).into(userCircleAvatar)
                            userCircleAvatar.layoutParams.apply {
                                this as LinearLayout.LayoutParams
                                width=DpPxUtils.dpToPX(it.sizeIndDp,userCircleAvatar.context)
                                height=DpPxUtils.dpToPX(it.sizeIndDp,userCircleAvatar.context)
                                val gravityList= mutableListOf<Int>()
                                if(it.horizontalPosition.toLowerCase()=="start") gravityList.add(Gravity.START)
                                if(it.horizontalPosition.toLowerCase()=="end") gravityList.add(Gravity.END)
                                if(it.verticalPosition.toLowerCase()=="top") gravityList.add(Gravity.TOP)
                                if(it.verticalPosition.toLowerCase()=="bottom") gravityList.add(Gravity.BOTTOM)
                                gravity=gravityList.reduce { a, b -> a xor b } ?: Gravity.NO_GRAVITY
                            }
                            userCircleAvatar.requestLayout()
                            userCircleAvatar.setOnClickListener { click -> it.action?.let { action -> onClick(action,it.url) } }
                        }
                        "square" -> {
                            userSquareAvatarCont.isVisible=true
                            userSquareAvatarCont.radius=it.cornerRadius
                            if(!it.url.isNullOrBlank())
                                Glide.with(userSquareAvatar).load(it.url).into(userSquareAvatar)
                            userSquareAvatarCont.layoutParams.apply {
                                this as LinearLayout.LayoutParams
                                width=DpPxUtils.dpToPX(it.sizeIndDp,userSquareAvatar.context)
                                height=DpPxUtils.dpToPX(it.sizeIndDp,userSquareAvatar.context)
                                val gravityList= mutableListOf<Int>()
                                if(it.horizontalPosition.toLowerCase()=="start") gravityList.add(Gravity.START)
                                if(it.horizontalPosition.toLowerCase()=="end") gravityList.add(Gravity.END)
                                if(it.verticalPosition.toLowerCase()=="top") gravityList.add(Gravity.TOP)
                                if(it.verticalPosition.toLowerCase()=="bottom") gravityList.add(Gravity.BOTTOM)
                                gravity=gravityList.reduce { a, b -> a xor b } ?: Gravity.NO_GRAVITY
                            }
                            userSquareAvatarCont.requestLayout()
                            userSquareAvatar.setOnClickListener { click -> it.action?.let { action -> onClick(action,it.url) } }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}