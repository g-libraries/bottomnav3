package com.core.bottomnav.bottomnavview

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.core.bottomnav.BottomNavItemData
import timber.log.Timber
import java.util.ArrayList
import androidx.constraintlayout.widget.ConstraintSet
import android.R
import android.text.method.TextKeyListener.clear
import android.R.layout
import android.view.ViewGroup
import com.core.base.util.toDp
import kotlin.random.Random


class BottomCircleNavLayout : ConstraintLayout, View.OnClickListener {

    //constants
    private val TAG = "BNLView"
    private val MIN_ITEMS = 2
    private val MAX_ITEMS = 5

    var navItems: ArrayList<BottomCircleNavItem> = arrayListOf()
    var navItemsViews: ArrayList<View> = arrayListOf()

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

    fun init(dataItemList: List<BottomNavItemData>) {
        setItemList(dataItemList)
        setConstraints()
    }

    /**
     * Set nav items
     */

    fun setItemList(dataItemList: List<BottomNavItemData>) {
        val list = createViewsFromDataObjects(dataItemList) as ArrayList<View>
        this.navItemsViews = list

        for (view in navItemsViews) {
            view.id = View.generateViewId()

            this.addView(view)
        }
    }

    fun createViewsFromDataObjects(dataItemList: List<BottomNavItemData>): ArrayList<BottomCircleNavItem> {
        val viewsList = arrayListOf<BottomCircleNavItem>()
        for (itemData in dataItemList)
            if (itemData.isCircle)
                viewsList.add(createCircleView(itemData))
            else
                viewsList.add(createDefaultView(itemData))

        return viewsList
    }

    fun createDefaultView(itemData: BottomNavItemData): BottomCircleNavItemView {
        val itemView = BottomCircleNavItemView(context)
        itemView.init(
            itemData
        )

        itemView.layoutParams = setZeroWidth()

        return itemView
    }

    fun createCircleView(itemData: BottomNavItemData): BottomCircleNavCircleView {
        val itemView = BottomCircleNavCircleView(context)
        itemView.init(itemData)

        itemView.layoutParams = setZeroWidth()

        return itemView
    }

    fun setZeroWidth(): ViewGroup.LayoutParams {
        val params = ViewGroup.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return params
    }

    /**
     * Finds Child Elements of type [BottomCircleNavItem] and adds them to [.navItems]
     */
    private fun updateChildNavItems() {
        navItems = ArrayList()
        for (index in 0 until childCount) {
            val view = getChildAt(index)
            if (view is BottomCircleNavItem) {
                navItemsViews.add(view)
                navItems.add(view)
            } else {
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
        currentActiveItemPosition = position

    }

    override fun onClick(v: View) {
        val changedPosition = getItemPositionById(v.id)
        if (changedPosition >= 0) {
            if (bottomNavClickListener?.onClicked(changedPosition, v.id)!!) {
                setCurrentActiveItem(changedPosition)
            }
        } else {
            Timber.w("Selected id not found! Cannot toggle")
        }
    }

    fun setConstraints() {
        for ((index, navItem) in navItemsViews.withIndex()) {
            val set = ConstraintSet()
            set.clone(this)

            when (index) {
                0 -> {
                    set.addToHorizontalChainRTL(
                        navItem.id,
                        ConstraintSet.PARENT_ID,
                        navItemsViews[index + 1].id
                    )
                    set.setMargin(navItem.id, ConstraintSet.START, 24.toDp)
                }
                navItemsViews.size - 1 -> {

                    set.addToHorizontalChainRTL(
                        navItem.id,
                        navItemsViews[index - 1].id,
                        ConstraintSet.PARENT_ID
                    )

                    set.setMargin(navItem.id, ConstraintSet.END, 24.toDp)
                }
                else -> {
                    set.addToHorizontalChainRTL(
                        navItem.id,
                        navItemsViews[index - 1].id,
                        navItemsViews[index + 1].id
                    )
                }
            }

            if (navItem is BottomCircleNavCircleView) {
                set.connect(navItem.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
                set.setHorizontalWeight(navItem.id, 1.3f)
            } else {
                set.connect(navItem.id, ConstraintSet.TOP, getChildAt(0).id, ConstraintSet.TOP)

                set.setHorizontalWeight(navItem.id, 1f)

                set.connect(
                    navItem.id,
                    ConstraintSet.BOTTOM,
                    getChildAt(0).id,
                    ConstraintSet.BOTTOM
                )
            }

            set.applyTo(this)
        }
    }
}