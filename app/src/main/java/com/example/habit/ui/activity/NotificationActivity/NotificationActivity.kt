package com.example.habit.ui.activity.NotificationActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.databinding.ActivityNotificationBinding
import com.example.habit.domain.models.notification.HabitRequest
import com.example.habit.ui.adapter.HabitRequestAdapter
import com.example.habit.ui.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private var habitRequests: List<HabitRequest>? = null
    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is NotificationUiState.Error -> {
                            Toast.makeText(
                                this@NotificationActivity,
                                "${it.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        NotificationUiState.Loading -> {}
                        NotificationUiState.Nothing -> {}
                        is NotificationUiState.Success -> {
                            habitRequests = it.notifications
                            habitRequests?.let {
                                binding.notifications.layoutManager =
                                    LinearLayoutManager(this@NotificationActivity)
                                val habitRequestAdapter = HabitRequestAdapter(
                                    habitRequests = it,
                                    acceptRequest = {
                                        viewModel.acceptHabitRequest(it)
                                    },
                                    rejectRequest = {
                                        viewModel.rejectHabitRequest(it)
                                    })
                                binding.notifications.adapter = habitRequestAdapter
                            }

                        }

                        is NotificationUiState.RequestAccepted -> {
                            Toast.makeText(this@NotificationActivity, it.msg, Toast.LENGTH_SHORT)
                                .show()
                            viewModel.getNotifications()
                        }

                        NotificationUiState.RequestRejected -> {
                            viewModel.getNotifications()
                        }
                    }
                }
            }
        }
        viewModel.getNotifications()
    }
}