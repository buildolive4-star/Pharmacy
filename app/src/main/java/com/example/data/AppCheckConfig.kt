package com.example.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

data class AppCheckDiagnostics(
    val isDebugBuild: Boolean,
    val providerName: String,
    val debugTokenUuid: String?,
    val tokenSource: String
)

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val intentToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
                ?.trim()
                ?.takeIf { it.isNotBlank() && !it.startsWith("YOUR_") }
            val resDebugToken = context.resources
                .getIdentifier("firebase_appcheck_debug_token", "string", context.packageName)
                .takeIf { it != 0 }
                ?.let { context.getString(it).trim() }
                ?.takeIf { it.isNotBlank() && !it.startsWith("YOUR_") }

            val debugToken = intentToken ?: resDebugToken
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                // Synchronous commit() is required before DebugAppCheckProviderFactory initializes.
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra or debug resource; SDK will use local debug UUID.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(
                RECAPTCHA_SITE_KEY_RES,
                "string",
                context.packageName
            )
            if (resId != 0) {
                val siteKey = context.getString(resId).trim()
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

/**
 * Reads the active App Check debug UUID stored by DebugAppCheckProviderFactory so users
 * running standalone debug APKs on physical devices can copy and register it in Firebase Console
 * without requiring adb/Logcat.
 */
fun readAppCheckDiagnostics(context: Context): AppCheckDiagnostics {
    if (!BuildConfig.DEBUG) {
        return AppCheckDiagnostics(
            isDebugBuild = false,
            providerName = "reCAPTCHA Enterprise",
            debugTokenUuid = null,
            tokenSource = "Release Attestation"
        )
    }
    return try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        val prefs = context.getSharedPreferences(
            "$DEBUG_STORE_PREFIX$persistenceKey",
            Context.MODE_PRIVATE
        )
        val storedUuid = prefs.getString(DEBUG_SECRET_KEY, null)?.trim()?.takeIf { it.isNotBlank() }
        val hasResToken = context.resources
            .getIdentifier("firebase_appcheck_debug_token", "string", context.packageName)
            .takeIf { it != 0 }
            ?.let { context.getString(it).trim().isNotBlank() }
            ?: false

        val source = when {
            hasResToken -> "CI / GitHub Secret"
            storedUuid != null -> "Device Generated (Register in Firebase Console)"
            else -> "Pending First AI Request"
        }
        AppCheckDiagnostics(
            isDebugBuild = true,
            providerName = "DebugAppCheckProvider",
            debugTokenUuid = storedUuid,
            tokenSource = source
        )
    } catch (e: Exception) {
        Log.w(TAG, "Unable to read App Check diagnostics", e)
        AppCheckDiagnostics(
            isDebugBuild = true,
            providerName = "DebugAppCheckProvider",
            debugTokenUuid = null,
            tokenSource = "Unavailable"
        )
    }
}

fun refreshAppCheckDebugToken(context: Context, onComplete: (AppCheckDiagnostics) -> Unit) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        FirebaseAppCheck.getInstance()
            .getAppCheckToken(false)
            .addOnCompleteListener {
                onComplete(readAppCheckDiagnostics(context))
            }
    } catch (_: Exception) {
        onComplete(readAppCheckDiagnostics(context))
    }
}

fun isAppCheckErrorMessage(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val lower = message.lowercase()
    return lower.contains("app check") ||
        lower.contains("appcheck") ||
        lower.contains("attestation") ||
        lower.contains("token is invalid") ||
        lower.contains("403")
}
