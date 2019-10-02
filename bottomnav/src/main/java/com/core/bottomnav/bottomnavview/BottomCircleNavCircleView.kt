package com.core.bottomnav.bottomnavview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.core.basicextensions.maskColor
import com.core.bottomnav.R

class BottomCircleNavCircleView : RelativeLayout, BottomCircleNavItem {


    constructor(context: Context?) : super(context) {
        init(null)
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


    lateinit var iconView: ImageView
    lateinit var viewBG: View

    var disabled = false
    var active = false
    var activeIcon: Drawable? = null
    var inactiveIcon: Drawable? = null
    var disabledIcon: Drawable? = null
    var iconWidth = context.resources.getDimension(R.dimen._30sdp)
    var iconHeight = context.resources.getDimension(R.dimen._60sdp)
    var bgColor = Color.RED
    var imgPadding = context.resources.getDimension(R.dimen._10sdp)
    var defPadding = context.resources.getDimension(R.dimen._10sdp)

    fun init(attrs: AttributeSet?) {
        active = false
        iconWidth = context.resources.getDimension(R.dimen._30sdp)
        iconHeight = context.resources.getDimension(R.dimen._40sdp)

        if (attrs != null) {
            val ta =
                context.obtainStyledAttributes(attrs, R.styleable.BottomCircleNavCircleView, 0, 0)
            try {
                activeIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavCircleView_ci_active_icon,
                        R.drawable.ic_nav_barcode_active
                    )
                )
                inactiveIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavCircleView_ci_inactive_icon,
                        R.drawable.ic_nav_barcode_disabled
                    )
                )
                disabledIcon = AppCompatResources.getDrawable(
                    context,
                    ta.getResourceId(
                        R.styleable.BottomCircleNavCircleView_ci_disabled_icon,
                        R.drawable.ic_nav_barcode_noactive
                    )
                )

                iconWidth =
                    ta.getDimension(R.styleable.BottomCircleNavCircleView_ci_iconWidth, iconWidth)
                iconHeight =
                    ta.getDimension(R.styleable.BottomCircleNavCircleView_ci_iconHeight, iconHeight)
                imgPadding =
                    ta.getDimension(R.styleable.BottomCircleNavCircleView_ci_imgPadding, imgPadding)
                defPadding =
                    ta.getDimension(R.styleable.BottomCircleNavCircleView_ci_padding, defPadding)
                bgColor = ta.getColor(
                    R.styleable.BottomCircleNavCircleView_ci_bgColor,
                    bgColor
                )
                active = ta.getBoolean(R.styleable.BottomCircleNavCircleView_ci_active, false)

            } finally {
                ta.recycle()
            }
        }

        createBubbleItemView(context)
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

        val lpIcon = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        lpIcon.addRule(CENTER_IN_PARENT, TRUE)

        iconView.layoutParams = lpIcon

        addView(iconView)
    }


    override fun setTypeface(typface: Typeface) {

    }

    override fun isActive(): Boolean {
        return active
    }

    override fun setOnItemClickListener(listener: OnClickListener) {
        setOnClickListener(listener)
    }

    override fun isDisabled(): Boolean {
        return disabled
    }

    override fun activate() {
        setParamsToViews(true, activeIcon)
    }


    override fun deactivate() {
        setParamsToViews(false, inactiveIcon)
    }

    override fun disable(enabled: Boolean) {
        setParamsToViews(false, inactiveIcon)
        isEnabled = enabled
        disabled = true
    }

    override fun enable() {
        setParamsToViews(false, inactiveIcon)
        isEnabled = true
        disabled = false
    }

    fun setParamsToViews(active: Boolean, icon: Drawable?) {
        this.active = active

        iconView.setImageDrawable(icon)
    }

    override fun setState(isActive: Boolean) {
        if (isActive) {
            activate()
        } else {
            deactivate()
        }
    }
}