package com.core.bottomnav.bottomnavview

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import timber.log.Timber
import java.util.ArrayList

class BottomCircleNavLayout : ConstraintLayout, View.OnClickListener {

    //constants
    private val TAG = "BNLView"
    private val MIN_ITEMS = 2
    private val MAX_ITEMS = 5

    var navItems: ArrayList<BottomCircleNavItem> = arrayListOf()

    var bottomNavClickListener: OnBottomNavClickListener? = null
    var bottomNavCreatedListener: OnBottomNavCreated? = null

    private var currentActiveItemPosition = 0
    private var loadPreviousState: Boolean = false

    private var currentTypeface: Typeface? = null

    constructor(context: Context?) : super(context) {
        init(context!!, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context!!, attrs)
    }

    /**
     * Constructors
     */

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("current_item", currentActiveItemPosition)
        bundle.putBoolean("load_prev_state", true)
        return bundle
    }

    override fun onRestoreInstanceState(state1: Parcelable?) {
        var state = state1
        if (state is Bundle) {
            val bundle = state as Bundle?
            currentActiveItemPosition = bundle!!.getInt("current_item")
            loadPreviousState = bundle.getBoolean("load_prev_state")
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    /////////////////////////////////////////
    // PRIVATE METHODS
    /////////////////////////////////////////

    /**
     * Initialize
     *
     * @param context current context
     * @param attrs   custom attributes
     */
    private fun init(context: Context, attrs: AttributeSet?) {

        post { updateChildNavItems() }
    }

    /**
     * Finds Child Elements of type [BottomCircleNavItem] and adds them to [.navItems]
     */
    private fun updateChildNavItems() {
        navItems = ArrayList()
        for (index in 0 until childCount) {
            val view = getChildAt(index)
            if (view is BottomCircleNavItem)
                navItems.add(view)
            else {
                Timber.w("Cannot have child navItems other than BottomCircleNavItem")
            }
        }

        if (navItems.size < MIN_ITEMS) {
            Timber.w("The navItems list should have at least 2 navItems of BottomCircleNavItem")
        } else if (navItems.size > MAX_ITEMS) {
            Timber.w("The navItems list should not have more than 5 navItems of BottomCircleNavItem")
        }

        setClickListenerForItems()
        setInitialActiveState()

        //update the typeface
        if (currentTypeface != null)
            setTypeface(currentTypeface!!)

        bottomNavCreatedListener?.onCreated()
    }

    /**
     * Makes sure that ONLY ONE child [.navItems] is active
     */
    private fun setInitialActiveState() {
        var foundActiveElement = false

        // find the initial state
        if (!loadPreviousState) {
            for (i in navItems.indices) {
                if (navItems[i].isActive() && !foundActiveElement) {
                    foundActiveElement = true
                    currentActiveItemPosition = i
                } else {
                    navItems[i].setState(false)
                }
            }
        } else {
            for (i in navItems.indices) {
                navItems[i].setState(false)
            }
        }
        //set the active element
        if (!foundActiveElement)
            navItems[currentActiveItemPosition].setState(true)
    }

    /**
     * Sets [OnClickListener] for the child views
     */
    private fun setClickListenerForItems() {
        for (btv in navItems)
            btv.setOnItemClickListener(this)
    }

    /**
     * Gets the Position of the Child from [.navItems] from its id
     *
     * @param id of view to be searched
     * @return position of the Item
     */
    private fun getItemPositionById(id: Int): Int {
        for (i in navItems.indices)
            if (id == navItems[i].getId())
                return i
        return -1
    }

    ///////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////

    /**
     * Set the [Typeface] for the Text Elements of the View
     *
     * @param typeface to be used
     */
    fun setTypeface(typeface: Typeface) {
        for (btv in navItems)
            btv.setTypeface(typeface)

    }

    /**
     * Gets the current active position
     *
     * @return active item position
     */
    fun getCurrentActiveItemPosition(): Int {
        return currentActiveItemPosition
    }

    /**
     * Sets the current active item
     *
     * @param position current position change
     */
    fun setCurrentActiveItem(position: Int) {
        if (navItems[position].isDisabled()) return

        if (position < 0 || position >= navItems.size)
            return

        for (item in navItems) {
            if (item.isActive()) {
                item.deactivate()
            }
        }

        navItems[position].activate()
    }

    override fun onClick(v: View) {
        val changedPosition = getItemPositionById(v.id)
        if (changedPosition >= 0) {
            if(bottomNavClickListener?.onClicked(currentActiveItemPosition, v.id)!!){
                setCurrentActiveItem(changedPosition)

                //changed the current active position
                currentActiveItemPosition = changedPosition
            }
        } else {
            Timber.w("Selected id not found! Cannot toggle")
        }
    }
}