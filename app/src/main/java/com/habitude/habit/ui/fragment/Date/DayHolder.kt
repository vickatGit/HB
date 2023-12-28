package com.habitude.habit.ui.fragment.Date

import com.habitude.habit.databinding.DayBinding
import com.habitude.habit.ui.callback.DateClick
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer

class DayHolder(view: DayBinding,dateClick: DateClick) : ViewContainer(view.root) {
    var dayBinding:DayBinding=view
    var day: CalendarDay? = null
    init {
        dayBinding.calendarDayText.setOnClickListener {
            if (day?.position == DayPosition.MonthDate) {
                dateClick.dateClick(day?.date)
            }
        }
    }
}