package com.example.dogs.util

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dogs.R

val PERMISSION_SEND_SMS = 234
//function that display a circular spinner on the imageView while loading the image
fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

//Extension function for the imageView
fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {

    val options = RequestOptions()
            .placeholder(progressDrawable)
            .error(R.mipmap.ic_dogs)

    //this refers to imageView class
    Glide.with(context).setDefaultRequestOptions(options).load(uri).into(this)
}

@BindingAdapter("android:bindImage")
fun loadImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}