package com.example.habit.ui.fragment.Habit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.CalendarLayoutBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentHabitBinding
import com.example.habit.domain.UseCases.GetHabitThumbUseCase
import com.example.habit.ui.callback.DateClick
import com.example.habit.ui.fragment.Date.DayHolder
import com.example.habit.ui.mapper.HabitMapper
import com.example.habit.ui.model.EntryView
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.notification.NotificationBuilder
import com.example.habit.ui.viewmodel.HabitViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HabitFragment : Fragment() {
    companion object {
        const val HABIT_ID: String = "habit_id_key"
        lateinit var image:ImageView
    }

    private lateinit var habit: HabitView
    private var habitDurationReached: Long? = null
    private var totalHabitDuration: Long? = null
    private val viewModel: HabitViewModel by viewModels()
    private var habitEntries: HashMap<LocalDate, EntryView>   = hashMapOf()


    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    private var _calendarBinding: CalendarLayoutBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    private var _weekDaysBinding: CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDaysBinding!!

    @Inject
    lateinit var notificationBuilder:NotificationBuilder

    @Inject
    lateinit var habitMapper:HabitMapper

    @Inject
    lateinit var getHabitThumbUseCase:GetHabitThumbUseCase



    var weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDates = mutableMapOf<LocalDate, LocalDate>()
    private var habitId: String? = null
    private var habitStartDate: LocalDate? = null
    private var habitEndDate: LocalDate? = null
    private var isCalendarEditable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        habitId = arguments?.getString(HABIT_ID)



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHabitBinding.inflate(inflater, container, false)
        _calendarBinding = CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.habitUiState.collectLatest {
                    when (it) {
                        is HabitUiState.HabitData -> {
                            habit=it.habit
                            bindHabitPageData(it.habit)
                        }

                        is HabitUiState.Error -> {
                            Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                            binding.progress.isVisible = false
                        }

                        is HabitUiState.Loading -> {
                            binding.progress.isVisible = true
                        }

                        is HabitUiState.HabitEntries -> {
                            Log.e(
                                "TAG",
                                "updateEntry: before update ${Gson().toJson(habitEntries)}",
                            )
                            habitEntries.clear()
                            habitEntries = it.habitEntries
                            bindDays()
                            binding.progress.isVisible = false
                        }
                        is HabitUiState.HabitDeleted -> {
                            binding.progress.isVisible=false
                            Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                        is HabitUiState.Nothing -> {
                            binding.progress.isVisible = false
                        }

                    }
                }
            }
        }
        binding.edit.setOnClickListener {
            habit?.let {
                val args = Bundle().apply {
                    putBoolean("isUpdate", true)
                    putParcelable("habit", habit)
                }
                findNavController().navigate(R.id.action_habitFragment_to_addHabitFragment, args)
            }
        }
        binding.delete.setOnClickListener {
            habit?.let {
                viewModel.deleteHabit(habitId = it.id!!,getString(R.string.habit_deletion_success_msg),getString(
                                    R.string.habit_deletion_failed_msg))
            }
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        habitId?.let { viewModel.getHabit(it, getString(R.string.habit_not_found_error)) }

        return binding.root
    }

    private fun bindHabitPageData(habit: HabitView) {
        binding.header.text = habit.title
        binding.progress.isVisible = false
        habit.entries?.let {
            habitEntries = it
            habitEntries?.map {
                selectedDates.put(it.key, it.value.timestamp!!)
            }
        }
        initialiseProgress(habit)
        initialiseCalendar(habit.startDate!!, habit.endDate!!)
        binding.streakEditSwitch.setOnCheckedChangeListener { _, isChecked ->
            isCalendarEditable = isChecked
            bindDays()
        }

    }

    private fun initialiseProgress(habit: HabitView) {
        totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)
        habitDurationReached =
            ChronoUnit.DAYS.between(habit.startDate,LocalDate.now())
        val progress = (totalHabitDuration!! / 100f) * habitDurationReached!!
        binding.habitProgress.progress = progress.roundToInt()
        binding.progressPercentage.text = "${DecimalFormat("#.#").format(progress)}%"
    }

    private fun initialiseCalendar(startDate: LocalDate, endDate: LocalDate) {
        habitStartDate = startDate
        habitEndDate = endDate
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
        calendarBinding.calendarView.dayBinder = object : MonthDayBinder<DayHolder> {
            override fun bind(container: DayHolder, calendarDay: CalendarDay) {
                container.day = calendarDay
                container.dayBinding.root.setOnTouchListener { _, _ -> return@setOnTouchListener !isCalendarEditable }

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
                container.dayBinding.calendarDayText.text =
                    calendarDay.date.dayOfMonth.toString() + ""

                if (calendarDay.position == DayPosition.MonthDate) {
                    container.dayBinding.calendarDayText.isVisible = true
                    if (habitEntries.containsKey(calendarDay.date) && habitEntries[calendarDay.date]!!.completed) {
                        container.dayBinding.calendarDayText.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.white)
                        )
                        container.dayBinding.calendarDayText.setBackgroundResource(R.drawable.book_now_bg)

                    }
                    if (calendarDay.date.isBefore(habitStartDate) || calendarDay.date.isAfter(
                            habitEndDate
                        )
                    )
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                }
                val params = container.dayBinding.calendarDayText.layoutParams as MarginLayoutParams
                params.leftMargin = 10
                params.bottomMargin = 10
                params.topMargin = 10
                params.rightMargin = 10

            }

            override fun create(view: View): DayHolder {
                return DayHolder(DayBinding.bind(view), object : DateClick {
                    override fun dateClick(date: LocalDate?) {
                        date?.let {
                            if (!date.isBefore(habitStartDate) && !date.isAfter(habitEndDate)) {
                                selectDate(date)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun selectDate(date: LocalDate?) {
        date?.let {
            if (habitEntries.containsKey(it)) {
                habitEntries[it]!!.completed = !habitEntries[it]!!.completed
                Log.e("TAG", "updateEntry selectDate: present $date ")
                updateEntries(it, habitEntries[it]!!.completed)
            } else {
                habitEntries[it] = EntryView(it!!, 0,true)
                Log.e("TAG", "updateEntry selectDate: not present $date ")
                updateEntries(it, true)
            }
            calendarBinding.calendarView.notifyDateChanged(it)
        }
    }


    private fun updateEntries(date: LocalDate, isUpgrade: Boolean) {
        val prevEntry = habitEntries[date.minusDays(1)]

        if (prevEntry == null) {
            habitEntries[date] = EntryView(date, if (isUpgrade) 1 else 0, isUpgrade)
        }
        val habitList= mutableListOf<EntryView>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }

        habitList.forEachIndexed {index,it ->
            if (index>0 && (it.timestamp!!.isAfter(date) || it.timestamp.isEqual(date))) {
                var score=habitList.get(if(index!=0) index-1 else index).score
                habitList[index].score=if (isUpgrade) score!!+1 else it.score!!-1
            }
        }
        habitEntries.putAll(habitList.associateBy { it.timestamp!! })
        habitId?.let { viewModel.updateHabitEntries(it.toInt(), habitEntries) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
        _calendarBinding=null
        _weekDaysBinding=null
    }


}


