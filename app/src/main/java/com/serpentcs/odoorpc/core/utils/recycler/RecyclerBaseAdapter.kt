package com.serpentcs.odoorpc.core.utils.recycler

import android.support.v7.widget.RecyclerView
import android.widget.Filterable

abstract class RecyclerBaseAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    companion object {
        @JvmField
        val TAG = "RecyclerBaseAdapter"
    }
}