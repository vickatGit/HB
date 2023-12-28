package com.habitude.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.habitude.habit.R
import com.habitude.habit.data.network.model.UiModels.HomePageModels.Action
import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.habitude.habit.databinding.ToolbarLayoutBinding
import com.habitude.habit.ui.util.DpPxUtils

data class NavSectionEpoxyModel(
    private val navSection: HomeElements.NavSection,
    val onClick: (Action) -> Unit
) : ViewBindingKotlinModel<ToolbarLayoutBinding>(R.layout.toolbar_layout) {
    override fun ToolbarLayoutBinding.bind() {
        toolbar.layoutParams.apply {
            this as RecyclerView.LayoutParams
            setMargins(
                DpPxUtils.dpToPX(navSection.marginLeft,toolbar.context),
                DpPxUtils.dpToPX(navSection.marginTop,toolbar.context),
                DpPxUtils.dpToPX(navSection.marginRight,toolbar.context),
                DpPxUtils.dpToPX(navSection.marginBottom,toolbar.context),
            )
            toolbar.setPadding(
                DpPxUtils.dpToPX(navSection.paddingLeft,toolbar.context),
                DpPxUtils.dpToPX(navSection.paddingTop,toolbar.context),
                DpPxUtils.dpToPX(navSection.paddingRight,toolbar.context),
                DpPxUtils.dpToPX(navSection.paddingBottom,toolbar.context),
            )
        }
        toolbar.requestLayout()
        navSection.navItems.forEach {
            when (it.sectionType) {
                "Menu" -> {
                    menu.layoutParams.apply {
                        this as FrameLayout.LayoutParams
                        width = DpPxUtils.dpToPX(it.iconSizeInDp, menu.context)
                        height = DpPxUtils.dpToPX(it.iconSizeInDp, menu.context)
                        val gravityList= mutableListOf<Int>()
                        if(it.iconHorizontalPosition.toLowerCase()=="start") gravityList.add(Gravity.START)
                        if(it.iconHorizontalPosition.toLowerCase()=="end") gravityList.add(Gravity.END)
                        if(it.iconVerticalPosition.toLowerCase()=="top") gravityList.add(Gravity.TOP)
                        if(it.iconVerticalPosition.toLowerCase()=="bottom") gravityList.add(Gravity.BOTTOM)
                        gravity=gravityList.reduce { a, b -> a xor b } ?: Gravity.NO_GRAVITY
                    }
                    menu.setOnClickListener { click -> it.action?.let { action -> onClick(action) } }

//                    menu.setColorFilter(Color.parseColor(it.iconColor),PorterDuff.Mode.SRC_ATOP)
                    menu.requestLayout()
                }

                "Notification" -> {
                    notification.layoutParams.apply {
                        this as FrameLayout.LayoutParams
                        width = DpPxUtils.dpToPX(it.iconSizeInDp, menu.context)
                        height = DpPxUtils.dpToPX(it.iconSizeInDp, menu.context)
                        val gravityList= mutableListOf<Int>()
                        if(it.iconHorizontalPosition.toLowerCase()=="start") gravityList.add(Gravity.START)
                        if(it.iconHorizontalPosition.toLowerCase()=="end") gravityList.add(Gravity.END)
                        if(it.iconVerticalPosition.toLowerCase()=="top") gravityList.add(Gravity.TOP)
                        if(it.iconVerticalPosition.toLowerCase()=="bottom") gravityList.add(Gravity.BOTTOM)
                        gravity=gravityList.reduce { a, b -> a xor b } ?: Gravity.NO_GRAVITY
                    }
//                    notification.setColorFilter(Color.parseColor(it.iconColor),PorterDuff.Mode.SRC_ATOP)
                    notification.requestLayout()
                    notification.setOnClickListener { click -> it.action?.let { action -> onClick(action) } }
                }
            }
        }
    }

}

