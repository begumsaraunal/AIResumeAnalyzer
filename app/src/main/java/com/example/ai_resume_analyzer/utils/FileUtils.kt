package com.example.ai_resume_analyzer.utils

import android.content.Context
import android.net.Uri
// Utility class for handling file related operations.
// Currently used to read the selected PDF resume from device storage
// and convert it into a ByteArray for API upload.
object FileUtils {

    fun readPdf(context: Context, uri: Uri): ByteArray? {

        return context.contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }

    }
}