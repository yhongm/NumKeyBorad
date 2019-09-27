package com.yhongm.numberkeyboard.view.inputview.xmlbean

import android.graphics.drawable.Drawable

open class SoftKey {

    open var keyIcon: Drawable? = null

    open var keyBgColor: Int = INVALID_COLOR

    open var keyBgImg: Drawable? = null

    open var keyText: String? = null

    open var keyCode: Int = 0

    open var pressColor: Int = INVALID_COLOR
    open var pressImg: Drawable? = null
    open var isPress: Boolean = false
    var mLeftF: Float = 0.toFloat()
    var mRightF: Float = 0.toFloat()
    var mTopF: Float = 0.toFloat()
    var mBottomF: Float = 0.toFloat()
    var mLeft: Int = 0
    var mRight: Int = 0
    var mTop: Int = 0
    var mBottom: Int = 0


    var textColor: Int = INVALID_COLOR
        set(value) {
            field = value
        }
        get() = field
    var textSize: Float = 0f
        set(value) {
            field = value
        }
        get() = field

    fun setKeyDimensions(left: Float, top: Float, right: Float,
                         bottom: Float) {
        mLeftF = left
        mTopF = top
        mRightF = right
        mBottomF = bottom
    }


    fun setKeyAttribute(keyCode: Int, label: String?, keyIcon: Drawable?) {
        this.keyCode = keyCode
        this.keyText = label
        this.keyIcon = keyIcon
    }

    fun setSkbCoreSize(skbWidth: Int, skbHeight: Int) {
        mLeft = (mLeftF * skbWidth).toInt()
        mRight = (mRightF * skbWidth).toInt()
        mTop = (mTopF * skbHeight).toInt()
        mBottom = (mBottomF * skbHeight).toInt()
    }

    fun width(): Int {
        return mRight - mLeft
    }

    fun height(): Int {
        return mBottom - mTop
    }

    companion object {
        val INVALID_COLOR: Int = -0x88888
    }
}
