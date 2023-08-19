package com.example.habit.ui.fragment.GroupHabit

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.R
import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.local.entity.HabitGroupWithHabits
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.CalendarLayoutBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentGroupBinding
import com.example.habit.ui.adapter.HabitGroupUsersAdapter
import com.example.habit.ui.callback.DateClick
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.fragment.AddHabit.AddHabitUiState
import com.example.habit.ui.fragment.Date.DayHolder
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.viewmodel.AddHabitViewModel
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private val viewModel:AddHabitViewModel by viewModels()

    private var maxScored: Int=0
    private var habitEntries: HashMap<LocalDate, EntryEntity> = hashMapOf()
    private var groupId: String? =null
    private lateinit var groupHabit: HabitGroupWithHabits
    var weekdays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDates = mutableMapOf<LocalDate, LocalDate>()
    private var habitStartDate: LocalDate? = null
    private var habitEndDate: LocalDate? = null
    private var isCalendarEditable: Boolean = false

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!
    private var _calendarBinding: CalendarLayoutBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    private var _weekDaysBinding: CalendarDayLegendContainerBinding? = null
    private val weekDayBinding get() = _weekDaysBinding!!

    private lateinit var usersAdapter:HabitGroupUsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGroupBinding.inflate(inflater,container,false)
        _calendarBinding = CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)
        groupId = arguments?.getString("group_habit_id",null)
        Log.e("TAG", "onCreateView: groupId $groupId" )
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is AddHabitUiState.GroupHabit -> {
                            groupHabit = it.groupHabit
                            bindData()
                            bindUserHabitData(groupHabit.habits.get(0))
                        }
                        else -> {}
                    }
                }
            }
        }
        groupId?.let { viewModel.getGroupHabit(it) }

        return binding.root
    }

    private fun bindData() {
        binding.habitTitle.text=groupHabit.habitGroup.title
        setupRecyclerView()
    }
    private fun bindUserHabitData(habit: HabitEntity){
        Toast.makeText(requireContext(),"user selected",Toast.LENGTH_SHORT).show()
        habit.entryList?.let {
            habitEntries = it.toMutableMap() as HashMap<LocalDate, EntryEntity>
            habitEntries?.map {
                selectedDates.put(it.key, it.value.timestamp!!)
            }
        }
        bindStreakInfo(habit)
        initialiseCalendar(habit.startDate!!, habit.endDate!!,habit)
        initialiseConsistencyGraph(habit.entryList)
        binding.streakEditSwitch.setOnCheckedChangeListener { _, isChecked ->
            isCalendarEditable = isChecked
            bindDays(habit)
        }
    }
    private fun initialiseConsistencyGraph(mapEntries: Map<LocalDate, EntryEntity>?) {
        //values for single line chart on the graph
        val entries:MutableList<Entry> = mutableListOf()
        mapEntries?.mapValues {
            if(it.value.score!!>maxScored) maxScored=it.value.score!!
            Log.e("TAG", "initialiseConsistencyGraph: ${
                Gson().toJson(
                    Entry(it.value.timestamp!!.atStartOfDay(
                        ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat())
                )}", )
            entries.add(Entry(it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat()))
        }
        if(entries.size>3) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
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
            xtAxis.position= XAxis.XAxisPosition.BOTTOM
            xtAxis.labelRotationAngle=320f
            yrAxis.isEnabled=false

            ylAxis.setDrawAxisLine(false)
            xtAxis.setDrawGridLines(false)
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
    private fun initialiseCalendar(startDate: LocalDate, endDate: LocalDate,habit: HabitEntity) {
        habitStartDate = startDate
        habitEndDate = endDate
        bindWeekDays()
        bindDays(habit)
        calendarBinding.calendarView.monthScrollListener = { calendarMonth: CalendarMonth ->
            calendarBinding.month.text = getMonthYearString(calendarMonth)
            viewModel.setCurrentViewingMonth(calendarMonth.yearMonth)
        }


        val startMonth = YearMonth.of(startDate.year, startDate.month)
        val endMonth = YearMonth.of(endDate.year, endDate.month)

        calendarBinding.calendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())
        calendarBinding.calendarView.scrollToMonth(viewModel.getCurrentViewingMonth()?:startMonth)

    }
    private fun bindDays(habit: HabitEntity) {
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
                        date?.let {
                            if (!date.isBefore(habitStartDate) && !date.isAfter(habitEndDate)) {
                                selectDate(date,habit)
                            }
                        }
                    }
                })
            }
        }
    }
    private fun selectDate(date: LocalDate?,habit: HabitEntity) {
        date?.let {
            if (habitEntries.containsKey(it)) {
                habitEntries[it]!!.completed = !habitEntries[it]!!.completed
                Log.e("TAG", "updateEntry selectDate: present $date ")
                updateEntries(it, habitEntries[it]!!.completed,habit)
            } else {
                habitEntries[it] = EntryEntity(it!!, 0,true)
                Log.e("TAG", "updateEntry selectDate: not present $date ")
                updateEntries(it, true,habit)
            }
            calendarBinding.calendarView.notifyDateChanged(it)
        }
    }
    private fun updateEntries(date: LocalDate, isUpgrade: Boolean,habit: HabitEntity) {
        val prevEntry = habitEntries[date.minusDays(1)]

        if (prevEntry == null) {
            habitEntries[date] = EntryEntity(date, if (isUpgrade) 1 else 0, isUpgrade)
        }
        val habitList= mutableListOf<EntryEntity>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }

        habitList.forEachIndexed {index,it ->
            if (index>0 && (it.timestamp!!.isAfter(date) || it.timestamp.isEqual(date))) {
                var score=habitList.get(if(index!=0) index-1 else index).score
                habitList[index].score=if (isUpgrade) score!!+1 else it.score!!-1
            }
        }
        habitEntries.putAll(habitList.associateBy { it.timestamp!! })
        habit.serverId?.let { viewModel.updateHabitEntries(it, habit.id, habitEntries) }
    }
    private fun bindStreakInfo(habit:HabitEntity) {
        var daysMissed=0
        var daysCompleted=0
        var highestStreak=0
        var currentStreak=0

        val habitList= mutableListOf<EntryEntity>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }



        habitEntries.toSortedMap().mapValues {
//            if(it.key.isBefore(LocalDate.now()) && it.key.isEqual(LocalDate.now())){
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
//            }
        }
        binding.currentStreak.text=currentStreak.toString()
        binding.daysMissed.text=daysMissed.toString()
        binding.highestStreak.text=highestStreak.toString()
        binding.daysCompleted.text="$daysCompleted/${ChronoUnit.DAYS.between(habit.startDate, habit.endDate!!.plusDays(1))}"
    }
    private fun bindHabitPageData(habit: HabitView) {
        binding.habitTitle.text=groupHabit.habitGroup.title
        binding.progress.isVisible = false


    }

    private fun setupRecyclerView() {
        binding.userHabitsPercentage.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        usersAdapter = HabitGroupUsersAdapter(groupHabit.habits, object : HabitClick {
            override fun habitClick(habitId: String) {
                bindUserHabitData(groupHabit.habits.get(habitId.toInt()))
            }

        })
        binding.userHabitsPercentage.adapter = usersAdapter

    }
    private fun getMonthYearString(calendarMonth: CalendarMonth): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return calendarMonth.yearMonth.format(formatter)
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




}