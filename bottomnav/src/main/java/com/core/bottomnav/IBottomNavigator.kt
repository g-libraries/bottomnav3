package com.core.bottomnav

import android.view.View
import androidx.annotation.IdRes


interface IBottomNavigator {

    fun attach(navListener: () -> Unit)
    fun hideNavView()
    fun showNavView(delayed: Boolean)
    fun internetChanged(online: Boolean)
    fun authorized(guest: Boolean)
    fun setBadgeToItem(@IdRes menuNavFragmentId: Int, amount: Int)
    fun setMenuItems(list: List<BottomNavItemData>)

}