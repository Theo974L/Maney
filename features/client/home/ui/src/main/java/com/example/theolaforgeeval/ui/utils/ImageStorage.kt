package com.example.theolaforgeeval.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File

object ImageStorage {

    fun persist(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "goal_images").apply { mkdirs() }
            val file = File(dir, "goal_${System.currentTimeMillis()}.jpg")

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
