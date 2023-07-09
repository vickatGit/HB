package com.example.habit.ui.fragment.Date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentDateBinding
import com.example.habit.ui.callback.DateClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DateFragment : BottomSheetDialogFragment() {

    companion object{
        const val DATE_RESULT: String= "date_result"
        const val DATE: String= "date"
    }


    private var _weekDayBinding: CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDayBinding!!
    private var _binding: FragmentDateBinding? = null
    private val binding get() = _binding!!
    var weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()
    private var currentMonth: YearMonth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentDateBinding.inflate(inflater,container,false)
        _weekDayBinding=CalendarDayLegendContainerBinding.bind(binding.weekDays.root)
        for (i in 0 until weekDayBinding.root.childCount) {
            val childView: View = weekDayBinding.root.getChildAt(i)
            if (childView is TextView && childView.getId() == R.id.legendText1) {
                childView.text = weekdays[i] // Set your desired text here
            }
        }

        binding.calendarView.dayBinder= object : MonthDayBinder<DayHolder> {
            override fun bind(container: DayHolder, calendarDay: CalendarDay) {
                container.day=calendarDay
                if (calendarDay.position == DayPosition.MonthDate) {
                    container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.medium_orange))
                } else if (calendarDay.position == DayPosition.InDate) {
                    container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                } else if (calendarDay.position == DayPosition.OutDate) {
                    container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.white))
                }
                container.dayBinding.calendarDayText.setBackgroundResource(R.color.white)
                container.dayBinding.calendarDayText.setText(calendarDay.date.dayOfMonth.toString() + "")
                if (calendarDay.position == DayPosition.MonthDate) {
                    container.dayBinding.calendarDayText.setVisibility(View.VISIBLE)
                    if (calendarDay.date.isBefore(today)) {
                        container.dayBinding.calendarDayText.setTextColor(resources.getColor(R.color.light_grey))
                    }
                    if (today == calendarDay.date) {
                        container.dayBinding.calendarDayText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.medium_orange
                            )
                        )
                        container.dayBinding.calendarDayText.setBackgroundResource(R.color.white)
                    }
                    if (selectedDate != null && selectedDate == calendarDay.date) {
                        container.dayBinding.calendarDayText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                        container.dayBinding.calendarDayText.setBackgroundResource(R.drawable.book_now_bg)
                    }
                }
            }

            override fun create(view: View): DayHolder {
                return DayHolder(DayBinding.bind(view), object : DateClick {
                    override fun dateClick(date: LocalDate?) {
                        selectDate(date)
                    }
                } )
            }
        }


        binding.calendarView.monthScrollListener = { calendarMonth: CalendarMonth ->
            binding.month.setText(getMonthYearString(calendarMonth))
            currentMonth = calendarMonth.yearMonth
        }
        currentMonth = YearMonth.now()
        val startMonth = currentMonth?.minusMonths(100) // Adjust as needed

        val endMonth = currentMonth?.plusMonths(100) // Adjust as needed

        binding.calendarView.setup(startMonth!!, endMonth!!, firstDayOfWeekFromLocale())
        binding.calendarView.scrollToMonth(currentMonth!!)
        binding.back.setOnClickListener(View.OnClickListener { findNavController().popBackStack() })
        binding.prevMonth.setOnClickListener{
            binding.calendarView.scrollToMonth(currentMonth!!.minusMonths(1))
        }
        binding.nextMonth.setOnClickListener{
            binding.calendarView.scrollToMonth(currentMonth!!.plusMonths(1))
        }
        binding.save.setOnClickListener{
            if (selectedDate != null) {
                setFragmentResult(DATE_RESULT, bundleOf(DATE to selectedDate.toString()))
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Please Select the Date", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun selectDate(date: LocalDate?) {
        date?.let {
            if (!date.isBefore(today) && selectedDate !== date) {
                val oldDate = selectedDate
                selectedDate = date
                if (oldDate != null) {
                    binding.calendarView.notifyDateChanged(oldDate)
                }
                binding.calendarView.notifyDateChanged(date)
            }
        }
    }
    private fun getMonthYearString(calendarMonth: CalendarMonth): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return calendarMonth.yearMonth.format(formatter)
    }
}