package com.example.habit.ui.fragment.HomeFragment

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
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.FragmentHomeBinding
import com.example.habit.ui.adapter.HomePageEpoxyRecycler
import com.example.habit.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {


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
                            val epoxyController = HomePageEpoxyRecycler()
                            binding.homeRecycler.setController(epoxyController)
                            Log.e("TAG", "onCreateView: home data $it ", )
                            it.homeUiData?.data?.sections?.let {
                                epoxyController.homeSections= it
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


}