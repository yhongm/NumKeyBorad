package com.yhongm.numberkeyboard.view.inputview


import com.yhongm.numberkeyboard.view.inputview.xmlbean.SoftKey
import com.yhongm.numberkeyboard.view.inputview.xmlbean.SoftKeyboard

import android.content.Context
import android.graphics.*
import android.graphics.Paint.FontMetricsInt
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap


class SKBView : View {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var mSoftKeyboard: SoftKeyboard? = null


    private var mSoftKeyDown: SoftKey? = null

    private var mNormalKeyTextSize: Int = 0


    private var mVibrator: Vibrator? = null

    protected var mVibratePattern = longArrayOf(1, 20)

    private val mDirtyRect = Rect()

    private val mPaint: Paint

    private val mFmi: FontMetricsInt

    private var mListener: Listener? = null

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mFmi = mPaint.fontMetricsInt
    }

    fun setSoftKeyboard(softSkb: SoftKeyboard?): Boolean {

        if (null == softSkb) {
            return false
        }
        mSoftKeyboard = softSkb
        val bg = softSkb.skbBackground
        val bgColor = softSkb.mSkbBgColor
        if (null != bg) {
            setBackgroundDrawable(bg)
        } else {
            setBackgroundColor(bgColor)
        }
        return true
    }

    fun getSoftKeyboard(): SoftKeyboard? {
        return mSoftKeyboard
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measuredWidth = 0
        var measuredHeight = 0
        if (null != mSoftKeyboard) {
            measuredWidth = mSoftKeyboard!!.skbCoreWidth
            measuredHeight = mSoftKeyboard!!.skbCoreHeight

            measuredWidth += paddingLeft + paddingRight
            measuredHeight += paddingTop + paddingBottom

        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }


    fun onKeyPress(x: Int, y: Int): SoftKey? {
        mSoftKeyDown = mSoftKeyboard!!.mapToKey(x, y)
        if (mSoftKeyDown == null) {
            return null
        }
        tryVibrate()
        mSoftKeyDown!!.isPress = true
        mDirtyRect.union(
            mSoftKeyDown!!.mLeft, mSoftKeyDown!!.mTop,
            mSoftKeyDown!!.mRight, mSoftKeyDown!!.mBottom
        )
        invalidate(mDirtyRect)

        return mSoftKeyDown
    }

    fun onKeyRelease(x: Int, y: Int): SoftKey? {
        val mSoftKeyUp = mSoftKeyboard!!.mapToKey(x, y)
        if (null == mSoftKeyUp) {
            return null
        }
        mSoftKeyUp!!.isPress = false
        if (mSoftKeyDown!!.keyCode == mSoftKeyUp.keyCode) {
            if (mListener != null) {
                mListener?.clickKey(mSoftKeyUp.keyCode, mSoftKeyUp.keyText)
            }
        } else {
            mSoftKeyDown!!.isPress = false
        }

        mDirtyRect.union(
            mSoftKeyUp!!.mLeft, mSoftKeyUp!!.mTop,
            mSoftKeyUp!!.mRight, mSoftKeyUp!!.mBottom
        )
        invalidate(mDirtyRect)

        return mSoftKeyUp
    }


    private fun tryVibrate() {
        if (mVibrator == null) {
            mVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        mVibrator!!.vibrate(mVibratePattern, -1)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        val x = event.x.toInt()
        var y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onKeyPress(x, y)
            }

            MotionEvent.ACTION_UP -> {
                onKeyRelease(x, y)
            }

        }

        return true

    }

    override fun onDraw(canvas: Canvas) {
        Log.i("SoftKeyboardView", "14:15,onDraw :${mSoftKeyboard} ")// 2018/3/23,yhongm
        if (null == mSoftKeyboard) {
            return
        }

        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        val env = SKBEnvironment.instance
        mNormalKeyTextSize = env.getKeyTextSize()
        val rowNum = mSoftKeyboard!!.rowNum
        val keyXMargin = mSoftKeyboard!!.keyXMargin
        val keyYMargin = mSoftKeyboard!!.keyYMargin
        for (row in 0 until rowNum) {
            val keyRow = mSoftKeyboard!!.getKeyRowForDisplay(row) ?: continue
            val softKeys = keyRow.mSoftKeys
            val keyNum = softKeys!!.size
            for (i in 0 until keyNum) {
                val softKey = softKeys[i]
                mPaint.textSize = mNormalKeyTextSize.toFloat()
                drawSoftKey(canvas, softKey, keyXMargin, keyYMargin)
            }
        }

        mDirtyRect.setEmpty()
    }

    private fun drawSoftKey(
        canvas: Canvas, softKey: SoftKey, keyXMargin: Int,
        keyYMargin: Int
    ) {

        var textColor: Int = softKey.textColor
        if (textColor == SoftKey.INVALID_COLOR) {
            textColor = mSoftKeyboard!!.mSoftKeyColor
        }
        val bgImg = softKey.keyBgImg
        val bgColor = softKey.keyBgColor

        val keyRect = Rect(
            softKey.mLeft + keyXMargin, softKey.mTop + keyYMargin,
            softKey.mRight - keyXMargin, softKey.mBottom - keyYMargin
        )
        if (null != bgImg) {

            bgImg.bounds = keyRect

            bgImg.draw(canvas)
        } else if (bgColor != SoftKey.INVALID_COLOR) {
            val bitmap = Bitmap.createBitmap(
                softKey.width(), softKey.height(),
                Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(bgColor)
            canvas.drawBitmap(bitmap, keyRect, keyRect, Paint())

        }
        if (softKey.isPress) {
            if (softKey.pressColor != SoftKey.INVALID_COLOR || softKey.pressImg != null) {
                val pressColor = softKey.pressColor
                val pressImg = softKey.pressImg
                if (null != pressImg) {
                    pressImg.bounds = keyRect
                    pressImg.draw(canvas)
                } else if (null != pressColor) {
                    val bitmap = Bitmap.createBitmap(
                        softKey.width(), softKey.height(),
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.eraseColor(pressColor)
                    canvas.drawBitmap(bitmap, keyRect, keyRect, Paint())
                }
            }

        }

        val keyText = softKey.keyText
        val keyIcon = softKey.keyIcon
        if (null != keyIcon) {
            val marginLeft = (softKey.width() - keyIcon.intrinsicWidth) / 2
            val marginRight = (softKey.width() - keyIcon.intrinsicWidth
                    - marginLeft)
            val marginTop = (softKey.height() - keyIcon.intrinsicHeight) / 2
            val marginBottom = (softKey.height() - keyIcon.intrinsicHeight
                    - marginTop)
            keyIcon.setBounds(
                softKey.mLeft + marginLeft,
                softKey.mTop + marginTop, softKey.mRight - marginRight,
                softKey.mBottom - marginBottom
            )
            keyIcon.draw(canvas)
        } else if (null != keyText) {
            mPaint.color = textColor
            var textSize = softKey.textSize / 1f
            if (textSize === 0f) {
                textSize = mSoftKeyboard!!.getSoftKeySize()
            }
            mPaint.textSize = textSize

            val centerX = softKey.mLeft + (softKey.width() - mPaint.measureText(keyText)) / 2.0f
            val fontMetrics = mPaint.getFontMetricsInt()
            val baseLine =
                (softKey.mBottom + softKey.mTop - fontMetrics.bottom - fontMetrics.top) / 2f

            canvas.drawText(keyText, centerX, baseLine, mPaint)
        }
    }

    fun setKeyListener(l: Listener) {
        this.mListener = l
    }

    interface Listener {
        fun clickKey(keyCode: Int, keyText: String?)
    }
}
