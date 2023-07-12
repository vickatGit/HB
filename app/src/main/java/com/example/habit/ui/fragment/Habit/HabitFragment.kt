package com.example.habit.ui.fragment.Habit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import android.widget.LinearLayout.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.example.habit.R
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.CalendarLayoutBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentHabitBinding
import com.example.habit.databinding.FragmentHabitsBinding
import com.example.habit.ui.callback.DateClick
import com.example.habit.ui.fragment.Date.DayHolder
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HabitFragment : Fragment() {

    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    private var _calendarBinding : CalendarLayoutBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    private var _weekDaysBinding : CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDaysBinding!!


    var weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()
    private var currentMonth: YearMonth? = null
    private var selectedDates= mutableMapOf<LocalDate,LocalDate>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding=FragmentHabitBinding.inflate(inflater,container,false)
        _calendarBinding= CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)

        initialiseCalendar()


        return binding.root
    }

    private fun initialiseCalendar() {
        bindWeekDays()
        bindDays()
        calendarBinding.calendarView.monthScrollListener = { calendarMonth: CalendarMonth ->
            calendarBinding.month.setText(getMonthYearString(calendarMonth))
            currentMonth = calendarMonth.yearMonth
        }
        currentMonth = YearMonth.now()
        val startMonth = currentMonth?.minusMonths(100) // Adjust as needed

        val endMonth = currentMonth?.plusMonths(100) // Adjust as needed

        calendarBinding.calendarView.setup(startMonth!!, endMonth!!, firstDayOfWeekFromLocale())
        calendarBinding.calendarView.scrollToMonth(currentMonth!!)
    }

    private fun bindDays() {
        calendarBinding.calendarView.dayBinder= object : MonthDayBinder<DayHolder> {
            override fun bind(container: DayHolder, calendarDay: CalendarDay) {
                container.day=calendarDay

                when (calendarDay.position) {
                    DayPosition.MonthDate -> {
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.medium_orange))
                    }
                    DayPosition.InDate -> {
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                    }
                    DayPosition.OutDate -> {
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                    }
                }


                container.dayBinding.calendarDayText.setBackgroundResource(R.color.white)
                container.dayBinding.calendarDayText.text = calendarDay.date.dayOfMonth.toString() + ""

                if (calendarDay.position == DayPosition.MonthDate) {
                    container.dayBinding.calendarDayText.isVisible = true
                    if (selectedDates.containsValue(calendarDay.date)) {
                        container.dayBinding.calendarDayText.setTextColor(
                            ContextCompat.getColor( requireContext() , R.color.white )
                        )
                        container.dayBinding.calendarDayText.setBackgroundResource(R.drawable.book_now_bg)

                    }
                }
                val params = container.dayBinding.calendarDayText.layoutParams as MarginLayoutParams
                params.leftMargin=10
                params.bottomMargin=10
                params.topMargin=10
                params.rightMargin=10


            }

            override fun create(view: View): DayHolder {
                return DayHolder(DayBinding.bind(view), object : DateClick {
                    override fun dateClick(date: LocalDate?) {
                        selectDate(date)
                    }
                } )
            }
        }
    }

    private fun selectDate(date: LocalDate?) {
        date?.let{
            if (selectedDates.containsValue(it) ) {
                selectedDates.remove(it)
            }else{
                selectedDates[it] = it
            }
            calendarBinding.calendarView.notifyDateChanged(it)
        }
    }

    private fun bindWeekDays() {
        (0 until weekDayBinding.root.childCount).forEach { index ->
            val childView: View = weekDayBinding.root.getChildAt(index)
            if (childView is TextView && childView.getId() == R.id.legendText1) {
                childView.text = weekdays[index] // Set your desired text here
            }
        }
    }

    private fun getMonthYearString(calendarMonth: CalendarMonth): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return calendarMonth.yearMonth.format(formatter)
    }

    fun dpToPx(dp: Int): Int {
        val deviceDensity = resources.displayMetrics.density
        val scale = deviceDensity / 160f
        return (dp * scale + 0.5f).toInt()
    }

}