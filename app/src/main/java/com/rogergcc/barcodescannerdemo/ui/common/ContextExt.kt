package com.rogergcc.barcodescannerdemo.ui.common

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

/**
 * Created on noviembre.
 * year 2023 .
 */


fun Context.getCompatColor(@ColorRes colorId: Int) =
    ResourcesCompat.getColor(resources, colorId, null)

fun Context.getCompatDrawable(@DrawableRes drawableId: Int) =
    AppCompatResources.getDrawable(this, drawableId)!!


fun Context.applyLanguage(locale: Locale?): Context {
    val configuration = resources.configuration
    val currentLocale = ConfigurationCompat.getLocales(configuration)[0]
    val newLocale = locale ?: ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]

    return if (newLocale == currentLocale) {
        this
    } else {
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        createConfigurationContext(configuration)
    }
}


fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
    this?.let { Toast.makeText(it, textId, duration).show() }



fun Context.hasPermission(vararg permissions: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        permissions.all { singlePermission ->
            ContextCompat.checkSelfPermission(
                this,
                singlePermission
            ) == PackageManager.PERMISSION_GRANTED
        }
    else true
}



fun Activity.requestPermission(vararg permissions: String, @IntRange(from = 0) requestCode: Int) =
    ActivityCompat.requestPermissions(this, permissions, requestCode)

fun Fragment.requestPermission(vararg permissions: String, @IntRange(from = 0) requestCode: Int) =
    requestPermissions(permissions, requestCode)


fun Context.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent?) -> Unit,
): BroadcastReceiver {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            onReceive(intent)
        }
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    return receiver
}


fun Context.isInstalledOnExternalStorage() = try {
    (applicationInfo.flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) ==
            ApplicationInfo.FLAG_EXTERNAL_STORAGE
} catch (e: PackageManager.NameNotFoundException) {
    false
}