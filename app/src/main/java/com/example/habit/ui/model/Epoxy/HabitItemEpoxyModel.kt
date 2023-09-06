package com.example.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.HomeHabitItemLayoutBinding
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.util.DpPxUtils

data class HabitItemEpoxyModel(
    val habitTitleProperties:HomeElements.Typography,
    val habit:HomeElements.HabitsSectionItem,
    val leftMargin:Float,
    val rightMargin:Float,
    val topMargin:Float,
    val bottomMargin:Float,
    val leftPadding:Float,
    val rightPadding:Float,
    val topPadding:Float,
    val bottomPadding:Float,
    val imageCornerRadius: Float,
    val imageHeight: Float,
    val habitWidth: String?,
    val habitHeight: String?,
    val onClick:(habitTitle:String) -> Unit
):ViewBindingKotlinModel<HomeHabitItemLayoutBinding>(R.layout.home_habit_item_layout){
    override fun HomeHabitItemLayoutBinding.bind() {
        habitCard.layoutParams.apply { 
            this as ConstraintLayout.LayoutParams
            width = when(habitWidth){
                "fill_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> habitWidth?.toInt()?:ViewGroup.LayoutParams.WRAP_CONTENT
            }
            height = when(habitHeight){
                "fill_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                else -> habitHeight?.toInt()?:ViewGroup.LayoutParams.WRAP_CONTENT
            }
            setMargins(
                DpPxUtils.dpToPX(leftMargin.toFloat(),habitCard.context),
                DpPxUtils.dpToPX(topMargin.toFloat(),habitCard.context),
                DpPxUtils.dpToPX(rightMargin.toFloat(),habitCard.context),
                DpPxUtils.dpToPX(bottomMargin.toFloat(),habitCard.context),
            )
            habitCont.setPadding(
                DpPxUtils.dpToPX(leftPadding,habitCard.context),
                DpPxUtils.dpToPX(topPadding,habitCard.context),
                DpPxUtils.dpToPX(rightPadding,habitCard.context),
                DpPxUtils.dpToPX(bottomPadding,habitCard.context),
            )
            habitCard.radius=imageCornerRadius
        }
        habitCard.requestLayout()
        habitThumb.layoutParams.apply {
            height=imageHeight.toInt()
        }
        habitThumb.requestLayout()
        habitName.layoutParams.apply {
            this as LinearLayout.LayoutParams
           
            val gravityList= mutableListOf<Int>()
            if(habitTitleProperties.horizontalPosition.toLowerCase()=="start") gravityList.add(
                TextView.TEXT_ALIGNMENT_TEXT_START)
            if(habitTitleProperties.horizontalPosition.toLowerCase()=="end") gravityList.add(
                TextView.TEXT_ALIGNMENT_TEXT_END)
            if(habitTitleProperties.horizontalPosition.toLowerCase()=="center") gravityList.add(
                TextView.TEXT_ALIGNMENT_CENTER)
            habitName.textAlignment=gravityList.reduce { a, b -> a xor b } ?: TextView.TEXT_ALIGNMENT_TEXT_START
        }
        Glide.with(habitCard.context).load(habit.habitThumbnail).into(habitThumb)
        habitName.text=habit.habitName
        habitName.setTextColor(Color.parseColor(habitTitleProperties.textColor))
        habitName.textSize=habitTitleProperties.headerTextSize
        val userTextStyle=
            if(habitTitleProperties.headerTextStyle.toLowerCase()=="bold") Typeface.BOLD
            else if(habitTitleProperties.headerTextStyle.toLowerCase()=="normal") Typeface.NORMAL
            else if(habitTitleProperties.headerTextStyle.toLowerCase()=="italic") Typeface.ITALIC
            else if(habitTitleProperties.headerTextStyle.toLowerCase()=="bold_italic") Typeface.BOLD_ITALIC
            else Typeface.NORMAL
        habitName.typeface= Typeface.create(habitName.typeface,userTextStyle)
        habitCont.setOnClickListener {
            onClick(habit.habitName!!)
        }



    }

} 