package com.myapp.osmnavigation.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import org.osmdroid.bonuspack.location.POI

@BindingAdapter("add_icon")
fun ImageView.setIcon(icon: String) {
    let {
        Glide.with(it.context).load(icon).into(it)
    }
}

@BindingAdapter("visible")
fun ImageView.setVisibleSearch(pois: ArrayList<POI>) {
    let {
        if (pois.isNotEmpty()) it.visibility = View.VISIBLE
    }
}

@BindingAdapter("set_image")
fun ImageView.setImagePOI(poiPathc: String?) {
    let {
        if (poiPathc != null) Glide.with(it.context).load(poiPathc).into(it)
    }
}

@BindingAdapter("set_text")
fun TextView.setTextPOI(textPOI: String?){
    let {
       text = textPOI ?:""
    }
}