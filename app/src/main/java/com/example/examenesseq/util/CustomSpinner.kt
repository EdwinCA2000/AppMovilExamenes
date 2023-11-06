package com.example.examenesseq.util

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner

class CustomSpinner : AppCompatSpinner {

    interface OnSpinnerEventsListener {
        fun onPopupWindowOpened(spinner: CustomSpinner)
        fun onPopupWindowClosed(spinner: CustomSpinner)
    }

    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    constructor(context: Context) : super(context)

    constructor(context: Context, mode: Int) : super(context, mode)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, mode: Int) : super(context, attrs, defStyleAttr, mode)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, mode: Int, popupTheme: Resources.Theme) : super(context, attrs, defStyleAttr, mode, popupTheme)

    override fun performClick(): Boolean {
        mOpenInitiated = true
        mListener?.onPopupWindowOpened(this)
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    fun setSpinnerEventsListener(onSpinnerEventsListener: OnSpinnerEventsListener) {
        mListener = onSpinnerEventsListener
    }

    fun performClosedEvent() {
        mOpenInitiated = false
        mListener?.onPopupWindowClosed(this)
    }

    fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }
}
