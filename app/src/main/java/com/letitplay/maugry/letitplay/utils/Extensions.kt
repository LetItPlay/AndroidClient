package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/*
*       GROUP INFLATION
* */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ViewGroup.inflateHolder(layoutId: Int): View =
        LayoutInflater.from(context).inflate(layoutId, this, false)

@Suppress("UNCHECKED_CAST")
fun <T> inflate(layoutId: Int, context: Context) = LayoutInflater.from(context).inflate(layoutId, null) as T
