package com.core.bottomnav.bottomnavview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.core.bottomnav.R

class BottomCircleNavItemView : RelativeLayout, BottomCircleNavItem {


    constructor(context: Context?) : super(context) {
        init(attrs = null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    var disabled = false
    var active = false

    private var title: String? = "Title"

    var activeIcon: Drawable? = null
    var inactiveIcon: Drawable? = null
    var disabledIcon: Drawable? = null
    var textColorActive = ContextCompat.getColor(context, R.color.textColorActive)
    var textColorInactive = ContextCompat.getColor(context, R.color.textColorInactive)
    var textColorDisable = ContextCompat.getColor(context, R.color.textColorDisabled)
    var titleSize = context.resources.getDimension(R.dimen._9sdp)
    var iconWidth = context.resources.getDimension(R.dimen._30sdp)
    var iconHeight = context.resources.getDimension(R.dimen._40sdp)
    var internalPadding =
        context.resources.getDimension(R.dimen._5sdp).toInt()
    var titlePadding =
        context.resources.getDimension(R.dimen._5sdp).toInt()
    var leftPadding =
        context.resources.getDimension(R.dimen._5sdp).toInt()
    var rightPadding =
        context.resources.getDimension(R.dimen._5sdp).toInt()

    lateinit var iconView: ImageView
    lateinit var titleView: TextView

    fun init(title: String, textColorActive: String, textColorInactive: String, textColorDisable: String) {
        this.title = title
        this.textColorActive = Color.parseColor(textColorActive)
        this.textColorInactive = Color.parseColor(textColorInactive)
        this.textColorDisable = Color.parseColor(textColorDisable)

        gravity = Gravity.CENTER

        createBubbleItemView(context)
        setState(false)
    }

    fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val ta =
                context.obtainStyledAttributes(attrs, R.styleable.BottomCircleNavItemView, 0, 0)
            try {
                activeIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavItemView_bt_active_icon,
                        R.drawable.ic_nav_main_active
                    )
                )
                inactiveIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavItemView_bt_inactive_icon,
                        R.drawable.ic_nav_main_disabled
                    )
                )
                disabledIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavItemView_bt_disabled_icon,
                        R.drawable.ic_nav_main_noactive
                    )
                )

                iconWidth =
                    ta.getDimension(R.styleable.BottomCircleNavItemView_bt_iconWidth, iconWidth)
                iconHeight =
                    ta.getDimension(R.styleable.BottomCircleNavItemView_bt_iconHeight, iconHeight)
                title = ta.getString(R.styleable.BottomCircleNavItemView_bt_title)
                titleSize =
                    ta.getDimension(R.styleable.BottomCircleNavItemView_bt_titleSize, titleSize)
                textColorActive =
                    ta.getColor(
                        R.styleable.BottomCircleNavItemView_bt_textColorActive,
                        textColorActive
                    )
                textColorInactive = ta.getColor(
                    R.styleable.BottomCircleNavItemView_bt_textColorInactive,
                    textColorInactive
                )
                textColorDisable = ta.getColor(
                    R.styleable.BottomCircleNavItemView_bt_textColorDisable,
                    textColorDisable
                )
                active = ta.getBoolean(R.styleable.BottomCircleNavItemView_bt_active, false)
                titlePadding = ta.getDimension(
                    R.styleable.BottomCircleNavItemView_bt_titlePadding,
                    titlePadding.toFloat()
                ).toInt()
            } finally {
                ta.recycle()
            }
        }

        gravity = Gravity.CENTER

        createBubbleItemView(context)
        setState(false)
    }

    /**
     * Create the components of the bubble item view [.iconView] and [.titleView]
     *
     * @param context current context
     */
    private fun createBubbleItemView(context: Context) {

        //create the nav icon
        iconView = ImageView(context)
        iconView.id = ViewCompat.generateViewId()
        iconView.setImageDrawable(inactiveIcon)

        //create the nav title
        titleView = TextView(context)
        val lpTitle = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        val lpIcon = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        lpIcon.addRule(CENTER_HORIZONTAL, TRUE)

        lpTitle.addRule(CENTER_HORIZONTAL, TRUE)
        lpTitle.addRule(BELOW, iconView.id)

        titleView.layoutParams = lpTitle
        iconView.layoutParams = lpIcon

        titleView.setPadding(0, titlePadding, 0, 0)
        titleView.isSingleLine = true
        titleView.setTextColor(textColorActive)
        titleView.text = title
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        //get the current measured title width
        titleView.visibility = View.VISIBLE

        addView(iconView)
        addView(titleView)


        //set the initial state
        setState(active)
    }

    /**
     * Updates the Initial State
     *
     * @param isActive current state
     */
    override fun setState(isActive: Boolean) {
        if (isActive) {
            activate()
        } else {
            deactivate()
        }
    }

    /**
     * Set Active state
     */
    override fun activate() {
        setParamsToViews(true, activeIcon, textColorActive)
    }

    /**
     * Set Inactive State
     */
    override fun deactivate() {
        setParamsToViews(false, inactiveIcon, textColorInactive)
    }

    /**
     * Set Disabled State
     */
    override fun disable(enabled: Boolean) {
        setParamsToViews(false, disabledIcon, textColorDisable)
        isEnabled = enabled
        disabled = true
    }

    /**
     * Set Enabled State
     */
    override fun enable() {
        if (!isActive()) {
            setParamsToViews(false, inactiveIcon, textColorInactive)
            disabled = false
            isEnabled = true
        }
    }

    fun setParamsToViews(active: Boolean, icon: Drawable?, textColor: Int) {
        this.active = active

        iconView.setImageDrawable(icon)
        titleView.setTextColor(textColor)
    }

    override fun isDisabled(): Boolean {
        return disabled
    }

    override fun setTypeface(typface: Typeface) {
        titleView.typeface = typface
    }

    override fun isActive() = active

    override fun setOnItemClickListener(listener: OnClickListener) {
        setOnClickListener(listener)
    }

    override fun setTitle(title: String) {
        this.title = title
    }
}