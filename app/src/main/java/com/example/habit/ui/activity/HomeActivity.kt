package com.example.habit.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.habit.R
import com.example.habit.databinding.ActivityHomeBinding
import com.example.habit.ui.activity.ProfileActivity.ProfileActivity
import com.example.habit.ui.activity.UserSearchActivity.UserSearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityHomeBinding.inflate(layoutInflater)
        val navHost=supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController=navHost.navController
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.popBackStack(R.id.homeFragment,true)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.my_habits -> {
                    navController.navigate(R.id.myHabitsFragment)
                    true
                }
                R.id.mentees -> {
                    startActivity(Intent(this@HomeActivity,UserSearchActivity::class.java))
//                    navController.navigate(R.id.menteesFragment)
                    true
                }
                R.id.community -> {
                    startActivity(Intent(this@HomeActivity,ProfileActivity::class.java))
//                    navController.navigate(R.id.communityFragment)
                    true
                }
                else -> {false}
            }
        }

        setContentView(binding.root)
    }
}