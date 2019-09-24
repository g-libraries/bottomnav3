package com.core.bottomnav

import android.animation.ValueAnimator
import android.app.Activity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.core.basicextensions.applyGlobalLayoutListener
import com.core.bottomnav.bottomnavview.BottomCircleNavLayout
import com.core.bottomnav.bottomnavview.OnBottomNavClickListener

abstract class BottomNavigatorImpl constructor(var activity: Activity, var params: Params) :
    IBottomNavigator {

    data class Params(
        var navHostId: Int,
        var navViewId: Int,
        // Ids of views to disable/enable in various cases
        var navMenuIds: Map<Int, Int>, // Map<Nav Menu item id, Nav Menu Navigation action id>
        var noInternetIds: ArrayList<Int>,
        var notAuthIds: ArrayList<Int>,
        // Menu itemViews alpha
        var alphaEnabled: Int = 255,
        var alphaDisabled: Int = 70
    )

    protected var showNavStrategy =
        ShowNavStrategy { showInstant }
    private lateinit var navigationView: BottomCircleNavLayout
    private lateinit var navigationController: NavController

    private var navH: Int = 0

    private var guest: Boolean = false
    private var online: Boolean = true

    override fun attach(navListener: () -> Unit) {
        this.navigationController = activity.findNavController(params.navHostId)
        this.navigationView = activity.findViewById(params.navViewId)

        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(params.navHostId)
            ?.let {
                attachNavigationCallbacks(
                    it.childFragmentManager,
                    navigationController
                )
            }

        navigationView.bottomNavClickListener = object : OnBottomNavClickListener {
            override fun onClicked(pos: Int, id: Int) {
                if (guest && params.notAuthIds.contains(id)) {
                    navListener.invoke()
                } else {
                    params.navMenuIds[id]?.let {
                        navigationController.navigate(it)
                    }
                }
            }
        }

        navigationView.applyGlobalLayoutListener { navH = navigationView.height }
    }

    abstract fun attachNavigationCallbacks(
        fragmentManager: FragmentManager,
        navigationController: NavController
    )

    override fun hideNavView() {
        navigationView.visibility = View.GONE

    }

    override fun showNavView(delayed: Boolean) {
        if (!delayed) {
            navigationView.visibility = View.VISIBLE
        } else {
            navigationView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            val animator = ValueAnimator.ofFloat(0.01f, 1f)
            animator.apply {
                addUpdateListener { updatedAnimation ->
                    val newH = (navH.times(updatedAnimation.animatedValue as Float)).toInt()
                    if (updatedAnimation.animatedValue == 0.01f) {
                        navigationView.visibility = View.VISIBLE
                        navigationView.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                    navigationView.layoutParams.height = newH
                    navigationView.requestLayout()
                }
                start()
            }
        }
    }

    // Internet and Auth status change logic
    override fun internetChanged(online: Boolean) {
        this@BottomNavigatorImpl.online = online
        if (online) enableMenuItems() else disableMenuItems(params.noInternetIds, false)
    }

    override fun authorized(guest: Boolean) {
        this@BottomNavigatorImpl.guest = guest
        if (!guest) enableMenuItems() else disableMenuItems(params.notAuthIds)
    }

    protected fun selectMenuItem(index: Int) {
        if (!navigationView.navItems.isNullOrEmpty())
            navigationView.setCurrentActiveItem(index)
    }

    private fun enableMenuItems() {
        for (i: Int in 0 until navigationView.navItems.size) {
            navigationView.navItems[i].let {
                it.enable()
            }
        }

        if (guest) disableMenuItems(params.notAuthIds)
        if (!online) disableMenuItems(params.noInternetIds)
    }

    private fun disableMenuItems(list: ArrayList<Int>, enabled: Boolean = true) {
        for (id in list) {
            navigationView.navItems.find { it.getId() == id }?.let {
                it.disable(enabled)
            }
        }
    }

    private fun getById(menu: Menu, id: Int): MenuItem? {
        for (i in 0 until menu.size()) {
            if (menu[i].itemId == id)
                return menu[i]
        }
        return null
    }

    // Strategy for Navigation view show animation
    class ShowNavStrategy(private val navViewStrategy: () -> Unit) {
        fun apply() = navViewStrategy.invoke()
    }

    val showDelayed = {
        showNavView(true)
    }

    val showInstant = {
        showNavView(false)
    }

}