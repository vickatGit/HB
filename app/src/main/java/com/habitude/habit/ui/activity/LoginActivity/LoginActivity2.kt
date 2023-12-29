package com.habitude.habit.ui.activity.LoginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.ActivityLogin2Binding
import com.habitude.habit.domain.models.Login.LoginView
import com.habitude.habit.ui.activity.HomeActivity
import com.habitude.habit.ui.activity.SignupActivity.SignupActivity
import com.habitude.habit.ui.viewmodel.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity2 : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var authPref: AuthPref
    private var _binding: ActivityLogin2Binding? = null
    private val binding get() = _binding!!


//    private lateinit var authState: AuthState
//    private lateinit var googleAuthService: AuthorizationService
//    private lateinit var googleAuthRequest: AuthorizationRequest
//    private lateinit var googleAuthServiceConfig: AuthorizationServiceConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getFcmToken()

        if(authPref.getToken()!=null && authPref.getToken().isNotBlank() ){
            startActivity(Intent(this@LoginActivity2, HomeActivity::class.java))
            finish()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.login.collectLatest {
                    when(it){
                        is LoginUiState.Loading -> {
                            showProgress()
                        }
                        is LoginUiState.Error -> {
                            Toast.makeText(this@LoginActivity2,it.error, Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        is LoginUiState.Success -> {
                            Toast.makeText(this@LoginActivity2,it.message, Toast.LENGTH_SHORT).show()
                            finishAffinity()
                            startActivity(Intent(this@LoginActivity2, HomeActivity::class.java))
                            hideProgress()
                        }
                        is LoginUiState.Nothing -> {}
                    }
                }
            }
        }

        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.login.setOnClickListener {
            if(binding.emailEdit.text.toString().isBlank() || !isEmailValid(binding.emailEdit.text.toString())){
                Toast.makeText(this,"Enter Valid Email", Toast.LENGTH_SHORT).show()
            }else if(binding.passwordEdit.text.toString().isBlank()){
                Toast.makeText(this,"Password can't be blank", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.doLogin(LoginView(binding.emailEdit.text.toString(),binding.passwordEdit.text.toString(),AuthPref(this@LoginActivity2).getFcmToken()))
            }
        }
        binding.navSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity2, SignupActivity::class.java))
        }

//        binding.google.setOnClickListener {
//
//            //AppAuth lib Must Be Instructed how to interact with the authorization service
//            // we can do this by creating an instance of AuthorizationServiceConfiguration
//            // by providing Authorization and Token End point
//            googleAuthServiceConfig = AuthorizationServiceConfiguration(
//                Uri.parse(AuthConstants.URL_AUTHORIZATION), //Authorization Endpoint
//                Uri.parse(AuthConstants.URL_TOKEN_EXCHANGE), // Token Endpoint
//                null,
//                Uri.parse(AuthConstants.URL_LOGOUT)
//            )
//            val config=AppAuthConfiguration.Builder().apply {
//                setBrowserMatcher (
//                    BrowserAllowList(
//                        VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
//                        VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB,
//                    )
//                )
//            }.build()
//
//
//            //now our request is ready we have to fire the intent to show the auth page by calling startActivityForResult
//            // to create we need help of AuthorizationService instance to get the authIntent to fire
//            googleAuthService=AuthorizationService(applicationContext,config)
//            attemptAuth()
//
//
//
//        }
    }


//    private fun attemptAuth() {
//        val secureRandom=SecureRandom()
//        val bytes=ByteArray(64)
//        secureRandom.nextBytes(bytes)
//        val encoding=Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
//        val codeVerifier = Base64.encodeToString(bytes,encoding)
//
//        val digest=MessageDigest.getInstance(AuthConstants.MESSAGE_DIGEST_ALGORITHM)
//        val hash = digest.digest(codeVerifier.toByteArray())
//        val codeChallenge = Base64.encodeToString(hash,encoding)
//
//        val builder = AuthorizationRequest.Builder(
//            googleAuthServiceConfig,
//            AuthConstants.CLIENT_ID,
//            ResponseTypeValues.CODE,
//            Uri.parse(AuthConstants.URL_AUTH_REDIRECT)
//        ).apply {
//            setCodeVerifier(
//                codeVerifier,
//                codeChallenge,
//                AuthConstants.CODE_VERIFIER_CHALLENGE_METHOD
//            )
//            setScopes(
//                AuthConstants.SCOPE_EMAIL,
//                AuthConstants.SCOPE_OPENID
//            )
//        }
//        val request=builder.build()
//        val authIntent = googleAuthService.getAuthorizationRequestIntent(request)
//        startActivityForResult(authIntent,123)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode==123){
//
//            if(resultCode== RESULT_OK){
//                val authResponse = AuthorizationResponse.fromIntent(data!!)
//                val error = AuthorizationException.fromIntent(data)
//                authState = AuthState(authResponse,error)
//                authResponse?.let {
//                    val tokenRequest = authResponse.createTokenExchangeRequest()
//                    Log.e("TAG", "onActivityResult: ${Gson().toJson(tokenRequest)}", )
//                    googleAuthService.performTokenRequest(tokenRequest){ res,err ->
//                        res?.let {
//                            authState.update(res,err)
//                            val jwt = JWT(res.idToken!!)
//                        }
//
//                    }
//                }
//            }
//        }
//    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful) {
                Log.e("TAG", "onCreate: got FCM token Successfully ${it.result} ", )
                AuthPref(this@LoginActivity2).setFcmToken(it.result.toString())
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