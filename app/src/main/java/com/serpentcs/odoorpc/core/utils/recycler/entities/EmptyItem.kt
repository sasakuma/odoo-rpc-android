package com.serpentcs.odoorpc.core.utils.recycler.entities

import android.support.annotation.DrawableRes

data class EmptyItem(
        val message: String,
        @DrawableRes
        val drawableResId: Int
)