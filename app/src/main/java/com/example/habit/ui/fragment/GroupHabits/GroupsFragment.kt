package com.example.habit.ui.fragment.GroupHabits

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.R
import com.example.habit.databinding.FragmentGroupsBinding
import com.example.habit.ui.adapter.GroupHabitsAdapter
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.fragment.AddHabit.AddHabitUiState
import com.example.habit.ui.fragment.CompletedHabitFragment.CompletedHabitFragment
import com.example.habit.ui.viewmodel.AddHabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupsFragment : Fragment() {

    val viewModel:AddHabitViewModel by viewModels()
    lateinit var groAd:GroupHabitsAdapter

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGroupsBinding.inflate(inflater,container,false)
        binding.ongoingHabits.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is AddHabitUiState.Habits -> {
                            groAd= GroupHabitsAdapter(it.habits, object : HabitClick {
                                override fun habitClick(habitId: String) {
                                    findNavController().navigate(
                                        R.id.action_groupsFragment_to_groupFragment,
                                        Bundle().apply { putString("group_habit_id", habitId) }
                                    )
                                } })
                            binding.ongoingHabits.adapter=groAd
                        }
                        else -> {

                        }
                    }

                }
            }
        }
        viewModel.getGroupHabits()

        return binding.root
    }


}