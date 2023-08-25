package com.example.habit.ui.fragment.GroupHabit

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.R
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.databinding.CalendarDayLegendContainerBinding
import com.example.habit.databinding.CalendarLayoutBinding
import com.example.habit.databinding.DayBinding
import com.example.habit.databinding.FragmentGroupBinding
import com.example.habit.ui.activity.AddMembersActivity.AddMembersActivity
import com.example.habit.ui.adapter.HabitGroupUsersAdapter
import com.example.habit.ui.callback.DateClick
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.fragment.Date.DayHolder
import com.example.habit.ui.model.EntryView
import com.example.habit.ui.model.GroupHabitWithHabitsView
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.model.UtilModels.UserGroupThumbProgressModel
import com.example.habit.ui.viewmodel.GroupHabitViewModel
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
import javax.inject.Inject

@AndroidEntryPoint
class GroupFragment : Fragment() {
    companion object {
        val GROUP_HABIT_ID: String = "group_habit_id"
    }

    private lateinit var users: MutableList<UserGroupThumbProgressModel>

    @Inject
    lateinit var authPref: AuthPref
    private val viewModel: GroupHabitViewModel by viewModels()

    private var maxScored: Int = 0
    private var habitEntries: HashMap<LocalDate, EntryView> = hashMapOf()
    private var groupId: String? = null
    private lateinit var groupHabit: GroupHabitWithHabitsView
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


    private lateinit var usersAdapter: HabitGroupUsersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        _calendarBinding = CalendarLayoutBinding.bind(binding.calendar.root)
        _weekDaysBinding = CalendarDayLegendContainerBinding.bind(calendarBinding.weekDays.root)
        groupId = arguments?.getString(GROUP_HABIT_ID, null)
        Log.e("TAG", "onCreateView: groupId $groupId")
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is GroupHabitUiState.GroupHabit -> {
                            groupHabit = it.groupHabit
                            Log.e("TAG", "onCreateView: $groupHabit", )
                            bindData()
                            hideProgress()
                        }
                        is GroupHabitUiState.Error -> {
                            Toast.makeText(requireContext(),it.error,Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        is GroupHabitUiState.Success -> {
                            Toast.makeText(requireContext(),it.msg,Toast.LENGTH_SHORT).show()
                            hideProgress()
                            requireActivity().onBackPressed()
                        }
                        GroupHabitUiState.Loading -> {
                            showProgress()
                        }
                        GroupHabitUiState.Nothing -> {hideProgress()}
                        else -> {}
                    }
                }
            }
        }
        groupId?.let { viewModel.getGroupHabit(it) }

        binding.edit.setOnClickListener {
            val args = Bundle().apply {
                putBoolean("isFromGroupHabitUpdate", true)
                putParcelable("groupHabit", groupHabit.habitGroup)
            }
            findNavController().navigate(
                R.id.action_groupFragment_to_addHabitFragment,
                args
            )
        }

        binding.delete.setOnClickListener {
            viewModel.deleteGroupHabit(groupHabit.habitGroup.id,groupHabit.habitGroup.serverId)
        }
        binding.leaveHabitGroupCard.setOnClickListener {
            viewModel.removeMembersFromGroupHabit(groupHabit.habitGroup.serverId,groupHabit.habitGroup.id,
                listOf(users.get(viewModel.getHabitStatePos()).member.userId!!)
            )
        }
        binding.addMembers.setOnClickListener {
            val intent = Intent(requireContext(),AddMembersActivity::class.java)
            intent.putExtra(AddMembersActivity.HABIT_GROUP_ID,groupHabit.habitGroup.id)
            startActivity(intent)
        }

        return binding.root
    }

    private fun bindData() {
        binding.habitTitle.text = groupHabit.habitGroup.title
        val isAdmin = authPref.getUserId().equals(groupHabit.habitGroup.admin)
        binding.edit.isVisible = isAdmin
        binding.delete.isVisible = isAdmin
        binding.addMembers.isVisible = isAdmin
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        users = mutableListOf()
        var members = groupHabit.habitGroup.members?: emptyList()
        Log.e("TAG", "setupRecyclerView: members $members", )


        //don't know the reason of error that's why added Try-Catch
        try {
            members.forEach {
                users.add(
                    UserGroupThumbProgressModel(
                        groupHabit?.habits?.find { habit ->
                            habit.userId.equals(it.userId)
                        }!!,
                        it
                    )
                )
            }
            val userIndex = users.indexOfFirst { it.member.userId==authPref.getUserId() }
            val user = users.get(userIndex)
            user.member.username="Me"
            users.removeAt(userIndex)
            users.add(0,user)


        }catch (e:Exception){
            Log.e("TAG", "setupRecyclerView: ${e}", )
        }

        binding.userHabitsPercentage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        usersAdapter = HabitGroupUsersAdapter(users,
            object : HabitClick {
                override fun habitClick(habitId: String) {
                    bindUserHabitData(users.get(habitId.toInt()).habit)
                    viewModel.setHabitStatePos(habitId.toInt())
                }

            })
        binding.userHabitsPercentage.adapter = usersAdapter
        bindUserHabitData(users.get(viewModel.getHabitStatePos()).habit)

    }

    private fun bindUserHabitData(habit: HabitView) {
//        Log.e("TAG", "bindUserHabitData: userId ${authPref.getUserId()} habit userId ${habit.userId}", )
//        Toast.makeText(requireContext(), "user selected ${authPref.getUserId().equals(habit.userId)}", Toast.LENGTH_SHORT).show()
        binding.streakEditSwitch.isVisible= authPref.getUserId() == habit.userId
        binding.leaveHabitGroupCard.isVisible= authPref.getUserId() == habit.userId



        habit.entries?.let {
            habitEntries = it
            habitEntries?.map {
                selectedDates.put(it.key, it.value.timestamp!!)
            }
        }

        bindStreakInfo(habit)
        initialiseCalendar(habit.startDate!!, habit.endDate!!, habit)
        initialiseConsistencyGraph(habit.entries)
        binding.streakEditSwitch.setOnCheckedChangeListener { _, isChecked ->
            isCalendarEditable = isChecked
            bindDays(habit)
        }
    }

    private fun initialiseConsistencyGraph(mapEntries: HashMap<LocalDate, EntryView>?) {
        //values for single line chart on the graph
        val entries: MutableList<Entry> = mutableListOf()
        mapEntries?.mapValues {
            if (it.value.score!! > maxScored) maxScored = it.value.score!!
            Log.e(
                "TAG",
                "initialiseConsistencyGraph: ${
                    Gson().toJson(
                        Entry(
                            it.value.timestamp!!.atStartOfDay(
                                ZoneOffset.UTC
                            ).toInstant().toEpochMilli().toFloat(), it.value.score!!.toFloat()
                        )
                    )
                }",
            )
            entries.add(
                Entry(
                    it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                        .toFloat(), it.value.score!!.toFloat()
                )
            )
        }
        if (entries.size > 3) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
            val startColor = resources.getColor(R.color.orange_op_20)
            val midColor = resources.getColor(R.color.orange_op_20)
            val endColor = resources.getColor(R.color.transparent)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startColor, midColor, endColor)
            )

            dataset.setDrawFilled(true)
            dataset.fillDrawable = gradientDrawable
            dataset.color = binding.root.resources.getColor(R.color.medium_orange)
            dataset.lineWidth = 3f
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.setDrawValues(false)
            dataset.mode = LineDataSet.Mode.CUBIC_BEZIER


            val xtAxis = binding.consistency.xAxis
            val ylAxis = binding.consistency.axisLeft
            val yrAxis = binding.consistency.axisRight

//            ylAxis.setLabelCount(3,true)
            xtAxis.setLabelCount(7, true)
            xtAxis.position = XAxis.XAxisPosition.BOTTOM
            xtAxis.labelRotationAngle = 320f
            yrAxis.isEnabled = false

            ylAxis.setDrawAxisLine(false)
            xtAxis.setDrawGridLines(false)
            ylAxis.gridColor = resources.getColor(R.color.consistency_graph_grid_color)
            ylAxis.gridLineWidth = 1.4f

            xtAxis.valueFormatter = XAxisFormatter()
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
        } else {
            binding.consistency.isVisible = false
            binding.graphHeader.isVisible = false
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

    private fun initialiseCalendar(startDate: LocalDate, endDate: LocalDate, habit: HabitView) {
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
        calendarBinding.calendarView.scrollToMonth(viewModel.getCurrentViewingMonth() ?: startMonth)

    }

    private fun bindDays(habit: HabitView) {
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
                val params =
                    container.dayBinding.calendarDayText.layoutParams as ViewGroup.MarginLayoutParams
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
                                selectDate(date, habit)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun selectDate(date: LocalDate?, habit: HabitView) {
        date?.let {
            if (habitEntries.containsKey(it)) {
                habitEntries[it]!!.completed = !habitEntries[it]!!.completed
                Log.e("TAG", "updateEntry selectDate: present $date ")
                updateEntries(it, habitEntries[it]!!.completed, habit)
            } else {
                habitEntries[it] = EntryView(it!!, 0, true)
                Log.e("TAG", "updateEntry selectDate: not present $date ")
                updateEntries(it, true, habit)
            }
            calendarBinding.calendarView.notifyDateChanged(it)
        }
    }

    private fun updateEntries(date: LocalDate, isUpgrade: Boolean, habit: HabitView) {
        val prevEntry = habitEntries[date.minusDays(1)]

        if (prevEntry == null) {
            habitEntries[date] = EntryView(date, if (isUpgrade) 1 else 0, isUpgrade)
        }
        val habitList = mutableListOf<EntryView>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }

        habitList.forEachIndexed { index, it ->
            if (index > 0 && (it.timestamp!!.isAfter(date) || it.timestamp.isEqual(date))) {
                var score = habitList.get(if (index != 0) index - 1 else index).score
                habitList[index].score = if (isUpgrade) score!! + 1 else it.score!! - 1
            }
        }
        habitEntries.putAll(habitList.associateBy { it.timestamp!! })
        habit.serverId?.let { viewModel.updateHabitEntries(it, habit.id, habitEntries) }
    }

    private fun bindStreakInfo(habit: HabitView) {
        var daysMissed = 0
        var daysCompleted = 0
        var highestStreak = 0
        var currentStreak = 0

        val habitList = mutableListOf<EntryView>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }



        habitEntries.toSortedMap().mapValues {
//            if(it.key.isBefore(LocalDate.now()) && it.key.isEqual(LocalDate.now())){
            Log.e("TAG", "bindStreakInfo: ${it.value.completed}")
            if (it.value.completed) {
                ++daysCompleted
                ++currentStreak
                if (highestStreak < currentStreak) highestStreak = currentStreak else {
                }
            } else {
                if (highestStreak < currentStreak) highestStreak = currentStreak
                currentStreak = 0
                ++daysMissed
            }
//            }
        }
        binding.currentStreak.text = currentStreak.toString()
        binding.daysMissed.text = daysMissed.toString()
        binding.highestStreak.text = highestStreak.toString()
        binding.daysCompleted.text = "$daysCompleted/${
            ChronoUnit.DAYS.between(
                habit.startDate,
                habit.endDate!!.plusDays(1)
            )
        }"
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

    private fun showProgress(){ binding.progress.isVisible=true }
    private fun hideProgress(){ binding.progress.isVisible=false }


}