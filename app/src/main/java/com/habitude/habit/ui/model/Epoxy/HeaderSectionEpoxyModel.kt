package com.habitude.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habitude.habit.R
import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.habitude.habit.databinding.HeaderLayoutBinding
import com.habitude.habit.ui.util.DpPxUtils

data class HeaderSectionEpoxyModel(
    private val headerSection: HomeElements.HeaderSection
):ViewBindingKotlinModel<HeaderLayoutBinding>(R.layout.header_layout) {
    override fun HeaderLayoutBinding.bind() {
        header.layoutParams.apply {
            this as RecyclerView.LayoutParams
            setMargins(
                DpPxUtils.dpToPX(headerSection.marginLeft,header.context),
                DpPxUtils.dpToPX(headerSection.marginTop,header.context),
                DpPxUtils.dpToPX(headerSection.marginRight,header.context),
                DpPxUtils.dpToPX(headerSection.marginBottom,header.context),
            )
            header.setPadding(
                DpPxUtils.dpToPX(headerSection.paddingLeft,header.context),
                DpPxUtils.dpToPX(headerSection.paddingTop,header.context),
                DpPxUtils.dpToPX(headerSection.paddingRight,header.context),
                DpPxUtils.dpToPX(headerSection.paddingBottom,header.context),
            )
            val gravityList= mutableListOf<Int>()
            if(headerSection.element.horizontalPosition.toLowerCase()=="start") gravityList.add(TextView.TEXT_ALIGNMENT_TEXT_START)
            if(headerSection.element.horizontalPosition.toLowerCase()=="end") gravityList.add(TextView.TEXT_ALIGNMENT_TEXT_END)
            if(headerSection.element.horizontalPosition.toLowerCase()=="center") gravityList.add(TextView.TEXT_ALIGNMENT_CENTER)
            header.textAlignment=gravityList.reduce { a, b -> a xor b } ?: TextView.TEXT_ALIGNMENT_TEXT_START
        }
        header.requestLayout()
        header.text=headerSection.element.headerText
        header.setTextColor(this.header.resources.getColor(R.color.text_color))
        header.textSize=headerSection.element.headerTextSize
        val userTextStyle=
            if(headerSection.element.headerTextStyle.toLowerCase()=="bold") Typeface.BOLD
            else if(headerSection.element.headerTextStyle.toLowerCase()=="normal") Typeface.NORMAL
            else if(headerSection.element.headerTextStyle.toLowerCase()=="italic") Typeface.ITALIC
            else if(headerSection.element.headerTextStyle.toLowerCase()=="bold_italic") Typeface.BOLD_ITALIC
            else Typeface.NORMAL
        header.typeface= Typeface.create(header.typeface,userTextStyle)


    }
}