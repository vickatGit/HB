package com.habitude.habit.ui.activity.SignupActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.habitude.habit.data.network.model.SignupModel.SignupView
import com.habitude.habit.databinding.ActivitySignupBinding
import com.habitude.habit.ui.activity.LoginActivity.LoginActivity2
import com.habitude.habit.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: ActivitySignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.signup.collectLatest {
                    when(it){
                        is SignupUiState.Loading -> showProgress()
                        is SignupUiState.Error -> {
                            Toast.makeText(this@SignupActivity,it.error, Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        is SignupUiState.Nothing -> { }
                        is SignupUiState.Success -> {
                            Toast.makeText(this@SignupActivity,it.message, Toast.LENGTH_SHORT).show()
                            hideProgress()
                            startActivity(Intent(this@SignupActivity,LoginActivity2::class.java))
                        }
                    }
                }
            }
        }
        binding.signup.setOnClickListener {
            if(binding.email.text.toString().isBlank()){
                Toast.makeText(this,"Email can't be blank",Toast.LENGTH_SHORT).show()
            }else if(binding.username.text.toString().isBlank()){
                Toast.makeText(this,"Username can't be blank",Toast.LENGTH_SHORT).show()
            }else if(!isEmailValid(binding.email.text.toString())){
                Toast.makeText(this,"Enter Valid Email",Toast.LENGTH_SHORT).show()
            }else if(binding.password.text.toString().isBlank()){
                Toast.makeText(this,"Password can't be blank",Toast.LENGTH_SHORT).show()
            }else if(binding.confirmPassword.text.toString().isBlank()){
                Toast.makeText(this,"Confirm Password can't be blank",Toast.LENGTH_SHORT).show()
            }else if(!(binding.password.text.toString().equals(binding.confirmPassword.text.toString()))){
                Toast.makeText(this,"Confirm Password and Password should be same",Toast.LENGTH_SHORT).show()
            }else{
                viewModel.doSignup(SignupView(binding.email.text.toString(),binding.password.text.toString(),binding.username.text.toString()))
            }
        }
        binding.navLogin.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity2::class.java))
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return emailPattern.matches(email)
    }
    private fun showProgress(){ binding.progress.isVisible=true }
    private fun hideProgress(){ binding.progress.isVisible=false }

}