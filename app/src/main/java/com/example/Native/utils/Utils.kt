package com.example.Native.utils

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal object Utils {
    private val hexArray = "0123456789ABCDEF".toCharArray()
    fun bytesToHex(bytes: ByteArray?): String? {
        if (bytes == null) return null
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = bytes[j] * 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return "0x" + String(hexChars)
    }

    fun bytesToHexAndString(bytes: ByteArray?): String? {
        return if (bytes == null) null else bytesToHex(bytes) + " (" + String(bytes) + ")"
    }

    fun now(): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        df.timeZone = tz
        return df.format(Date())
    }

    fun showNfcSettingsDialog(app: Activity) {
        AlertDialog.Builder(app)
            .setTitle("NFC is disabled")
            .setMessage("You must enable NFC to use this app.")
            .setPositiveButton(
                R.string.yes
            ) { dialog, which -> app.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            .setNegativeButton(
                R.string.no
            ) { dialog, which -> app.finish() }
            .setIcon(R.drawable.ic_dialog_alert)
            .show()
    }
}