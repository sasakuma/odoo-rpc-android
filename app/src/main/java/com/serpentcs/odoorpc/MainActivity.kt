package com.serpentcs.odoorpc

import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import com.bumptech.glide.Glide
import com.serpentcs.odoorpc.authenticator.ProfileActivity
import com.serpentcs.odoorpc.core.Odoo
import com.serpentcs.odoorpc.core.utils.*
import com.serpentcs.odoorpc.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = "MainActivity"

        private val ACTION_DASHBOARD = 0
    }

    private lateinit var app: App
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navHeader: NavHeaderViewHolder

    private var currentDrawerItemID: Int = 0
    private var drawerClickStatus: Boolean = false

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.tb)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.tb.setNavigationOnClickListener {
            binding.dl.openDrawer(GravityCompat.START)
        }
        setTitle(R.string.app_name)

        drawerToggle = ActionBarDrawerToggle(
                this, binding.dl, binding.tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.dl.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val view = binding.nv.getHeaderView(0)
        if (view != null) {
            navHeader = NavHeaderViewHolder(view)
            val user = getActiveOdooUser()
            if (user != null) {
                navHeader.name.text = user.name
                navHeader.email.text = user.login
                if (user.imageSmall != "false") {
                    val byteArray = Base64.decode(user.imageSmall, Base64.DEFAULT)
                    Glide.with(this)
                            .load(byteArray)
                            .asBitmap()
                            .into(navHeader.pic)
                } else {
                    Glide.with(this)
                            .load(app.getLetterTile(user.name))
                            .asBitmap()
                            .into(navHeader.pic)
                }
            }
        }

        drawerClickStatus = false

        navHeader.menuToggle.setOnClickListener {
            val menu = binding.nv.menu
            if (drawerClickStatus) {
                menu.setGroupVisible(R.id.nav_menu_1, true)
                menu.setGroupVisible(R.id.nav_menu_2, true)
                menu.setGroupVisible(R.id.nav_menu_3, true)
                menu.setGroupVisible(R.id.nav_menu_4, false)
                navHeader.menuToggleImage.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp)
            } else {
                menu.setGroupVisible(R.id.nav_menu_1, false)
                menu.setGroupVisible(R.id.nav_menu_2, false)
                menu.setGroupVisible(R.id.nav_menu_3, false)
                menu.setGroupVisible(R.id.nav_menu_4, true)
                navHeader.menuToggleImage.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp)
            }
            drawerClickStatus = !drawerClickStatus
        }

        binding.nv.setNavigationItemSelectedListener { item ->
            binding.dl.post { binding.dl.closeDrawer(GravityCompat.START) }
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    if (currentDrawerItemID != ACTION_DASHBOARD) {

                    }
                    true
                }
                R.id.nav_profile -> {
                    if (getActiveOdooUser() != null) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    } else {
                        showMessage(message = getString(R.string.error_active_user))
                    }
                    true
                }
                R.id.nav_settings -> {
                    true
                }
                R.id.nav_add_account -> {
                    true
                }
                R.id.nav_manage_account -> {
                    true
                }
                else -> {
                    true
                }
            }
        }

        Odoo.searchRead("res.partner") { searchRead ->
            if (searchRead.isSuccessful) {
                logD(TAG, searchRead.result.records.toString())
            } else {
                logE(TAG, searchRead.errorMessage)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }
}
