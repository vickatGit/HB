package com.example.habit.ui.fragment.HomeFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.Action
import com.example.habit.databinding.FragmentHomeBinding
import com.example.habit.domain.UseCases.HabitUseCase.GetAllHabitsUseCase
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.example.habit.ui.activity.NotificationActivity.NotificationActivity
import com.example.habit.ui.activity.ProfileActivity.ProfileActivity
import com.example.habit.ui.adapter.HomePageEpoxyRecycler
import com.example.habit.ui.model.Epoxy.ProgressSectionHabit
import com.example.habit.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    @Inject
    lateinit var getAllHabitsUseCase: GetAllHabitsUseCase
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is HomeUiState.HomeData -> {
                            val habits = mutableListOf<ProgressSectionHabit>()
                            var totalHabits = 0
                            var completedHabits = 0
                            getAllHabitsUseCase(this).forEach { habit ->
                                if (habit.entries != null) {
                                    if (habit.entries!!.isEmpty()) {
                                        totalHabits++
                                        habits.add(ProgressSectionHabit(habit.title!!, false))
                                    }
                                    habit.entries?.forEach {
                                        if (it.key == LocalDate.now()) {
                                            totalHabits++
                                            if (it.value.completed) ++completedHabits
                                            habits.add(ProgressSectionHabit(habit.title!!, it.value.completed))
                                        }
                                    }
                                } else {
                                    totalHabits++
                                }
                            }
                            withContext(Dispatchers.Main) {
                                Log.e("TAG", "onCreateView: collect mAin")
                                val epoxyController = HomePageEpoxyRecycler(
                                    totalHabits,
                                    completedHabits,
                                    habits
                                ) {
                                    handleEvents(it)
                                }
                                binding.homeRecycler.setController(epoxyController)
                                it.homeUiData?.data?.sections?.let {
                                    epoxyController.homeSections = it
                                }
                                return@withContext
                            }


                        }

                        is HomeUiState.Error -> {

                        }

                        HomeUiState.Loading -> {

                        }

                        HomeUiState.Nothing -> {

                        }
                    }
                }
            }
        }
        viewModel.getHomeData()
        return binding.root
    }

    private fun handleEvents(it: Action) {
        when (it.actionType) {
            "open_screen" -> {
                handleServerNavigation(it.screenType, it.resId)
            }
        }
    }

    private fun handleServerNavigation(screenType: String, resId: String?) {
        when (screenType) {
            "profile" -> {
                startActivity(Intent(requireContext(), ProfileActivity::class.java))
            }

            "add_habit" -> {
                val bundle = Bundle()
                bundle.putBoolean("isFromHome", true)
                bundle.putString("habitTitle", resId)
                findNavController().navigate(R.id.action_homeFragment_to_addHabitFragment2, bundle)
            }

            "notification" -> {
                startActivity(Intent(requireContext(), NotificationActivity::class.java))
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}