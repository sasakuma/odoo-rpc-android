package com.serpentcs.odoorpc.authenticator

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.serpentcs.odoorpc.App
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.utils.getOdooUsers
import com.serpentcs.odoorpc.core.utils.recycler.decorators.VerticalLinearItemDecorator
import com.serpentcs.odoorpc.databinding.ActivityManageAccountBinding


class ManageAccountActivity : AppCompatActivity() {

    lateinit var app: App
    lateinit var binding: ActivityManageAccountBinding
    lateinit var adapter: ManageAccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as App
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_account)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            this@ManageAccountActivity.onBackPressed()
        }

        val users = getOdooUsers()
        val layoutManager = LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        )
        binding.rv.layoutManager = layoutManager
        binding.rv.addItemDecoration(VerticalLinearItemDecorator(
                resources.getDimensionPixelOffset(R.dimen.default_8dp)
        ))

        adapter = ManageAccountAdapter(this, ArrayList(users))
        binding.rv.adapter = adapter
    }
}
