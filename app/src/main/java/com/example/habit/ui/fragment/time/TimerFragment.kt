package com.example.habit.ui.fragment.time

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.habit.databinding.FragmentTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class TimerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    companion object{
        const val TIME_RESULT="time_result"
        const val TIME="time"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentTimerBinding.inflate(inflater,container,false)
        binding.save.setOnClickListener(View.OnClickListener {
            if (binding.timer.getSelectedTime() != null ) {
//                val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
//                val formattedTime = timeFormat.format(Date(binding.timer.getSelectedTime()?.timeInMillis?:0))
//                Log.e("alarm scheduler", " from time setter invoke: alarm scheduled at $formattedTime", )
                setFragmentResult(TIME_RESULT, bundleOf(TIME to binding.timer.getSelectedTime()?.timeInMillis))
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Please Select the Time", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}