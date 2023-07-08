package com.example.habit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.data.models.Habit
import com.example.habit.databinding.FragmentAddHabitBinding
import com.example.habit.ui.fragment.Date.DateFragment
import com.example.habit.ui.fragment.Date.DateFragment.*
import com.example.habit.ui.fragment.time.TimerFragment
import com.example.habit.ui.fragment.time.TimerFragment.*
import com.example.habit.ui.viewmodel.AddHabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private val viewmodel:AddHabitViewModel by viewModels()
    private var isStart=false
    private var habit= Habit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding=FragmentAddHabitBinding.inflate(inflater,container,false)
        binding.startDate.setOnClickListener {
            isStart=true
            habit.startDate=null
            findNavController().navigate(R.id.action_addHabitFragment_to_dateFragment)
            binding.startDate.setText("")
        }
        binding.endDate.setOnClickListener {
            isStart=false
            habit.endDate=null
            findNavController().navigate(R.id.action_addHabitFragment_to_dateFragment)
            binding.endDate.setText("")
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
//                    viewmodel.addHabit(habit, requireContext())
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
            val uiDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
            if (isStart) {
                habit.startDate = date
                binding.startDate.setText(date.format(uiDateFormat))
            }
            else {
                habit.endDate = date
                binding.endDate.setText(date.format(uiDateFormat))
            }
        }
        setFragmentResultListener(TimerFragment.TIME_RESULT){_,bundle ->
            val time =Calendar.getInstance()
            time.timeInMillis=bundle.getLong(TimerFragment.TIME)
            time?.let {
                habit.reminderTime= LocalDateTime.of(
                    time.get(Calendar.YEAR),
                    time.get(Calendar.MONTH),
                    time.get(Calendar.DAY_OF_MONTH),
                    time.get(Calendar.HOUR_OF_DAY),
                    time.get(Calendar.MONTH),
                )
            }
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time.time)
            binding.reminder.text = "Daily at $formattedTime"
        }

        return binding.root
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}