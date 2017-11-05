package com.serpentcs.odoorpc.authenticator

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.utils.getOdooUsers
import com.serpentcs.odoorpc.databinding.ActivityManageAccountBinding

class ManageAccountActivity : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivityManageAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_account)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val users = getOdooUsers()
    }
}
