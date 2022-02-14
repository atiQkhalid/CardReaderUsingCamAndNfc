package com.example.Native.extenssions

import android.view.View
import android.widget.Toast
import com.example.Native.App

/**
 * Extension function to show toast message
 */
fun Any.showToastMsg(message: String) {
    Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show()
}

/**
 * An Extension to make view Gone
 * @return void
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * An Extension to make view Visible
 * @return void
 */
fun View.visible() {
    visibility = View.VISIBLE
}