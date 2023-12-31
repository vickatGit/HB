package com.habitude.habit.ui.fragment.AddHabit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.habitude.habit.R
import com.habitude.habit.databinding.FragmentAddHabitBinding
import com.habitude.habit.ui.fragment.Date.DateFragment
import com.habitude.habit.ui.fragment.time.TimerFragment
import com.habitude.habit.ui.model.GroupHabitView
import com.habitude.habit.ui.model.GroupHabitWithHabitsView
import com.habitude.habit.ui.model.HabitView
import com.habitude.habit.ui.viewmodel.AddHabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddHabitFragment : Fragment() {

    private lateinit var habitTitle: String
    private var isGroupUpdate: Boolean=false
    private var isFromHome: Boolean=false
    private var selectedHabitType: Int = 0
    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private val viewModel:AddHabitViewModel by viewModels()
    private var isStart=false
    private var habit= HabitView()
    private var groupHabit= GroupHabitView()
    private var isUpdate:Boolean=false
    private val habitTypes = listOf<String>("Personal","Group")
    private lateinit var habitTypeAdapter:ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUpdate = arguments?.getBoolean("isUpdate")?:false
        isGroupUpdate = arguments?.getBoolean("isFromGroupHabitUpdate",false)?:false
        isFromHome = arguments?.getBoolean("isFromHome",false)?:false
        if(isUpdate) habit=arguments?.getParcelable("habit")!!
        if(isGroupUpdate) groupHabit=arguments?.getParcelable("groupHabit")!!
        if(isFromHome) habitTitle=arguments?.getString("habitTitle")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding=FragmentAddHabitBinding.inflate(inflater,container,false)
        if(isUpdate || isGroupUpdate) {
            binding.addHabit.text="Update Habit"
            binding.habitTypeCont.isVisible=false
        }
        if(isUpdate){
            bindHabitData()
        }else{
            if(isGroupUpdate){
                bindGroupHabitHabitData()
                binding.habitTypeCont.isVisible=false
            }else {
                habit.id = UUID.randomUUID().toString()
            }
        }
        if(isFromHome){
            binding.title.setText(habitTitle)
        }

        habitTypeAdapter=
            ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,habitTypes)
        binding.habitType.setAdapter(habitTypeAdapter)
        binding.habitType.setOnItemClickListener { _, _, position, _ ->
            selectedHabitType = position
        }
       lifecycleScope.launch {
           viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
               viewModel.uiState.collectLatest {
                   when(it){
                       is AddHabitUiState.Success ->{
                           showToast(it.msg)
                           binding.progress.isVisible=false
                           findNavController().popBackStack()
                       }
                       is AddHabitUiState.Error -> {
                           showToast(it.error)
                           binding.progress.isVisible=false
                       }
                       is AddHabitUiState.Loading -> {
                           binding.progress.isVisible=true
                       }

                       is AddHabitUiState.Habits -> {}
                       else -> {}
                   }
               }
           }
       }

        binding.startDate.setOnClickListener {
            if(!isUpdate) {
                isStart = true
                habit.startDate = null
                findNavController().navigate(R.id.action_addHabitFragment_to_dateFragment)
                binding.startDate.setText("")
            }
        }
        binding.endDate.setOnClickListener {
            if(!isUpdate) {
                isStart = false
                habit.endDate = null
                findNavController().navigate(R.id.action_addHabitFragment_to_dateFragment)
                binding.endDate.setText("")
            }
        }
        binding.addHabit.setOnClickListener {
            val title = binding.title.text.trim().toString()
            val reminderQuestion = binding.reminderQuestion.text.trim().toString()
            val description = binding.description.text.trim().toString()

            when {
                title.isBlank() -> {
                    showToast("Title is empty")
                    return@setOnClickListener
                }
                habit.startDate == null -> {
                    showToast("Start Date is not specified")
                    return@setOnClickListener
                }
                habit.endDate == null -> {
                    showToast("End Date is not specified")
                    return@setOnClickListener
                }
                binding.reminderSwitch.isChecked && reminderQuestion.isBlank() -> {
                    showToast("Reminder Question is not specified")
                    habit.reminderQuestion = reminderQuestion
                    return@setOnClickListener
                }
                binding.reminderSwitch.isChecked && habit.reminderTime == null -> {
                    showToast("Reminder Time is not specified")
                    return@setOnClickListener
                }
                else -> {
                    habit.title = title
                    habit.description = description
                    habit.reminderQuestion=reminderQuestion
                    if(isUpdate || isGroupUpdate)
                        if(isUpdate) {
                            viewModel.updateHabit(habit, requireContext())
                        }else{
                            viewModel.updateGroupHabit(
                                GroupHabitView(
                                    groupHabit.id,
                                    groupHabit.serverId,
                                    habit.title,
                                    habit.description,
                                    habit.reminderQuestion,
                                    habit.startDate,
                                    habit.endDate,
                                    habit.isReminderOn,
                                    habit.reminderTime
                                )
                            )
                        }
                    else {
                        if(selectedHabitType==0)
                            viewModel.addHabit(habit, requireContext())
                        else
                            viewModel.addGroupHabit(habit)
                    }
                }
            }
        }
        binding.reminder.setOnClickListener {
            binding.reminder.text=""
            habit.reminderTime=null
            findNavController().navigate(R.id.action_addHabitFragment_to_timerFragment)
        }
        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            habit.isReminderOn=isChecked
        }
        setFragmentResultListener(DateFragment.DATE_RESULT) { _, bundle ->
            val date  = LocalDate.parse(bundle.getString(DateFragment.DATE))
            if (isStart) {
                habit.startDate = date
                binding.startDate.setText(dateFormatter(date))
            }
            else {
                habit.endDate = date
                binding.endDate.setText(dateFormatter(date))
            }
        }
        setFragmentResultListener(TimerFragment.TIME_RESULT){_,bundle ->
            val time =Calendar.getInstance()
            time.timeInMillis=bundle.getLong(TimerFragment.TIME)
            val instant = Instant.ofEpochMilli(time.timeInMillis)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            time?.let {
                habit.reminderTime= localDateTime
            }
            binding.reminder.text = "Daily at ${timeFormatter(time)}"
        }

        binding.reminderSwitch.isChecked=true
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun dateFormatter(date: LocalDate):String{
        val uiDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return date.format(uiDateFormat)
    }
    private fun timeFormatter(time: Calendar):String{
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time.time)
    }
    private fun timeFormatter(time: LocalDateTime):String{
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return time.format(formatter)
    }

    private fun bindHabitData() {
        binding.title.setText(habit.title)
        binding.reminder.text = "Daily at ${timeFormatter(habit.reminderTime!!)}"
        binding.startDate.setText(dateFormatter(habit.startDate!!))
        binding.endDate.setText(dateFormatter(habit.endDate!!))
        binding.reminderQuestion.setText(habit.reminderQuestion)
        binding.description.setText(habit.description)
        binding.reminderSwitch.isChecked=habit.isReminderOn!!
    }

    private fun bindGroupHabitHabitData() {
        binding.title.setText(groupHabit.title)
        binding.reminder.text = "Daily at ${timeFormatter(groupHabit.reminderTime!!)}"
        habit.reminderTime=groupHabit.reminderTime
        binding.startDate.setText(dateFormatter(groupHabit.startDate!!))
        habit.startDate=groupHabit.startDate
        binding.endDate.setText(dateFormatter(groupHabit.endDate!!))
        habit.endDate=groupHabit.endDate
        binding.reminderQuestion.setText(groupHabit.reminderQuestion)
        binding.description.setText(groupHabit.description)
        binding.reminderSwitch.isChecked=groupHabit.isReminderOn!!
        habit.isReminderOn=groupHabit.isReminderOn
    }

    private fun showToast(message: String) {
        if(message.isNotBlank())
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}