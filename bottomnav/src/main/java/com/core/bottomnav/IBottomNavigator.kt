package com.core.bottomnav

import android.view.View


interface IBottomNavigator {

    fun attach(navListener: () -> Unit)
    fun hideNavView()
    fun showNavView(delayed: Boolean)
    fun internetChanged(online: Boolean)
    fun authorized(guest: Boolean)
    fun setMenuItems(list: List<BottomNavItemData<Any>>)

}