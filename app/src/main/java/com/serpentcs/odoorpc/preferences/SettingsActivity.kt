package com.serpentcs.odoorpc.preferences

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var app: App
    lateinit var binding: ActivitySettingsBinding

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
