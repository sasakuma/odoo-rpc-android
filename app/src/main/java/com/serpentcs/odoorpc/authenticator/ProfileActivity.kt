package com.serpentcs.odoorpc.authenticator

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import com.bumptech.glide.Glide
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.utils.getActiveOdooUser
import com.serpentcs.odoorpc.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivityProfileBinding

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = getActiveOdooUser()
        if (user != null) {
            binding.user = user
            if (user.imageSmall != "false") {
                val byteArray = Base64.decode(user.imageSmall, Base64.DEFAULT)
                Glide.with(this)
                        .load(byteArray)
                        .asBitmap()
                        .into(binding.ivProfile)
            } else {
                Glide.with(this)
                        .load(app.getLetterTile(user.name))
                        .asBitmap()
                        .into(binding.ivProfile)
            }
        }
    }
}
