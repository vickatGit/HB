package com.example.habit.ui.fragment.HomeFragment

import android.content.Intent
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
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.Action
import com.example.habit.databinding.FragmentHomeBinding
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.example.habit.ui.activity.ProfileActivity.ProfileActivity
import com.example.habit.ui.adapter.HomePageEpoxyRecycler
import com.example.habit.ui.model.Epoxy.ProgressSectionHabit
import com.example.habit.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    @Inject
    lateinit var getHabitThumbsUseCase: GetHabitThumbsUseCase
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel:HomeViewModel  by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is HomeUiState.HomeData -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                getHabitThumbsUseCase(this).collectLatest { progresses ->
                                    val habits = mutableListOf<ProgressSectionHabit>()
                                    var totalHabits = 0
                                    var completedHabits = 0
                                    progresses.forEach { habit ->
                                        if (habit.entries != null) {
                                            if (habit.entries!!.isEmpty()) {
                                                totalHabits++
                                                habits.add(ProgressSectionHabit(habit.title!!, false))
                                            }
                                            habit.entries?.forEach {
                                                if (it.key == LocalDate.now()) {
                                                    totalHabits++
                                                    if (it.value.completed) ++completedHabits
                                                    habits.add(
                                                        ProgressSectionHabit(
                                                            habit.title!!,
                                                            it.value.completed
                                                        )
                                                    )
                                                }
                                            }
                                        } else {
                                            totalHabits++
                                        }
                                        withContext(Dispatchers.Main) {
                                            val epoxyController = HomePageEpoxyRecycler(
                                                progresses,
                                                totalHabits,
                                                completedHabits,
                                                habits,
                                                this
                                            ) {
                                                handleEvents(it)
                                            }
                                            binding.homeRecycler.setController(epoxyController)
                                            Log.e("TAG", "onCreateView: home data $it ",)
                                            it.homeUiData?.data?.sections?.let {
                                                epoxyController.homeSections = it
                                            }
                                        }
                                    }
                                }
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
        when(it.actionType){
            "open_screen" -> {
                handleServerNavigation(it.screenType,it.resId)
            }
        }
    }

    private fun handleServerNavigation(screenType: String, resId: String?) {
        when(screenType){
            "profile" -> { startActivity(Intent(requireContext(),ProfileActivity::class.java)) }
            "add_habit" -> {
                val bundle = Bundle()
                bundle.putBoolean("isFromHome",true)
                bundle.putString("habitTitle",resId)
                findNavController().navigate(R.id.action_homeFragment_to_addHabitFragment2,bundle)
            }
        }

    }


}