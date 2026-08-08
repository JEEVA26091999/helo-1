package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ShareUtils {

    /**
     * Triggers a WhatsApp share intent with the given text and optional media URI.
     * If WhatsApp is not installed, falls back to standard Android chooser.
     */
    fun shareToWhatsApp(
        context: Context,
        text: String,
        mediaUri: Uri? = null
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (mediaUri != null) {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, mediaUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general intent chooser if WhatsApp is not directly installed
            try {
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    if (mediaUri != null) {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, mediaUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                        type = "text/plain"
                    }
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                val chooser = Intent.createChooser(fallbackIntent, "Share via")
                context.startActivity(chooser)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open share application", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * General intent share helper.
     */
    fun shareGeneral(
        context: Context,
        title: String = "Share Content",
        text: String
    ) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
