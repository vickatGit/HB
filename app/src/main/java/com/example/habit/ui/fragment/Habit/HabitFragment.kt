package com.example.habit.ui.fragment.Habit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.habit.R
import com.example.habit.databinding.FragmentHabitsBinding

class HabitFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding=FragmentHabitsBinding.inflate(inflater,container,false)
        return binding.root
    }


}