package com.habitude.habit.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.ActivityHomeBinding
import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.ui.activity.ChatsActivity.ChatsActivity
import com.habitude.habit.ui.activity.UserSearchActivity.UserSearchActivity
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val NOTIFICATION_PERMISSION_REQUEST_CODE: Int=123
    private lateinit var navController: NavController
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private var prevDestinationId:Int=1

    @Inject
    lateinit var authPref:AuthPref

    @Inject
    lateinit var socialRepo: SocialRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.meow.add(MeowBottomNavigation.Model(1,R.drawable.home_filled_icon))
        binding.meow.add(MeowBottomNavigation.Model(2,R.drawable.habit_filled_icon))
        binding.meow.add(MeowBottomNavigation.Model(3,R.drawable.plus))
        binding.meow.add(MeowBottomNavigation.Model(4,R.drawable.chat_icon))
        binding.meow.add(MeowBottomNavigation.Model(5,R.drawable.community_filled_icon))
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHost.navController
        turnOnBottomNavMenuClickListener()
        binding.meow.show(1,true)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.meow.setOnClickMenuListener{  }
            when (destination.id) {
                R.id.homeFragment -> { prevDestinationId =1
                    binding.meow.show(1,true) }
                R.id.myHabitsFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.habitsFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.habitFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.groupsFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.groupFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.completedHabitFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.myHabitsFragment -> { prevDestinationId =2
                    binding.meow.show(2,true) }
                R.id.addHabitFragment -> { prevDestinationId =3
                    binding.meow.show(3,true) }
                R.id.menteesFragment -> {  binding.meow.show(prevDestinationId,true)
                }
                R.id.communityFragment -> { binding.meow.show(prevDestinationId,true) }
                else -> {}
            }
            turnOnBottomNavMenuClickListener()
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                askNotificationPermissionFor33()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            socialRepo.getHomeData()
        }


    }

    private fun turnOnBottomNavMenuClickListener() {
        binding.meow.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    navController.popBackStack(R.id.homeFragment, true)
                    navController.navigate(R.id.homeFragment)
                    prevDestinationId=1
                    true
                }
                2 -> {
                    navController.popBackStack(R.id.myHabitsFragment, true)
                    navController.navigate(R.id.myHabitsFragment)
                    prevDestinationId=2
                    true
                }
                3 -> {
                    navController.popBackStack(R.id.addHabitFragment, true)
                    navController.navigate(R.id.addHabitFragment)
                    prevDestinationId=3
                    true
                }

                4 -> {
                    navController.popBackStack(R.id.menteesFragment, true)
                    startActivity(Intent(this@HomeActivity, ChatsActivity::class.java))
                    false
                }

                5 -> {
                    navController.popBackStack(R.id.communityFragment, true)
                    startActivity(Intent(this@HomeActivity, UserSearchActivity::class.java))
                    false
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        binding.meow.setOnClickMenuListener{  }
        binding.meow.show(prevDestinationId,true)
        turnOnBottomNavMenuClickListener()
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
