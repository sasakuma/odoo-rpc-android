package com.serpentcs.odoorpc.core.utils

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.support.v13.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.BuildConfig
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.Odoo
import com.serpentcs.odoorpc.core.OdooUser
import com.serpentcs.odoorpc.core.entities.authenticate.AuthenticateResult

typealias OdooList = com.serpentcs.odoorpc.core.entities.list.List

fun Context.createOdooUser(authenticateResult: AuthenticateResult): Boolean {
    val accountManager = AccountManager.get(this)
    val account = Account(authenticateResult.androidName, App.KEY_ACCOUNT_TYPE)
    val result = accountManager.addAccountExplicitly(
            account,
            authenticateResult.password,
            authenticateResult.toBundle
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        accountManager.notifyAccountAuthenticated(account)
    }
    return result
}

fun Context.getOdooUsers(): List<OdooUser> {
    val manager = AccountManager.get(this)
    val odooUsers = ArrayList<OdooUser>()
    manager.getAccountsByType(App.KEY_ACCOUNT_TYPE)
            .map {
                Odoo.fromAccount(manager, it)
            }
            .forEach { odooUsers += it }
    return odooUsers.toList()
}

fun Context.odooUserByAndroidName(androidName: String): OdooUser? {
    getOdooUsers()
            .filter { it.androidName == androidName }
            .forEach { return it }
    return null
}

fun Context.getActiveOdooUser(): OdooUser? {
    getOdooUsers()
            .filter { it.isActive }
            .forEach { return it }
    return null
}

fun Context.loginOdooUser(odooUser: OdooUser): OdooUser {
    do {
        val user = getActiveOdooUser()
        if (user != null) {
            logout(user)
        }
    } while (user != null)
    val accountManager = AccountManager.get(this)
    accountManager.setUserData(odooUser.account, "active", "true")

    return odooUserByAndroidName(odooUser.androidName)!!
}

fun Context.logout(odooUser: OdooUser) {
    val accountManager = AccountManager.get(this)
    accountManager.setUserData(odooUser.account, "active", "false")
}

fun AppCompatActivity.hideSoftKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

var alertDialog: AlertDialog? = null

fun AppCompatActivity.showMessage(
        title: String? = null,
        message: String,
        cancelable: Boolean = false,
        positiveButton: String = getString(R.string.ok),
        positiveButtonListener: (dialog: DialogInterface?, which: Int) -> Unit = { _, _ -> }
): AlertDialog {
    alertDialog?.dismiss()
    alertDialog = AlertDialog.Builder(this, R.style.AppAlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(positiveButton) { dialog: DialogInterface?, which: Int -> positiveButtonListener(dialog, which) }
            .show()
    return alertDialog!!
}

fun AppCompatActivity.showExitMessage(message: String) {
    showMessage(getString(R.string.fatal_error), message, false, getString(R.string.exit)) { _, _ ->
        ActivityCompat.finishAffinity(this)
    }
}

fun logV(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.v(tag, msg)
} else -1

fun logV(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.v(tag, msg, tr)
} else -1

fun logD(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.d(tag, msg)
} else -1

fun logD(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.d(tag, msg, tr)
} else -1

fun logI(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.i(tag, msg)
} else -1

fun logI(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.i(tag, msg, tr)
} else -1

fun logW(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.w(tag, msg)
} else -1

fun logW(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.w(tag, msg, tr)
} else -1

fun logE(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.e(tag, msg)
} else -1

fun logE(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.e(tag, msg, tr)
} else -1

fun logWTF(tag: String, msg: String): Int = if (BuildConfig.DEBUG) {
    Log.wtf(tag, msg)
} else -1

fun logWTF(tag: String, msg: String, tr: Throwable): Int = if (BuildConfig.DEBUG) {
    Log.wtf(tag, msg, tr)
} else -1

fun String.toJsonObject(): JsonObject
        = Gson().fromJson(this, JsonElement::class.java).asJsonObject
