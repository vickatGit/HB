package com.example.habit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.data.models.Habit
import com.example.habit.databinding.FragmentAddHabitBinding
import com.example.habit.ui.viewmodel.AddHabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalField

@AndroidEntryPoint
class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!
    private val viewmodel:AddHabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding=FragmentAddHabitBinding.inflate(inflater,container,false)
        binding.timelineContainer.setOnClickListener { findNavController().navigate(R.id.action_addHabitFragment_to_dateFragment) }
        binding.addHabit.setOnClickListener {
            viewmodel.addHabit(
                Habit(1,"first habit","dexription",1, LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),true,Duration.ofMinutes(5),
                    LocalDateTime.now().plusSeconds(10)
                ),
                requireContext()
            )
        }

        return binding.root
    }

}