package com.example.habit.ui.activity.LoginActivity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import com.example.habit.R
import com.example.habit.databinding.ActivityLoginBinding
import com.example.habit.ui.constants.AuthConstants
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import java.security.MessageDigest
import java.security.SecureRandom

class LoginActivity : AppCompatActivity() {
    private lateinit var googleAuthService: AuthorizationService
    private lateinit var googleAuthRequest: AuthorizationRequest
    private lateinit var googleAuthServiceConfig: AuthorizationServiceConfiguration
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                AuthConstants.SCOPE_OPENID,
                AuthConstants.SCOPE_DRIVE
            )
        }
        val request=builder.build()
        val authIntent = googleAuthService.getAuthorizationRequestIntent(request)
        startActivityForResult(authIntent,123)
    }
}