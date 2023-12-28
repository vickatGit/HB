package com.habitude.habit.ui.fragment.CompletedHabitFragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.habitude.habit.R
import com.habitude.habit.databinding.CalendarDayLegendContainerBinding
import com.habitude.habit.databinding.CalendarLayoutBinding
import com.habitude.habit.databinding.DayBinding
import com.habitude.habit.databinding.FragmentCompletedHabitBinding
import com.habitude.habit.ui.callback.DateClick
import com.habitude.habit.ui.fragment.Date.DayHolder
import com.habitude.habit.ui.fragment.Habit.HabitUiState
import com.habitude.habit.ui.mapper.HabitMapper.HabitMapper
import com.habitude.habit.ui.model.EntryView
import com.habitude.habit.ui.model.HabitView
import com.habitude.habit.ui.viewmodel.HabitViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class CompletedHabitFragment : Fragment() {


    companion object {
        const val HABIT_ID: String = "completed_habit_id_key"
    }

    private var maxScored: Int=0
    private lateinit var habit: HabitView
    private var totalHabitDuration: Long? = null
    private val viewModel: HabitViewModel by viewModels()
    private var habitEntries: HashMap<LocalDate, EntryView>   = hashMapOf()


    private var _binding: FragmentCompletedHabitBinding? = null
    private val binding get() = _binding!!

    private var _calendarBinding: CalendarLayoutBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    private var _weekDaysBinding: CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDaysBinding!!



    @Inject
    lateinit var habitMapper: HabitMapper

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
        _binding=FragmentCompletedHabitBinding.inflate(inflater,container,false)
        _calendarBinding = CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.habitUiState.collectLatest {
                    when (it) {
                        is HabitUiState.HabitData -> {
                            habit = it.habit
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
                            binding.progress.isVisible = false
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
        binding.delete.setOnClickListener {
            habit?.let {
                viewModel.deleteHabit(habitServerId = habit.serverId, habitId = it.serverId,getString(R.string.habit_deletion_success_msg),getString(
                    R.string.habit_deletion_failed_msg))
            }
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        habitId?.let { viewModel.getHabit(it, getString(R.string.habit_not_found_error)) }
        binding.calendarToggle.setOnCheckedChangeListener { _, isChecked ->
            binding.calendar.root.isVisible=isChecked
        }
        binding.graphToggle.setOnCheckedChangeListener { _, isChecked ->
            binding.consistency.isVisible=isChecked
        }
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
        bindStreakInfo()
        initialiseCalendar(habit.startDate!!, habit.endDate!!)
        initialiseConsistencyGraph(habit.entries)


    }

    private fun bindStreakInfo() {
        var daysMissed=0
        var daysCompleted=0
        var highestStreak=0
        var currentStreak=0

        val habitList= mutableListOf<EntryView>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }
        habitEntries.toSortedMap().mapValues {
            Log.e("TAG", "bindStreakInfo: ${it.value.completed}", )
            if(it.value.completed){
                ++daysCompleted
                ++currentStreak
                if( highestStreak < currentStreak ) highestStreak = currentStreak else {}
            }else{
                if( highestStreak < currentStreak ) highestStreak = currentStreak
                currentStreak=0
                ++daysMissed
            }
        }
        binding.totalDays.text="${ChronoUnit.DAYS.between(habit.startDate, habit.endDate!!.plusDays(1))}"
        binding.daysMissed.text=daysMissed.toString()
        binding.highestStreak.text=highestStreak.toString()
        initialiseProgress(daysCompleted)
    }

    private fun initialiseProgress(daysCompleted: Int) {
        totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)
        val progress = (totalHabitDuration!! / 100f) * daysCompleted!!
        binding.habitProgress.progress = progress.roundToInt()
        "${DecimalFormat("#.#").format(progress)}%".also { binding.progressPercentage.text = it }
        "${DecimalFormat("#.#").format(progress)}% ${resources.getString(R.string.habit_post_completion_greet)}".also { binding.completionGreet.text = it }
    }

    private fun initialiseCalendar(startDate: LocalDate, endDate: LocalDate) {
        habitStartDate = startDate
        habitEndDate = endDate
        bindWeekDays()
        bindDays()
        calendarBinding.calendarView.monthScrollListener = { calendarMonth: CalendarMonth ->
            calendarBinding.month.text = getMonthYearString(calendarMonth)
            viewModel.setCurrentViewingMonth(calendarMonth.yearMonth)
        }


        val startMonth = YearMonth.of(startDate.year, startDate.month)
        val endMonth = YearMonth.of(endDate.year, endDate.month)

        calendarBinding.calendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())
        calendarBinding.calendarView.scrollToMonth(viewModel.getCurrentViewingMonth()?:startMonth)

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
                val params = container.dayBinding.calendarDayText.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = 10
                params.bottomMargin = 10
                params.topMargin = 10
                params.rightMargin = 10

            }

            override fun create(view: View): DayHolder {
                return DayHolder(DayBinding.bind(view), object : DateClick {
                    override fun dateClick(date: LocalDate?) {
                    }
                })
            }
        }
    }




    private fun initialiseConsistencyGraph(mapEntries: HashMap<LocalDate, EntryView>?) {
        //values for single line chart on the graph
        val entries:MutableList<Entry> = mutableListOf()
        mapEntries?.forEach {
            if(it.key.isBefore(LocalDate.now()) || it.key.isEqual(LocalDate.now()))
                entries.add(Entry(it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat()))
        }
        entries.sortBy { it.x }
//        mapEntries?.mapValues {
////            if(it.value.score!!>maxScored) maxScored=it.value.score!!
//            Log.e("TAG", "initialiseConsistencyGraph: ${Gson().toJson(Entry(it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat()))}", )
//            entries.add(Entry(it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat()))
//        }
//        if(LocalDate.now().compareTo(habit.startDate)>3) {
//        Log.e("TAG", "initialiseConsistencyGraph: start date ${habit.startDate} \n todays date : ${LocalDate.now()} \n diff " +
//                "${(LocalDate.now().diff(habit.startDate!!))} ", )
        if((habit.startDate!!.diff(LocalDate.now()))>3) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
            Log.e("TAG", "initialiseConsistencyGraph: date set ${dataset}", )
            val startColor =resources.getColor(R.color.orange_op_20)
            val midColor = resources.getColor(R.color.orange_op_20)
            val endColor = resources.getColor(R.color.transparent)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startColor,midColor, endColor)
            )

            dataset.setDrawFilled(true)
            dataset.fillDrawable = gradientDrawable
            dataset.color=binding.root.resources.getColor(R.color.medium_orange)
            dataset.lineWidth=3f
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.setDrawValues(false)
            dataset.mode= LineDataSet.Mode.CUBIC_BEZIER


            val xtAxis=binding.consistency.xAxis
            val ylAxis=binding.consistency.axisLeft
            val yrAxis=binding.consistency.axisRight

//            ylAxis.setLabelCount(3,true)
            xtAxis.setLabelCount(7,true)
            xtAxis.position=XAxis.XAxisPosition.BOTTOM
            xtAxis.textColor = resources.getColor(R.color.text_color)
            xtAxis.labelRotationAngle=320f
            yrAxis.isEnabled=false

            ylAxis.setDrawAxisLine(false)
            xtAxis.setDrawGridLines(false)
            ylAxis.textColor = resources.getColor(R.color.text_color)
            ylAxis.gridColor=resources.getColor(R.color.consistency_graph_grid_color)
            ylAxis.gridLineWidth=1.4f

            xtAxis.valueFormatter=XAxisFormatter()
//            ylAxis.valueFormatter=YAxisFormatter()



            //LineData object is Needed by Graph and to create LineData() object we Need to Pass list ILineDataSet objects
            // since it has capability to show multiple Line chart on single graph whereas LineDataSet Object Represents one chart in a Graph
            val datasets = mutableListOf<ILineDataSet>(dataset)
            val chartLineData = LineData(datasets)

            binding.consistency.description.isEnabled = false
            binding.consistency.legend.isEnabled = false
            binding.consistency.setTouchEnabled(true)
            binding.consistency.isHighlightPerTapEnabled = false
            binding.consistency.isHighlightPerDragEnabled = false
            binding.consistency.isDragEnabled = true
            binding.consistency.setScaleEnabled(true)
            // if disabled, scaling can be done on x- and y-axis separately
            binding.consistency.setPinchZoom(false)
            binding.consistency.animateX(1000)

            binding.consistency.data = chartLineData
            Log.e("TAG", "initialiseConsistencyGraph: chart ${Gson().toJson(entries)}", )
            binding.consistency.invalidate()
        }else{
            binding.consistency.isVisible=false
            binding.graphHeader.isVisible=false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
        _calendarBinding=null
        _weekDaysBinding=null
    }

    inner class XAxisFormatter : IAxisValueFormatter {
        private val dateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            val date = Date(value.toLong())
            return dateFormatter.format(date)
        }

        override fun getDecimalDigits(): Int {
            return -1
        }

    }

    fun LocalDate.diff(other: LocalDate): Long {
        return ChronoUnit.DAYS.between(this, other)
    }


}