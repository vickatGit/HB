package com.example.habit.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.habit.R
import com.example.habit.databinding.ActivityHomeBinding
import com.example.habit.ui.activity.ChatsActivity.ChatsActivity
import com.example.habit.ui.activity.UserSearchActivity.UserSearchActivity
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val NOTIFICATION_PERMISSION_REQUEST_CODE: Int=123
    private lateinit var navController: NavController
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHost.navController
        val navListener=NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navController.popBackStack(R.id.homeFragment, true)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.my_habits -> {
                    navController.popBackStack(R.id.myHabitsFragment, true)
                    navController.navigate(R.id.myHabitsFragment)
                    true
                }

                R.id.mentees -> {
                    navController.popBackStack(R.id.menteesFragment, true)
                    startActivity(Intent(this@HomeActivity, ChatsActivity::class.java))
                    false
                }

                R.id.community -> {
                    navController.popBackStack(R.id.communityFragment, true)
                    startActivity(Intent(this@HomeActivity, UserSearchActivity::class.java))
                    false
                }

                else -> {
                    false
                }
            }
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.bottomNavigationView.setOnItemSelectedListener(null)
            when (destination.id) {
                R.id.homeFragment -> { binding.bottomNavigationView.menu[0].isChecked = true }
                R.id.myHabitsFragment -> { binding.bottomNavigationView.menu[1].isChecked = true }
                R.id.menteesFragment -> { binding.bottomNavigationView.menu[2].isChecked = true }
                R.id.communityFragment -> { binding.bottomNavigationView.menu[3].isChecked = true }
                else -> {}
            }
            binding.bottomNavigationView.setOnItemSelectedListener(navListener)
        }
        binding.addHabitFab.setOnClickListener {
            navController.navigate(R.id.addHabitFragment)
        }
        binding.bottomNavigationView.setOnItemSelectedListener(navListener)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                askNotificationPermissionFor33()
            }
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun askNotificationPermissionFor33() {
        if (ContextCompat.checkSelfPermission(
                this@HomeActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Snackbar.make(findViewById(R.id.home_layout), "The user denied the notifications ):", Snackbar.LENGTH_LONG)
                .setAction("Settings") {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("com.onesilisondiode.geeksforgeeks", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
        } else {
            ActivityCompat.requestPermissions(
                this@HomeActivity,
                arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}
