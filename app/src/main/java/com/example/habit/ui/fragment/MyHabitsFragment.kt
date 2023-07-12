package com.example.habit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.habit.R
import com.example.habit.databinding.FragmentMyHabitsBinding

class MyHabitsFragment : Fragment() {

    private var _binding: FragmentMyHabitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentMyHabitsBinding.inflate(inflater,container,false)
        binding.newHabitContainer.setOnClickListener {
            findNavController().navigate(R.id.action_myHabitsFragment_to_addHabitFragment)
        }
        binding.habits.setOnClickListener {
            findNavController().navigate(R.id.action_myHabitsFragment_to_habitsFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}