package com.unclekong.ebookdemo

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ScrollView

class MyScrollView : ScrollView {
    var gestureDetector: GestureDetector? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return gestureDetector!!.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(ev)
        super.dispatchTouchEvent(ev)
        return true
    }
}