package com.habitude.habit.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BitmapUtils {
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        val file = getOutputMediaFile(context, fileName)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                return file
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun getOutputMediaFile(context: Context, fileName: String): File? {
        // Get the directory for storing images or other files
        val mediaStorageDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "YourDirectoryName")

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // Create a media file name
        val mediaFile: File
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = fileName + "_" + timeStamp + ".jpg"
        mediaFile = File(mediaStorageDir.path + File.separator + imageFileName)
        return mediaFile
    }
}