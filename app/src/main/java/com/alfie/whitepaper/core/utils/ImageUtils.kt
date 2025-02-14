package com.alfie.whitepaper.core.utils


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import checkAndAskPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


//writing files to storage via scope and normal manner acc. to Api level
internal fun Context.saveImage(
    bitmap: Bitmap,
    imageFileType: String = ".png",
    imageMimeType: String = "image/png",
    bitmapCompressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): Uri? {
    var uri: Uri? = null
    try {
        val fileName = System.nanoTime().toString() + imageFileType
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, imageMimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val file = File(directory, fileName)
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
            }
        }

        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            contentResolver.openOutputStream(it).use { output ->
                if (output != null) {
                    bitmap.compress(bitmapCompressFormat, 100, output)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.apply {
                    clear()
                    put(MediaStore.Audio.Media.IS_PENDING, 0)
                }
                contentResolver.update(uri, values, null, null)
            }
        }
        return uri
    } catch (e: java.lang.Exception) {
        if (uri != null) {
            // Don't leave an orphan entry in the MediaStore
            contentResolver.delete(uri, null, null)
        }
        throw e
    }
}

fun shareAsPng(imageBitmap: ImageBitmap?, context: Context) {
    imageBitmap?.let {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uri = saveBitmapToTempFile(context, imageBitmap.asAndroidBitmap())
                if (uri != null) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    withContext(Dispatchers.Main) {
                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                    }
                } else {
                    // Handle error
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


fun shareImageFromAssets(
    context: Context,
    imageName: String = "white_paper.png",
    @StringRes sharePopupTitle: Int,
    @StringRes textContent: Int
) {
    try {
        // Create a temporary file in the app's private storage
        val tempFile = File(context.cacheDir, imageName)

        // Copy image content from assets to the temporary file
        context.assets.open(imageName).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }

        // Set up FileProvider and create URI
        val providerAuthority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, providerAuthority, tempFile)

        // Create and share Intent with read-only permission
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, context.getString(textContent))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, context.getString(sharePopupTitle)))
    } catch (e: IOException) {
        Log.e("ImageSharing", "Error sharing image: $e")
        // Handle error appropriately, e.g., display a toast message
    }
}


fun saveBitmapToTempFile(context: Context, bitmap: Bitmap): Uri? {
    val file = File(context.cacheDir, "temp_image.png")
    var outputStream: FileOutputStream? = null
    return try {
        outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        outputStream?.close()
    }
}

fun saveAsImage(
    imageBitmap: ImageBitmap?,
    context: Context,
    imageFileType: String,
    imageMimeType: String,
    bitmapCompressFormat: Bitmap.CompressFormat
) {
    imageBitmap?.let {
        context.getActivity()?.checkAndAskPermission {
            CoroutineScope(Dispatchers.IO).launch {
                val uri = context.saveImage(
                    imageBitmap.asAndroidBitmap(),
                    imageFileType = imageFileType,
                    imageMimeType = imageMimeType,
                    bitmapCompressFormat = bitmapCompressFormat
                )
                withContext(Dispatchers.Main) {
                    context.getActivity()?.startActivity(activityChooser(uri))
                }
            }
        }
    }
}




