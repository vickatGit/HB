package com.habitude.habit.ui.activity.ProfileActivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.ActivityProfileBinding
import com.habitude.habit.ui.activity.ChatsActivity.ChatsActivity
import com.habitude.habit.ui.activity.FollowFollowingActivity.FollowFollowingActivity
import com.habitude.habit.ui.activity.UserActivity.UserActivity
import com.habitude.habit.ui.util.BitmapUtils
import com.habitude.habit.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var avatarUrl: String? = null
    private val viewModel:ProfileViewModel by viewModels()

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var bioBackground:Drawable

    @Inject
    lateinit var authPref: AuthPref

    private var photoGetter = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == RESULT_OK) {
            try {
                val data = result.data
                if (data != null && data.data != null) {
                    val contentType = contentResolver.getType(data.data!!)
                    if (contentType!!.startsWith("image/")) {
                        val selectedImageUri = data.data
                        val selectedImageBitmap: Bitmap
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                            val file: File? = BitmapUtils.saveBitmapToFile(this@ProfileActivity, selectedImageBitmap, "soem")
                            file?.let {
                                val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
                                viewModel.uploadProfileImage(requestBody)
                            }
                        } catch (e: IOException) { e.printStackTrace() }
                    } else Toast.makeText(this@ProfileActivity, "Not an Image", Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "" + e, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        avatarUrl = intent.getStringExtra(AVATAR_URL)
        bioBackground=binding.userBio.background
        isProfileEditable(false)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED ){
                viewModel.uiState.collectLatest {
                    when(it){
                        is ProfileUiState.Success -> {
                            Toast.makeText(this@ProfileActivity,it.msg, Toast.LENGTH_SHORT).show()
                            hideProgress()
                            isProfileEditable(false)
                        }
                        is ProfileUiState.Error -> {
                            Toast.makeText(this@ProfileActivity,it.error, Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }
                        ProfileUiState.Loading -> {
                            showProgress()
                        }
                        is ProfileUiState.Profile -> {
                            hideProgress()
                            viewModel.user = it.user
                            bindProfileData()
                        }
                        ProfileUiState.Nothing -> {}
                    }
                }
            }
        }
        avatarUrl?.let {
            Glide.with(this@ProfileActivity).load(avatarUrl).into(binding.userProfileImage)
        }
        binding.edit.setOnClickListener {
            isProfileEditable(true)
        }
//        binding.cameraIcon.setOnClickListener {
//            checkPermissionAndGetImage()
//        }
//        binding.userBio.addTextChangedListener {
//            viewModel.user?.userBio=it.toString()
//        }
        binding.updateProfile.setOnClickListener {
            viewModel.user?.userBio=binding.userBio.text.toString()
            viewModel.updateProfile()
        }
        binding.cameraIcon.setOnClickListener {
            getImage()
        }
        binding.followerCont.setOnClickListener {
            val intent = Intent(this@ProfileActivity,FollowFollowingActivity::class.java)
            intent.putExtra(FollowFollowingActivity.IS_FOLLOWERS,true)
            startActivityForResult(intent,568)
        }
        binding.followingCont.setOnClickListener {
            val intent = Intent(this@ProfileActivity,FollowFollowingActivity::class.java)
            intent.putExtra(FollowFollowingActivity.IS_FOLLOWERS,false)
            startActivityForResult(intent,568)

        }
        binding.chats.setOnClickListener {
            startActivity(Intent(this,ChatsActivity::class.java))
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
        viewModel.getProfile(authPref.getUserId())
    }
    private fun bindProfileData() {
        viewModel.user?.let {
            binding.userBio.setText(it.userBio)
            binding.userName.text=it.username
            binding.followersCnt.text = it.followers
            binding.followingCnt.text = it.followings
            Glide.with(this@ProfileActivity).load(it.avatarUrl).into(binding.userProfileImage)
        }
    }

    private fun showProgress(){
        binding.progress.isVisible=true
    }
    private fun hideProgress(){
        binding.progress.isVisible=false
    }
    private fun isProfileEditable(isEditable:Boolean){
        if(isEditable)
            binding.userBio.background= bioBackground
        else
            binding.userBio.background=null
        binding.edit.isVisible=!isEditable
        binding.userBio.isEnabled=isEditable
        binding.updateProfile.isVisible=isEditable
//        binding.cameraIcon.isVisible=isEditable
    }
    private fun getImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            intent.type = "image/*"
            photoGetter.launch(intent)
        } else {
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            imageIntent.action = Intent.ACTION_GET_CONTENT
            photoGetter.launch(imageIntent)
        }
    }

    override fun onBackPressed() {
        if(!binding.edit.isVisible) {
            isProfileEditable(false)
            bindProfileData()
        } else super.onBackPressed()
    }

    private fun checkPermissionAndGetImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(Intent.ACTION_PICK)
            photoGetter.launch(intent)
        } else {
            val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
            imageIntent.type = "image/*"
            imageIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            photoGetter.launch(imageIntent)
        }
    }

    companion object {
        val AVATAR_URL: String = "avatar_url"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            if(it.getBooleanExtra(UserActivity.FOLLOW_FOLLOWING_API_SHOULD_BE_CALLED,false)){
                viewModel.getProfile(authPref.getUserId())
            }
        }
    }
}