package com.habitude.habit.ui.fragment.GroupHabits

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.FragmentGroupsBinding
import com.habitude.habit.ui.adapter.GroupHabitsAdapter
import com.habitude.habit.ui.callback.HabitClick
import com.habitude.habit.ui.fragment.GroupHabit.GroupFragment
import com.habitude.habit.ui.fragment.GroupHabit.GroupHabitUiState
import com.habitude.habit.ui.viewmodel.GroupHabitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class GroupsFragment : Fragment() {

    @Inject
    lateinit var authPref: AuthPref

    val viewModel: GroupHabitViewModel by viewModels()
    lateinit var groupHabitsAdapter: GroupHabitsAdapter

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
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.ongoingHabits.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is GroupHabitUiState.Habits -> {
                            groupHabitsAdapter = GroupHabitsAdapter(authPref.getUserId(),it.habits, object : HabitClick {
                                override fun habitClick(habitId: String) {
                                    findNavController().navigate(
                                        R.id.action_groupsFragment_to_groupFragment,
                                        Bundle().apply { putString(GroupFragment.GROUP_HABIT_ID, habitId) }
                                    )
                                }
                            })
                            binding.ongoingHabits.adapter = groupHabitsAdapter
                            hideProgress()
                        }
                        is GroupHabitUiState.Error -> {
                            Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        GroupHabitUiState.Loading -> {showProgress()}
                        GroupHabitUiState.Nothing -> {hideProgress()}
                        else -> {}
                    }

                }
            }
        }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewModel.getGroupHabits()

        return binding.root
    }

    private fun showProgress(){ binding.progress.isVisible=true }
    private fun hideProgress(){ binding.progress.isVisible=false }

}