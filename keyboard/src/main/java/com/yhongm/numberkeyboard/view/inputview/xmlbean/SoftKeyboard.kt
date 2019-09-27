package com.yhongm.numberkeyboard.view.inputview.xmlbean

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import com.yhongm.numberkeyboard.view.inputview.SKBEnvironment


import java.util.ArrayList

class SoftKeyboard(
        val skbXmlId: Int,
        private val skbWidth: Int,
        skbHeight: Int) {
    var skbCoreWidth: Int = 0
        private set
    var skbCoreHeight: Int = 0
        private set

    private var mEnabledRowId: Int = 0

    private var mKeyRows: MutableList<KeyRow>? = null

    var mSkbBg: Drawable? = null

    open var mSkbBgColor: Int = SoftKey.INVALID_COLOR
        set(value) {
            field = value
        }
        get() = field
    private var mSoftKeySize: Float = 0f

    open var mSoftKeyColor: Int = SoftKey.INVALID_COLOR
        set(value) {
            field = value
        }
        get() = field

    private var mKeyXMargin = 0f

    private var mKeyYMargin = 0f

    private val mTmpRect = Rect()

    val skbTotalWidth: Int
        get() {
            val padding = padding
            return skbCoreWidth + padding.left + padding.right
        }

    val skbTotalHeight: Int
        get() {
            val padding = padding
            return skbCoreHeight + padding.top + padding.bottom
        }

    val keyXMargin: Int
        get() {
            val env = SKBEnvironment.instance
            return (mKeyXMargin * skbCoreWidth.toFloat() * env.keyXMarginFactor).toInt()
        }

    val keyYMargin: Int
        get() {
            val env = SKBEnvironment.instance
            return (mKeyYMargin * skbCoreHeight.toFloat() * env.keyYMarginFactor).toInt()
        }

    var skbBackground: Drawable?
        get() = if (null != mSkbBg) {
            mSkbBg
        } else mSkbBg
        set(skbBg) {
            mSkbBg = skbBg
        }


    val rowNum: Int
        get() = if (null != mKeyRows) {
            mKeyRows!!.size
        } else 0

    private val padding: Rect
        get() {
            mTmpRect.set(0, 0, 0, 0)
            val skbBg = skbBackground ?: return mTmpRect
            skbBg.getPadding(mTmpRect)
            return mTmpRect
        }

    init {
        skbCoreWidth = skbWidth
        skbCoreHeight = skbHeight
    }


    fun setKeyMargins(xMargin: Float, yMargin: Float) {
        mKeyXMargin = xMargin
        mKeyYMargin = yMargin
    }

    fun reset() {
        if (null != mKeyRows) {
            mKeyRows!!.clear()
        }
    }

    fun beginNewRow(rowId: Int, yStartingPos: Float) {
        if (null == mKeyRows) mKeyRows = ArrayList()
        val keyRow = KeyRow()
        keyRow.mRowId = rowId
        keyRow.mTopF = yStartingPos
        keyRow.mBottomF = yStartingPos
        keyRow.mSoftKeys = ArrayList()
        Log.i("SoftKeyboard", "17:29,beginNewRow keyRow:$keyRow ")// 2017/12/28,yhongm
        mKeyRows!!.add(keyRow)
    }

    fun addSoftKey(softKey: SoftKey): Boolean {
        if (mKeyRows!!.size == 0) {
            return false
        }
        val keyRow = mKeyRows!![mKeyRows!!.size - 1] ?: return false
        val softKeys = keyRow.mSoftKeys

        softKey.setSkbCoreSize(skbCoreWidth, skbCoreHeight)
        softKeys!!.add(softKey)
        if (softKey.mTopF < keyRow.mTopF) {
            keyRow.mTopF = softKey.mTopF
        }
        if (softKey.mBottomF > keyRow.mBottomF) {
            keyRow.mBottomF = softKey.mBottomF
        }
        Log.i("SoftKeyboard", "16:27,addSoftKey softKey:$softKey ")// 2018/1/2,yhongm

        return true
    }

    fun setSkbCoreSize(skbCoreWidth: Int, skbCoreHeight: Int) {
        if (null == mKeyRows || skbCoreWidth == this.skbCoreWidth && skbCoreHeight == this.skbCoreHeight) {
            return
        }
        for (row in mKeyRows!!.indices) {
            val keyRow = mKeyRows!![row]
            keyRow.mBottom = (skbCoreHeight * keyRow.mBottomF).toInt()
            keyRow.mTop = (skbCoreHeight * keyRow.mTopF).toInt()

            val softKeys = keyRow.mSoftKeys
            for (i in softKeys!!.indices) {
                val softKey = softKeys[i]
                softKey.setSkbCoreSize(skbCoreWidth, skbCoreHeight)
            }
        }
        this.skbCoreWidth = skbCoreWidth
        this.skbCoreHeight = skbCoreHeight
    }

    fun getKeyRowForDisplay(row: Int): KeyRow? {
        if (null != mKeyRows && mKeyRows!!.size > row) {
            val keyRow = mKeyRows!![row]
            if (KeyRow.ALWAYS_SHOW_ROW_ID == keyRow.mRowId || keyRow.mRowId == mEnabledRowId) {
                return keyRow
            }
        }
        return null
    }

    fun getKey(row: Int, location: Int): SoftKey? {
        if (null != mKeyRows && mKeyRows!!.size > row) {
            val softKeys = mKeyRows!![row].mSoftKeys
            if (softKeys!!.size > location) {
                return softKeys[location]
            }
        }
        return null
    }

    fun mapToKey(x: Int, y: Int): SoftKey? {
        if (null == mKeyRows) {
            return null
        }
        val rowNum = mKeyRows!!.size
        for (row in 0 until rowNum) {
            val keyRow = mKeyRows!![row]
            if (KeyRow.ALWAYS_SHOW_ROW_ID != keyRow.mRowId && keyRow.mRowId != mEnabledRowId) {
                continue
            }
            if (keyRow.mTop > y && keyRow.mBottom <= y) {
                continue
            }

            val softKeys = keyRow.mSoftKeys
            val keyNum = softKeys!!.size
            for (i in 0 until keyNum) {
                val sKey = softKeys[i]
                if (sKey.mLeft <= x && sKey.mTop <= y && sKey.mRight > x
                        && sKey.mBottom > y) {
                    return sKey
                }
            }
        }

        var nearestKey: SoftKey? = null
        var nearestDis = java.lang.Float.MAX_VALUE
        for (row in 0 until rowNum) {
            val keyRow = mKeyRows!![row]
            if (KeyRow.ALWAYS_SHOW_ROW_ID != keyRow.mRowId && keyRow.mRowId != mEnabledRowId) {
                continue
            }
            if (keyRow.mTop > y && keyRow.mBottom <= y) {
                continue
            }

            val softKeys = keyRow.mSoftKeys
            val keyNum = softKeys!!.size
            for (i in 0 until keyNum) {
                val sKey = softKeys[i]
                val disx = (sKey.mLeft + sKey.mRight) / 2 - x
                val disy = (sKey.mTop + sKey.mBottom) / 2 - y
                val dis = (disx * disx + disy * disy).toFloat()
                if (dis < nearestDis) {
                    nearestDis = dis
                    nearestKey = sKey
                }
            }
        }
        return nearestKey
    }




    fun setSoftKeyConfig(softKeySize: Float, softKeyColor: Int) {
        this.mSoftKeySize = softKeySize
        this.mSoftKeyColor = softKeyColor
    }

    fun getSoftKeySize(): Float {
        return mSoftKeySize
    }

    fun getSoftKeyColor(): Int {
        return mSoftKeyColor
    }

}
