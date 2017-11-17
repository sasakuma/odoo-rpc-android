package com.serpentcs.odoorpc.authenticator

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.serpentcs.odoorpc.R
import com.serpentcs.odoorpc.core.Odoo
import com.serpentcs.odoorpc.core.OdooUser
import com.serpentcs.odoorpc.core.utils.*
import com.serpentcs.odoorpc.core.utils.recycler.RecyclerBaseAdapter
import com.serpentcs.odoorpc.databinding.ItemViewManageAccountBinding

class ManageAccountAdapter(
        private val activity: ManageAccountActivity,
        items: ArrayList<Any>
) : RecyclerBaseAdapter(items, activity.binding.rv) {

    companion object {
        @JvmField
        val TAG: String = "ManageAccountAdapter"

        private val VIEW_TYPE_ITEM = 0
    }

    private val rowItems: ArrayList<OdooUser> = ArrayList(
            items.filterIsInstance<OdooUser>()
    )

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(parent!!.context)
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = DataBindingUtil.inflate<ItemViewManageAccountBinding>(
                        inflater,
                        R.layout.item_view_manage_account,
                        parent,
                        false
                )
                return ManageAccountViewHolder(binding)
            }
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    @SuppressLint("StaticFieldLeak")
    override fun onBindViewHolder(baseHolder: RecyclerView.ViewHolder?, basePosition: Int) {
        super.onBindViewHolder(baseHolder, basePosition)
        if (baseHolder != null) {
            val position = baseHolder.adapterPosition
            when (getItemViewType(basePosition)) {
                VIEW_TYPE_ITEM -> {
                    val holder = baseHolder as ManageAccountViewHolder
                    val binding = holder.binding
                    val item = items[position] as OdooUser

                    val loginDrawable = ContextCompat
                            .getDrawable(activity, R.drawable.ic_done_all_white_24dp)
                    val logoutDrawable = ContextCompat
                            .getDrawable(activity, R.drawable.ic_exit_to_app_white_24dp)
                    val deleteDrawable = ContextCompat
                            .getDrawable(activity, R.drawable.ic_close_white_24dp)

                    binding.civLogin.setImageDrawable(loginDrawable)
                    binding.civLogout.setImageDrawable(logoutDrawable)
                    binding.civDelete.setImageDrawable(deleteDrawable)

                    if (item.imageSmall != "false") {
                        val byteArray = Base64.decode(item.imageSmall, Base64.DEFAULT)
                        Glide.with(activity)
                                .load(byteArray)
                                .asBitmap()
                                .into(binding.civImage)
                    } else {
                        Glide.with(activity)
                                .load(activity.app.getLetterTile(item.name))
                                .asBitmap()
                                .into(binding.civImage)
                    }

                    binding.tvName.text = item.name
                    binding.tvHost.text = item.host

                    val activeUser = activity.getActiveOdooUser()
                    if (activeUser != null && item == activeUser) {
                        binding.civLogin.visibility = View.GONE
                        binding.civLogout.visibility = View.VISIBLE
                    } else {
                        binding.civLogin.visibility = View.VISIBLE
                        binding.civLogout.visibility = View.GONE
                    }

                    binding.civLogin.setOnClickListener {
                        val clickedPosition = baseHolder.adapterPosition
                        val clickedItem = items[clickedPosition] as OdooUser
                        Odoo.user = clickedItem
                        Odoo.authenticate(
                                clickedItem.login, clickedItem.password,
                                clickedItem.database, true
                        ) { authenticate ->
                            if (authenticate.isSuccessful) {
                                object : AsyncTask<OdooUser, Unit, OdooUser?>() {
                                    override fun doInBackground(vararg params: OdooUser): OdooUser? =
                                            activity.loginOdooUser(params[0])

                                    override fun onPostExecute(result: OdooUser?) {
                                        super.onPostExecute(result)
                                        if (result != null) {
                                            TaskStackBuilder.create(activity)
                                                    .addNextIntent(Intent(
                                                            activity, SplashActivity::class.java
                                                    )).startActivities()
                                        } else {
                                            activity.closeApp()
                                        }
                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clickedItem)
                            } else {
                                activity.showMessage(message = authenticate.errorMessage)
                            }
                        }
                    }

                    binding.civLogout.setOnClickListener {
                        val clickedPosition = baseHolder.adapterPosition
                        val clickedItem = items[clickedPosition] as OdooUser
                        object : AsyncTask<OdooUser, Unit, Unit>() {
                            override fun doInBackground(vararg params: OdooUser) =
                                    activity.logoutOdooUser(params[0])

                            override fun onPostExecute(result: Unit) {
                                super.onPostExecute(result)
                                TaskStackBuilder.create(activity)
                                        .addNextIntent(Intent(
                                                activity, LoginActivity::class.java
                                        )).addNextIntent(Intent(
                                        activity, ManageAccountActivity::class.java
                                )).startActivities()
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clickedItem)
                    }

                    binding.civDelete.setOnClickListener {
                        val clickedPosition = baseHolder.adapterPosition
                        val clickedItem = items[clickedPosition] as OdooUser
                        val clickedActiveUser = activity.getActiveOdooUser()
                        object : AsyncTask<OdooUser, Unit, Boolean>() {
                            override fun doInBackground(vararg params: OdooUser): Boolean =
                                    activity.deleteOdooUser(params[0])

                            override fun onPostExecute(result: Boolean) {
                                super.onPostExecute(result)
                                if (result) {
                                    removeRow(clickedPosition)
                                    if ((clickedActiveUser != null && clickedItem == clickedActiveUser)) {
                                        TaskStackBuilder.create(activity)
                                                .addNextIntent(Intent(
                                                        activity, SplashActivity::class.java
                                                )).startActivities()
                                    }
                                } else {
                                    activity.showMessage(message = activity.getString(
                                            R.string.manage_account_remove_error
                                    ))
                                }
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clickedItem)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val o = items[position]
        if (o is OdooUser) {
            return VIEW_TYPE_ITEM
        }
        return super.getItemViewType(position)
    }

    fun removeRow(position: Int) {
        @Suppress("UnnecessaryVariable")
        val start = position
        val count = itemCount - 1 - position
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(start, count)
        updateRowItems()
    }

    private fun updateRowItems() {
        updateSearchItems()
        rowItems.clear()
        rowItems.addAll(ArrayList(
                items.filterIsInstance<OdooUser>()))
    }

    override fun clear() {
        rowItems.clear()
        super.clear()
    }
}