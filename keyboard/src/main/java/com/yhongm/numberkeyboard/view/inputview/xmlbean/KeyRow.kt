package com.yhongm.numberkeyboard.view.inputview.xmlbean

class KeyRow {

    var mSoftKeys: MutableList<SoftKey>? = null
    var mRowId: Int = 0
    var mTopF: Float = 0.toFloat()
    var mBottomF: Float = 0.toFloat()
    var mTop: Int = 0
    var mBottom: Int = 0

    companion object {

        val ALWAYS_SHOW_ROW_ID = -1
        val DEFAULT_ROW_ID = 0
    }
}
