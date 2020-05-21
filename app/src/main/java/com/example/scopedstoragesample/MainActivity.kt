package com.example.scopedstoragesample

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createFile(view: View) {
        // Add a specific media item.
        val resolver = applicationContext.contentResolver

        val fileId = Random.nextInt(5)
        val contentValues = ContentValues().apply {
            put(MediaStore.DownloadColumns.DISPLAY_NAME, "agreement_$fileId")
            put(MediaStore.DownloadColumns.MIME_TYPE, "image/webp")
            put(MediaStore.DownloadColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LangLive/WebP")
        }
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        resolver.openOutputStream(uri!!).use {
            it?.write("hello file\n".toByteArray())
        }

        val descriptor = resolver.openFileDescriptor(uri, "r")
        FileInputStream(descriptor!!.fileDescriptor).bufferedReader()
            .use {
                val text = it.readLine()
                Log.d("Main", "text ${text}")
            }

        Log.d("Main", "path ${uri}")

    }

    fun listFile(view: View) {
        val resolver = applicationContext.contentResolver
        val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.DownloadColumns.DISPLAY_NAME} DESC"
        val filesUris = mutableListOf<Uri>()
        resolver.query(
            external,
            arrayOf(
                MediaStore.DownloadColumns._ID,
                MediaStore.DownloadColumns.DISPLAY_NAME
            ),
            "${MediaStore.DownloadColumns.DISPLAY_NAME} = 'agreement_1.webp'",
            null,
            sortOrder)
            ?.use {
                while (it.moveToNext()) {
                    val index = it.getColumnIndexOrThrow(MediaStore.DownloadColumns._ID)
                    filesUris.add(ContentUris.withAppendedId(external, it.getLong(index)))
                }
            }
        filesUris.forEach {
            Log.d("Main", "list uri $it")
        }

        val file = File("/sdcard/Download/LangLive/WebP/agreement_1.webp")
        Log.d("Main", "list uri , exist ${file.exists()}")
    }
}
