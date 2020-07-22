package com.core.bottomnav

import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.core.base.util.applyGlobalLayoutListener
import com.core.base.util.getVisibleFragment
import com.core.bottomnav.bottomnavview.BottomCircleNavLayout
import com.core.bottomnav.bottomnavview.OnBottomNavClickListener
import java.lang.reflect.Type

abstract class BottomNavigatorImpl constructor(var activity: Activity, var params: Params) :
    IBottomNavigator {

    data class Params(
        var navHostId: Int,
        var navViewId: Int,
        //View for creating overlay effect
        var fakeNavViewId: Int,
        // Ids of views to disable/enable in various cases
        var menuItems: List<BottomNavItemData>,
        // Menu itemViews alpha
        var alphaEnabled: Int = 255,
        var alphaDisabled: Int = 70
    )

    protected var showNavStrategy =
        ShowNavStrategy { showInstant }
    private lateinit var navigationView: BottomCircleNavLayout
    private lateinit var fakeNavView: View
    private lateinit var navigationController: NavController

    private var navH: Int = 0

    private var guest: Boolean = false
    private var online: Boolean = true

    override fun attach(navListener: () -> Unit) {
        this.navigationController = activity.findNavController(params.navHostId)
        this.navigationView = activity.findViewById(params.navViewId)
        this.fakeNavView = activity.findViewById(params.fakeNavViewId)

        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(params.navHostId)
            ?.let {
                attachNavigationCallbacks(
                    it.childFragmentManager,
                    navigationController
                )
            }

        navigationView.bottomNavClickListener = object : OnBottomNavClickListener {
            override fun onClicked(pos: Int, id: Int): Boolean {
                if (pos == navigationView.getCurrentActiveItemPosition()) return false
                return if (guest && !params.menuItems.find { it.menuItemId == id }?.noAuthAvailable!!) {
                    navListener.invoke()
                    false
                } else {
                    params.menuItems.find { it.menuItemId == id }?.let {
                        navigationController.navigate(it.menuActionId)
                    }
                    true
                }
            }
        }

        navigationView.applyGlobalLayoutListener { navH = navigationView.height }
    }

    fun attachNavigationCallbacks(
        fragmentManager: FragmentManager,
        navigationController: NavController
    ) {
        fragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)
                fragmentManager.getVisibleFragment()?.let {
                    for (item in params.menuItems.filterIndexed { _, item -> item.showNavBoolean }) {
                        if (item.fragmentType == it::class.java) {
                            showNavStrategy =
                                ShowNavStrategy(
                                    showInstant
                                )

                            return
                        }
                    }


                    showNavStrategy =
                        ShowNavStrategy(
                            showInstant
                        ) // Can set strategy to showDelayed for extra effects
                    hideNavView()
                }
            }


            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentViewDestroyed(fm, f)
                fragmentManager.getVisibleFragment()?.let {
                    for (item in params.menuItems.filterIndexed { _, item -> item.showNavBoolean }) {
                        if (item.fragmentType == it::class.java) {
                            showNavStrategy.apply()

                            return
                        }
                    }

                    showNavStrategy = ShowNavStrategy(showInstant); hideNavView()
                }

            }
        }, true)

        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination is FragmentNavigator.Destination)
                for ((index, item) in params.menuItems.withIndex())
                    if (item.fragmentType.javaClass.kotlin.qualifiedName == destination.className) selectMenuItem(
                        index
                    )
        }
    }

    override fun hideNavView() {
        navigationView.visibility = View.GONE
        fakeNavView.visibility = View.GONE
    }

    override fun showNavView(delayed: Boolean) {
        if (!delayed) {
            navigationView.visibility = View.VISIBLE
            fakeNavView.visibility = View.VISIBLE
        } else {
            navigationView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            val animator = ValueAnimator.ofFloat(0.01f, 1f)
            animator.apply {
                addUpdateListener { updatedAnimation ->
                    val newH = (navH.times(updatedAnimation.animatedValue as Float)).toInt()
                    if (updatedAnimation.animatedValue == 0.01f) {
                        navigationView.visibility = View.VISIBLE
                        fakeNavView.visibility = View.VISIBLE
                        navigationView.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                    navigationView.layoutParams.height = newH
                    navigationView.requestLayout()
                }
                start()
            }
        }
    }

    override fun setMenuItems(list: List<BottomNavItemData>) {
        params.menuItems = list
    }

    // Internet and Auth status change logic
    override fun internetChanged(online: Boolean) {
        this@BottomNavigatorImpl.online = online
        if (online) enableMenuItems() else disableMenuItems(
            params.menuItems.filterIndexed { _, item -> !item.noInternetAvailable },
            false
        )
    }

    override fun authorized(guest: Boolean) {
        this@BottomNavigatorImpl.guest = guest
        if (!guest) enableMenuItems() else disableMenuItems(params.menuItems.filterIndexed { _, item -> !item.noAuthAvailable })
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

        if (guest) disableMenuItems(params.menuItems.filterIndexed { _, item -> !item.noAuthAvailable })
        if (!online) disableMenuItems(
            params.menuItems.filterIndexed { _, item -> !item.noInternetAvailable },
            false
        )
    }

    private fun disableMenuItems(list: List<BottomNavItemData>, enabled: Boolean = true) {
        for (item in list) {
            navigationView.navItems.find { it.getId() == item.menuItemId }?.let {
                it.disable(enabled)
            }
        }
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