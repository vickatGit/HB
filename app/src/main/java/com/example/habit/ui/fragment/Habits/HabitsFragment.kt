package com.example.habit.ui.fragment.Habits

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.habit.R
import com.example.habit.databinding.FragmentHabitsBinding
import com.example.habit.ui.adapter.HabitsAdapter
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.fragment.CompletedHabitFragment.CompletedHabitFragment
import com.example.habit.ui.fragment.Habit.HabitFragment
import com.example.habit.ui.model.HabitThumbView
import com.example.habit.ui.viewmodel.HabitsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HabitsFragment : Fragment() {

    companion object{
        const val IS_COMPLETED_HABITS="isCompleted"
    }

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HabitsViewModel by viewModels()
    private var habits= mutableListOf<HabitThumbView>()
    private lateinit var habitsAdapter: HabitsAdapter

    private var isCompleted=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCompleted=arguments?.getBoolean(IS_COMPLETED_HABITS,false)!!
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentHabitsBinding.inflate(inflater,container,false)
        habitsAdapter= HabitsAdapter(habits, object : HabitClick {
            override fun habitClick(habitId: String) {
                if(isCompleted){
                    findNavController().navigate(
                        R.id.action_habitsFragment_to_completedHabitFragment,
                        Bundle().apply { putString(CompletedHabitFragment.HABIT_ID, habitId) }
                    )
                }else {
                    findNavController().navigate(
                        R.id.action_habitsFragment_to_habitFragment,
                        Bundle().apply { putString(HabitFragment.HABIT_ID, habitId) }
                    )
                }
            }
        })
        binding.habits.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
//        binding.habits.layoutManager=GridLayoutManager(requireContext(),1)
        binding.habits.adapter=habitsAdapter
       lifecycleScope.launch {
           viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED){
               viewModel.habitsUiState.collectLatest {
                   when(it){
                       is HabitsUiState.HabitsFetched -> {
                           binding.progress.isVisible=false
                           habits.clear()
                           habits.addAll(it.habits)
                           habitsAdapter.notifyDataSetChanged()
                       }
                       is HabitsUiState.Error -> {
                           binding.progress.isVisible=false
                           it.error?.let { error -> showToast(error) }
                       }
                       is HabitsUiState.Loading -> {
                           binding.progress.isVisible=true
                       }
                       is HabitsUiState.Nothing -> { }
                   }
               }
           }
       }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        if(isCompleted) viewModel.getCompletedHabits() else viewModel.getHabits()

        return binding.root
    }

    private fun showToast(message: String) {
        if(message.isNotBlank())
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}