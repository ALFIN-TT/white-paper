package com.alfie.whitepaper.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun saveToDownloads(fileName: String, content: String) {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)
    try {
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        outputStream.write(content.toByteArray())
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun saveToDownloads(fileName: String, content: ByteArray) {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    try {
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        outputStream.write(content)
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


fun saveFileToTempFile(content: String, context: Context): Uri? {
    val file = File(context.cacheDir, "project.txt")
    var outputStream: FileOutputStream? = null
    return try {
        file.createNewFile()
        outputStream = FileOutputStream(file)
        outputStream.write(content.toByteArray())
        FileProvider.getUriForFile(
            context,
            "com.alfie.whitepaper.fileprovider", // Replace with your FileProvider authority
            file
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        outputStream?.close()
    }
}

fun shareFile(content: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val uri = saveFileToTempFile(content, context)
            if (uri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "plain/txt"
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(shareIntent, "Share Project"))
                }
            } else {
                // Handle error
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@Throws(Exception::class)
fun saveFile(fileData: ByteArray, path: String) {
    val file = File(path)
    val bos = BufferedOutputStream(FileOutputStream(file, false))
    bos.write(fileData)
    bos.flush()
    bos.close()
}

@Throws(Exception::class)
fun readFile(filePath: String): ByteArray {
    val file = File(filePath)
    val fileContents = file.readBytes()
    val inputBuffer = BufferedInputStream(
        FileInputStream(file)
    )
    inputBuffer.read(fileContents)
    inputBuffer.close()

    return fileContents
}