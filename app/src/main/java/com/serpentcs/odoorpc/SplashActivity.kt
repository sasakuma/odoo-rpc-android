package com.serpentcs.odoorpc

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.serpentcs.odoorpc.core.Odoo
import com.serpentcs.odoorpc.core.utils.getActiveOdooUser

class SplashActivity : AppCompatActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val user = getActiveOdooUser()
        if (user != null) {
            Odoo.user = user
            startMainActivity()
        } else {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    private fun startMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
