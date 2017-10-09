package com.serpentcs.odoorpc

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.serpentcs.odoorpc.core.utils.getActiveOdooUser
import com.serpentcs.odoorpc.core.utils.logD
import com.serpentcs.odoorpc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = "MainActivity"
    }

    private lateinit var app: App
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.tb)

        logD(TAG, getActiveOdooUser().toString())
    }
}
