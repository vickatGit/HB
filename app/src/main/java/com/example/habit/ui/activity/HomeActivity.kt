package com.example.habit.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.habit.R
import com.example.habit.databinding.ActivityHomeBinding
import com.example.habit.ui.activity.AddMembersActivity.AddMembersActivity
import com.example.habit.ui.activity.ProfileActivity.ProfileActivity
import com.example.habit.ui.activity.UserSearchActivity.UserSearchActivity
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
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
                else -> {

//                    for (i in 0 until binding.bottomNavigationView.menu.size()) {
//                        val item = binding.bottomNavigationView.menu.getItem(i)
//                        item.isChecked = false
//                    }
                }
            }
            binding.bottomNavigationView.setOnItemSelectedListener(navListener)
        }
        binding.addHabitFab.setOnClickListener {
            navController.navigate(R.id.addHabitFragment)
        }
        binding.bottomNavigationView.setOnItemSelectedListener(navListener)

        setContentView(binding.root)
    }
}
