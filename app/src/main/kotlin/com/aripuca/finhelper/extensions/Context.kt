package com.aripuca.finhelper.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat

fun Context.getVersionName(): String {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return packageInfo.versionName ?: ""
}

fun Context.launchEmail(emailAddress: String, subject: String, body: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:") // only email apps should handle this

    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, body)

    startActivity(intent, Bundle())
}