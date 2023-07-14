package com.example.habit.ui.fragment.Habit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.habit.R
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.CalendarLayoutBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentHabitBinding
import com.example.habit.ui.callback.DateClick
import com.example.habit.ui.fragment.Date.DayHolder
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.viewmodel.HabitViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class HabitFragment : Fragment() {
    companion object {
        const val HABIT_ID: String = "habit_id_key"
    }

    private var habitDurationReached: Long? = null
    private var totalHabitDuration: Long? = null
    private val viewModel : HabitViewModel  by viewModels()


    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    private var _calendarBinding : CalendarLayoutBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    private var _weekDaysBinding : CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDaysBinding!!


    var weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDates= mutableMapOf<LocalDate,LocalDate>()
    private var habitId:String? = null
    private var habitStartDate:LocalDate?=null
    private var habitEndDate:LocalDate?=null
    private var isCalendarEditable:Boolean=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding=FragmentHabitBinding.inflate(inflater,container,false)
        _calendarBinding= CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)
        habitId=arguments?.getString(HABIT_ID)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.habitUiState.collectLatest {
                    when(it){
                        is HabitUiState.DataFetched -> {
                            bindHabitPageData(it.habit)
                        }
                        is HabitUiState.Error -> {
                            Toast.makeText(requireContext(),it.error,Toast.LENGTH_SHORT).show()
                            binding.progress.isVisible=false
                        }
                        is HabitUiState.Loading -> {
                            binding.progress.isVisible=true
                        }
                        is HabitUiState.Nothing -> { }
                    }
                }
            }
        }
        habitId?.let { viewModel.getHabit(it,getString(R.string.habit_not_found_error)) }

        return binding.root
    }

    private fun bindHabitPageData(habit: HabitView) {
        binding.header.text = habit.title
        binding.progress.isVisible=false
        totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate,habit.endDate)
        habitDurationReached = ChronoUnit.DAYS.between(habit.startDate,habit.startDate?.plusDays(1))
        val progress = (totalHabitDuration!!/100f)*habitDurationReached!!
        binding.habitProgress.progress=progress.roundToInt()
        binding.progressPercentage.text="${DecimalFormat("#.#").format(progress)}%"
        initialiseCalendar(habit.startDate!!,habit.endDate!!)
        binding.streakEditSwitch.setOnCheckedChangeListener { _, isChecked ->
            isCalendarEditable=isChecked
            bindDays()
        }

    }

    private fun initialiseCalendar(startDate: LocalDate, endDate: LocalDate) {
        habitStartDate=startDate
        habitEndDate=endDate
        bindWeekDays()
        bindDays()
        calendarBinding.calendarView.monthScrollListener = { calendarMonth: CalendarMonth ->
            calendarBinding.month.text = getMonthYearString(calendarMonth)
        }


        val startMonth = YearMonth.of(startDate.year, startDate.month)
        val endMonth = YearMonth.of(endDate.year, endDate.month)

        calendarBinding.calendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())
        calendarBinding.calendarView.scrollToMonth(startMonth)

    }


    private fun bindDays() {

        calendarBinding.calendarView.dayBinder= object : MonthDayBinder<DayHolder> {
            override fun bind(container: DayHolder, calendarDay: CalendarDay) {
                container.day=calendarDay
                container.dayBinding.root.setOnTouchListener { _, _ ->  return@setOnTouchListener !isCalendarEditable}

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
                    if(calendarDay.date.isBefore(habitStartDate) || calendarDay.date.isAfter(habitEndDate)){
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
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
                        date?.let {
                            if(!date.isBefore(habitStartDate) && !date.isAfter(habitEndDate)) {
                                selectDate(date)
                            }
                        }
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