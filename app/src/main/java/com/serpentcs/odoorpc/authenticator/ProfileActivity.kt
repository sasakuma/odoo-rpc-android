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
            binding.ctl.title = user.name
            binding.tvName.text = user.name
            binding.tvLogin.text = user.login
            binding.tvServerURL.text = user.host
            binding.tvDatabase.text = user.database
            binding.tvVersion.text = user.serverVersion
            binding.tvTimezone.text = user.context["tz"].asString
        }
    }
}
