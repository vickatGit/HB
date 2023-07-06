package com.example.habit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.habit.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
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
                    navController.navigate(R.id.habitsFragment)
                    true
                }
                R.id.mentees -> {
                    navController.navigate(R.id.menteesFragment)
                    true
                }
                R.id.community -> {
                    navController.navigate(R.id.communityFragment)
                    true
                }
                else -> {false}
            }
        }

        setContentView(binding.root)
    }
}