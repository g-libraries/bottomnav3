package com.core.bottomnav.bottomnavview

import android.graphics.Typeface
import android.view.View

interface BottomCircleNavItem {
    fun activate()
    fun deactivate()
    fun disable(enabled: Boolean)
    fun enable()
    fun getId(): Int
    fun isActive(): Boolean
    fun setAmountOfNotifications(amount: Int)
    fun setOnItemClickListener(listener: View.OnClickListener)
    fun setState(isActive: Boolean)
    fun setTypeface(typface: Typeface)
    fun setTitle(title: String)
    fun isDisabled(): Boolean
}