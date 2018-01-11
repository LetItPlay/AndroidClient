package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL

/*
*       GROUP INFLATION
* */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ViewGroup.inflateHolder(layoutId: Int): View =
        LayoutInflater.from(context).inflate(layoutId, this, false)

@Suppress("UNCHECKED_CAST")
fun <T> inflate(layoutId: Int, context: Context) = LayoutInflater.from(context).inflate(layoutId, null) as T

fun ImageView.loadImage(context: Context, url: String?, prefix: String = GL_MEDIA_SERVICE_URL) {
    if (url != null) {
        Glide.with(context)
                .load("$prefix$url")
                .into(this)
    }
}