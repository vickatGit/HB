package com.example.habit.ui.activity.LoginActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.auth0.android.jwt.JWT
import com.example.habit.R
import com.example.habit.databinding.ActivityLoginBinding
import com.example.habit.domain.models.Login.LoginView
import com.example.habit.ui.activity.HomeActivity
import com.example.habit.ui.activity.SignupActivity.SignupActivity
import com.example.habit.ui.constants.AuthConstants
import com.example.habit.ui.viewmodel.AuthViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

     private val viewModel:AuthViewModel  by viewModels()

    private lateinit var authState: AuthState
    private lateinit var googleAuthService: AuthorizationService
    private lateinit var googleAuthRequest: AuthorizationRequest
    private lateinit var googleAuthServiceConfig: AuthorizationServiceConfiguration
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.login.collectLatest { 
                    when(it){
                        is LoginUiState.Loading -> {
                            showProgress()
                        }
                        is LoginUiState.Error -> {
                            Toast.makeText(this@LoginActivity,"got"+it.error,Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        is LoginUiState.Success -> {
                            Toast.makeText(this@LoginActivity,"got"+it.message,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                            hideProgress()
                        }
                        is LoginUiState.Nothing -> {}
                    }
                }
            }
        }
        binding.login.setOnClickListener {
            if(binding.email.text.toString().isBlank() || !isEmailValid(binding.email.text.toString())){
                Toast.makeText(this,"Enter Valid Email",Toast.LENGTH_SHORT).show()
            }else if(binding.password.text.toString().isBlank()){
                Toast.makeText(this,"Password can't be blank",Toast.LENGTH_SHORT).show()
            }else{
                viewModel.doLogin(LoginView(binding.email.text.toString(),binding.password.text.toString()))
            }
        }
        binding.navSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignupActivity::class.java))
        }
        binding.google.setOnClickListener {

            //AppAuth lib Must Be Instructed how to interact with the authorization service
            // we can do this by creating an instance of AuthorizationServiceConfiguration
            // by providing Authorization and Token End point
            googleAuthServiceConfig = AuthorizationServiceConfiguration(
                Uri.parse(AuthConstants.URL_AUTHORIZATION), //Authorization Endpoint
                Uri.parse(AuthConstants.URL_TOKEN_EXCHANGE), // Token Endpoint
                null,
                Uri.parse(AuthConstants.URL_LOGOUT)
            )
            val config=AppAuthConfiguration.Builder().apply {
                setBrowserMatcher (
                    BrowserAllowList(
                        VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                        VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB,
                    )
                )
            }.build()


            //now our request is ready we have to fire the intent to show the auth page by calling startActivityForResult
            // to create we need help of AuthorizationService instance to get the authIntent to fire
            googleAuthService=AuthorizationService(applicationContext,config)
            attemptAuth()



        }
    }

    private fun attemptAuth() {
        val secureRandom=SecureRandom()
        val bytes=ByteArray(64)
        secureRandom.nextBytes(bytes)
        val encoding=Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        val codeVerifier = Base64.encodeToString(bytes,encoding)

        val digest=MessageDigest.getInstance(AuthConstants.MESSAGE_DIGEST_ALGORITHM)
        val hash = digest.digest(codeVerifier.toByteArray())
        val codeChallenge = Base64.encodeToString(hash,encoding)

        val builder = AuthorizationRequest.Builder(
            googleAuthServiceConfig,
            AuthConstants.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(AuthConstants.URL_AUTH_REDIRECT)
        ).apply {
            setCodeVerifier(
                codeVerifier,
                codeChallenge,
                AuthConstants.CODE_VERIFIER_CHALLENGE_METHOD
            )
            setScopes(
                AuthConstants.SCOPE_EMAIL,
                AuthConstants.SCOPE_OPENID
            )
        }
        val request=builder.build()
        val authIntent = googleAuthService.getAuthorizationRequestIntent(request)
        startActivityForResult(authIntent,123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){

            if(resultCode== RESULT_OK){
                val authResponse = AuthorizationResponse.fromIntent(data!!)
                val error = AuthorizationException.fromIntent(data)
                authState = AuthState(authResponse,error)
                authResponse?.let {
                    val tokenRequest = authResponse.createTokenExchangeRequest()
                    Log.e("TAG", "onActivityResult: ${Gson().toJson(tokenRequest)}", )
                    googleAuthService.performTokenRequest(tokenRequest){ res,err ->
                        res?.let {
                            authState.update(res,err)
                            val jwt = JWT(res.idToken!!)
                        }

                    }
                }
            }
        }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return emailPattern.matches(email)
    }
    private fun showProgress(){ binding.progress.isVisible=true }
    private fun hideProgress(){ binding.progress.isVisible=false }
}