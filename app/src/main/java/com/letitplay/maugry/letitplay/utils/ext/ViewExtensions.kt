package com.letitplay.maugry.letitplay.utils.ext

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber


fun View.show() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.hide() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

inline val RecyclerView.defaultItemAnimator
    get() = itemAnimator as DefaultItemAnimator

@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShiftMode() {
    val menuView = getChildAt(0) as BottomNavigationMenuView
    try {
        menuView.javaClass.getDeclaredField("mShiftingMode").also { shiftMode ->
            shiftMode.isAccessible = true
            shiftMode.setBoolean(menuView, false)
            shiftMode.isAccessible = false
        }
        for (i in 0 until menuView.childCount) {
            (menuView.getChildAt(i) as BottomNavigationItemView).also { item ->
                item.setShiftingMode(false)
                item.setChecked(item.itemData.isChecked)
            }
        }
    } catch (e: NoSuchFieldException) {
        Timber.e( e, "Unable to get shift mode field")
    } catch (e: IllegalAccessException) {
        Timber.e(e, "Unable to change value of shift mode")
    }
}

fun BottomNavigationView.active(position: Int) {
    menu.findItem(position).isChecked = true
}

fun TextView.updateText(text: CharSequence?) {
    if (this.text != text) {
        this.text = text
    }
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ViewGroup.inflateHolder(layoutId: Int): View =
        LayoutInflater.from(context).inflate(layoutId, this, false)

@Suppress("UNCHECKED_CAST")
fun <T> inflate(layoutId: Int, context: Context) = LayoutInflater.from(context).inflate(layoutId, null) as T

fun ImageView.loadImage(url: String?, context: Context? = null) {
    if (url != null) {
        Glide.with(context ?: this.context)
                .load(url)
                .into(this)
    } else {
        Glide.with(context)
                .clear(this)
    }
}

fun ImageView.loadCircularImage(url: String?, context: Context? = null) {
    if (url != null) {
        Glide.with(context ?: this.context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(this)
    } else {
        Glide.with(context)
                .clear(this)
    }
}