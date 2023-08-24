package com.example.habit.ui.activity.ProfileActivity

import android.content.Intent
import android.graphics.Bitmap
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
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.databinding.ActivityProfileBinding
import com.example.habit.ui.util.BitmapUtils
import com.example.habit.ui.viewmodel.ProfileViewModel
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

    private val viewModel:ProfileViewModel by viewModels()

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authPref: AuthPref

    var photoGetter = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
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
                                         val requestBody =RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
                                        val multiBody= MultipartBody.Part.createFormData("file", file.name, requestBody)
                                    }

                                } catch (e: IOException) { e.printStackTrace() }
                            } else {
                                Toast.makeText(this@ProfileActivity, "Not an Image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ProfileActivity, "" + e, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is ProfileUiState.Success -> {
                            Toast.makeText(this@ProfileActivity,it.msg, Toast.LENGTH_SHORT).show()
                            hideProgress()
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
        binding.edit.setOnClickListener {
            isProfileEditable(true)
        }
        binding.userBio.addTextChangedListener {
            viewModel.user?.userBio=it.toString()
        }
        binding.updateProfile.setOnClickListener {
            viewModel.updateProfile()
        }
        binding.cameraIcon.setOnClickListener {
            getImage()
        }
        viewModel.getProfile(authPref.getUserId())
    }
    private fun bindProfileData() {
        viewModel.user?.let {
            binding.userBio.setText(it.userBio)
            binding.userName.text=it.username
            binding.followersCnt.text = it.followers
            binding.followingCnt.text = it.followings
        }
    }

    private fun showProgress(){
        binding.progress.isVisible=true
    }
    private fun hideProgress(){
        binding.progress.isVisible=false
    }
    private fun isProfileEditable(isEditable:Boolean){
        binding.edit.isVisible=!isEditable
        binding.userBio.isEnabled=isEditable
        binding.updateProfile.isVisible=isEditable
        binding.cameraIcon.isVisible=isEditable
    }
    private fun getImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            intent.type = "image/*"
            photoGetter.launch(intent)
        } else {
            val imageIntent = Intent()
            imageIntent.setType("image/*")
            imageIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf<String>("image/*"))
            imageIntent.setAction(Intent.ACTION_GET_CONTENT)
            photoGetter.launch(imageIntent)
        }
    }

    override fun onBackPressed() {
        if(!binding.edit.isVisible)
            isProfileEditable(false)
        else
            super.onBackPressed()

    }
}