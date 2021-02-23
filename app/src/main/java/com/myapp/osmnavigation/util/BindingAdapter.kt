package com.myapp.osmnavigation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("add_icon")
fun ImageView.setIcon(icon: String){
    let {
        Glide.with(it.context).load(icon).into(it)
    }
}