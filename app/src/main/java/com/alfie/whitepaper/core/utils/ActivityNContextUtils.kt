package com.alfie.whitepaper.core.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity

internal fun activityChooser(uri: Uri?) = Intent.createChooser(Intent().apply {
    type = "image/*"
    action = Intent.ACTION_VIEW
    data = uri
}, "Select Gallery App")


fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}