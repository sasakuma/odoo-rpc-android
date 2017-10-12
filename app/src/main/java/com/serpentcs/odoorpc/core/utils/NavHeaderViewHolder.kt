package com.serpentcs.odoorpc.core.utils

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.serpentcs.odoorpc.R
import de.hdodenhof.circleimageview.CircleImageView

class NavHeaderViewHolder(view: View) {
    val pic: CircleImageView = view.findViewById(R.id.userImage)
    val name: TextView = view.findViewById(R.id.header_name)
    val email: TextView = view.findViewById(R.id.header_details)
    val menuToggle: LinearLayout = view.findViewById(R.id.drawer_layout_dropdown)
    val menuToggleImage: ImageView = view.findViewById(R.id.ivDropdown)
}
