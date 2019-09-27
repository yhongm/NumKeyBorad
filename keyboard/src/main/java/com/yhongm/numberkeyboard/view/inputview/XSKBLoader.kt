package com.yhongm.numberkeyboard.view.inputview

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.Log
import com.yhongm.numberkeyboard.view.inputview.xmlbean.KeyRow
import com.yhongm.numberkeyboard.view.inputview.xmlbean.SoftKey
import com.yhongm.numberkeyboard.view.inputview.xmlbean.SoftKeyboard

import java.io.IOException
import java.util.regex.Pattern

import org.xmlpull.v1.XmlPullParserException

class XSKBLoader(private val mContext: Context?) {

    private val mResources: Resources

    private var mXmlEventType: Int = 0

    internal var mKeyXPos: Float = 0.toFloat()

    internal var mKeyYPos: Float = 0.toFloat()

    internal var mSkbWidth: Int = 0
    internal var mSkbHeight: Int = 0

    internal var mKeyXMargin = 0f

    internal var mKeyYMargin = 0f

    internal var mNextEventFetched = false

    internal var mAttrTmp: String? = null

    internal inner class KeyCommonAttributes(var mXrp: XmlResourceParser) {
        var keyWidth: Float = 0.toFloat()
        var keyHeight: Float = 0.toFloat()
        var pressColor: Int = SoftKey.INVALID_COLOR
        var pressImg: Drawable? = null
        //确保默认的对象不是空的
        // Make sure the default object is not null.
        fun getAttributes(defAttr: KeyCommonAttributes): Boolean {
            keyWidth = getFloat(mXrp, XMLATTR_KEY_WIDTH, defAttr.keyWidth)
            keyHeight = getFloat(mXrp, XMLATTR_KEY_HEIGHT, defAttr.keyHeight)
            pressColor = getColor(mXrp, XMLATTR_KEY_PRESS_COLOR, defAttr.pressColor)
            pressImg = getDrawable(mXrp, XMLATTR_KEY_PRESS_IMG, defAttr.pressImg)
            return if (keyWidth <= 0 || keyHeight <= 0) {
                false
            } else true
        }


    }

    init {
        mResources = mContext!!.resources
    }

    fun loadSKB(resourceId: Int, skbWidth: Int, skbHeight: Int): SoftKeyboard? {
        if (null == mContext) {
            return null
        }
        val xrp = mContext.resources.getXml(resourceId)
        var softKeyboard: SoftKeyboard? = null
        var softKey: SoftKey? = null
        val attrDef = KeyCommonAttributes(xrp)
        val attrSkb = KeyCommonAttributes(xrp)
        val attrRow = KeyCommonAttributes(xrp)
        val attrKeys = KeyCommonAttributes(xrp)
        val attrKey = KeyCommonAttributes(xrp)

        mKeyXPos = 0f
        mKeyYPos = 0f
        mSkbWidth = skbWidth
        mSkbHeight = skbHeight

        try {
            mKeyXMargin = 0f
            mKeyYMargin = 0f
            mXmlEventType = xrp.next()
            while (mXmlEventType != XmlResourceParser.END_DOCUMENT) {
                mNextEventFetched = false
                if (mXmlEventType == XmlResourceParser.START_TAG) {
                    var attr = xrp.name
                    if (XMLTAG_KEYBOARD.compareTo(attr) == 0) {
                        attrSkb.getAttributes(attrDef)
                        softKeyboard = SoftKeyboard(
                            resourceId,
                            mSkbWidth, mSkbHeight
                        )
                        softKeyboard.setKeyMargins(mKeyXMargin, mKeyYMargin)
                        val softBgColor =
                            getColor(xrp, XMLATTR_SOFT_BG_COLOR, SoftKey.INVALID_COLOR)
                        val softBgImg = getDrawable(xrp, XMLATTR_SOFT_BG_IMG, null)
                        softKeyboard.skbBackground = softBgImg
                        softKeyboard.mSkbBgColor = softBgColor
                        val softKeySize = getFloat(xrp, XMLATTR_SOFT_KET_TEXT_SIZE, 0f)
                        val softKeyColor =
                            getColor(xrp, XMLATTR_SOFT_KET_TEXT_COLOR, SoftKey.INVALID_COLOR)
                        softKeyboard.setSoftKeyConfig(softKeySize, softKeyColor)
                    } else if (XMLTAG_ROW.compareTo(attr) == 0) {
                        attrRow.getAttributes(attrSkb)
                        mKeyXPos = getFloat(xrp, XMLATTR_START_POS_X, 0f)
                        mKeyYPos = getFloat(xrp, XMLATTR_START_POS_Y, mKeyYPos)
                        val rowId = getInteger(
                            xrp, XMLATTR_ROW_ID,
                            KeyRow.ALWAYS_SHOW_ROW_ID
                        )
                        softKeyboard!!.beginNewRow(rowId, mKeyYPos)
                    } else if (XMLTAG_KEYS.compareTo(attr) == 0) {
                        if (null == softKeyboard) {
                            return null
                        }
                        attrKeys.getAttributes(attrRow)

                        var splitter: String? = xrp.getAttributeValue(
                            null,
                            XMLATTR_KEY_SPLITTER
                        )
                        splitter = Pattern.quote(splitter)
                        val texts = xrp.getAttributeValue(
                            null,
                            XMLATTR_KEY_TEXTS
                        )
                        val codes = xrp.getAttributeValue(
                            null,
                            XMLATTR_KEY_CODES
                        )
                        val keyBgColor = getColor(xrp, XMLATTR_KEYS_BG_COLOR, SoftKey.INVALID_COLOR)
                        val keyBgImg = getDrawable(xrp, XMLATTR_KEYS_BG_IMG, null)
                        val keyTextColor =
                            getColor(xrp, XMLATTR_KEYS_TEXT_COLOR, SoftKey.INVALID_COLOR)
                        val keyTextSize = getFloat(xrp, XMLATTR_KEYS_TEXT_SIZE, 0f)
                        Log.i(
                            "XSKBLoader",
                            "23:29,loadSKB texts:$texts ,splitter:$splitter "
                        )// 2018/3/24,yhongm
                        if (null == splitter || null == texts) {
                            return null
                        }

                        val textArr = texts.split(splitter.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()

                        var codeArr: Array<String>? = null
                        if (null != codes) {
                            codeArr = codes.split(splitter.toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            Log.i(
                                "XSKBLoader",
                                "23:35,loadSKB textSize:${textArr.size},codeSize${codeArr.size} "
                            )// 2018/3/24,yhongm
                            if (textArr.size != codeArr.size) {
                                return null
                            }
                        }


                        for (i in textArr.indices) {
                            softKey = SoftKey()
                            var keyCode = 0
                            softKey.textColor = keyTextColor
                            softKey.textSize = keyTextSize
                            softKey.keyBgImg = keyBgImg
                            softKey.keyBgColor = keyBgColor
                            softKey.pressColor = attrKeys.pressColor
                            softKey.pressImg = attrKeys.pressImg

                            if (null != codeArr) {
                                keyCode = Integer.valueOf(codeArr[i])!!
                            }
                            softKey.setKeyAttribute(keyCode, textArr[i], null)
                            val left: Float
                            val right: Float
                            val top: Float
                            val bottom: Float
                            left = mKeyXPos

                            right = left + attrKeys.keyWidth
                            top = mKeyYPos
                            bottom = top + attrKeys.keyHeight

                            if (right - left < 2 * mKeyXMargin) {
//                                return null
                            }
                            if (bottom - top < 2 * mKeyYMargin) {
//                                return null
                            }

                            softKey.setKeyDimensions(left, top, right, bottom)
                            softKeyboard.addSoftKey(softKey)
                            mKeyXPos = right
                            if (mKeyXPos.toInt() * mSkbWidth > mSkbWidth) {
//                                return null
                            }
                        }
                    } else if (XMLTAG_KEY.compareTo(attr) == 0) {
                        if (null == softKeyboard) {
                            return null
                        }
                        if (!attrKey.getAttributes(attrRow)) {
                            return null
                        }

                        softKey = getSoftKey(xrp, attrKey)
                        if (null == softKey) {
//                            return null
                        }
                        // Update the key position for the key.更新这个按键的位置对于按键
                        //更新下一个按键的位置
                        // Update the position for next key.
                        mKeyXPos = softKey!!.mRightF
                        if (mXmlEventType == XmlResourceParser.START_TAG) {
                            attr = xrp.name
                            if (XMLTAG_ROW.compareTo(attr) == 0) {
                                mKeyYPos += attrRow.keyHeight
                                Log.i(
                                    "XSKBLoader",
                                    "17:51,loadSKB mKeyYPos:$mKeyYPos "
                                )// 2018/3/23,yhongm
                                if (mKeyYPos.toInt() * mSkbHeight > mSkbHeight) {
                                    return null
                                }
                            }
                        }
                        softKeyboard.addSoftKey(softKey)
                    }
                } else if (mXmlEventType == XmlResourceParser.END_TAG) {
                    val attr = xrp.name
                    if (XMLTAG_ROW.compareTo(attr) == 0) {
                        mKeyYPos += attrRow.keyHeight
                        if (mKeyYPos.toInt() * mSkbHeight > mSkbHeight) {
//                            return null
                        }
                    }
                }

                // Get the next tag.获取接下来的标签
                if (!mNextEventFetched) {
                    mXmlEventType = xrp.next()
                }
            }
            xrp.close()
            Log.i("XSKBLoader", "16:49,loadSKB : $mSkbWidth ,$mSkbHeight ")// 2018/3/23,yhongm
            softKeyboard!!.setSkbCoreSize(mSkbWidth, mSkbHeight)
            return softKeyboard
        } catch (e: XmlPullParserException) {
        } catch (e: IOException) {

        }

        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun getSoftKey(
        xrp: XmlResourceParser,
        attrKey: KeyCommonAttributes
    ): SoftKey? {
        val keyCode = getInteger(xrp, XMLATTR_KEY_CODE, 0)
        val keyText = getString(xrp, XMLATTR_KEY_TEXT, null)
        var keyIcon = getDrawable(xrp, XMLATTR_KEY_ICON, null)
        val textColor = getColor(xrp, XMLATTR_KEY_TEXT_COLOR, SoftKey.INVALID_COLOR)
        val textSize = getFloat(xrp, XMLATTR_KET_TEXT_SIZE, 0f)
        var keyBgColor = getColor(xrp, XMLATTR_KET_BG_COLOR, SoftKey.INVALID_COLOR)
        var keyBgImg = getDrawable(xrp, XMLATTR_KET_BG_IMG, null)
        val left: Float
        val right: Float
        val top: Float
        val bottom: Float
        left = mKeyXPos
        right = left + attrKey.keyWidth
        top = mKeyYPos
        bottom = top + attrKey.keyHeight



        if (right - left < 2 * mKeyXMargin) {
            return null
        }
        if (bottom - top < 2 * mKeyYMargin) {
            return null
        }

        mXmlEventType = xrp.next()
        mNextEventFetched = true

        val softKey: SoftKey
        if (mXmlEventType == XmlResourceParser.START_TAG) {
            mAttrTmp = xrp.name
        }

        softKey = SoftKey()
        softKey.setKeyAttribute(keyCode, keyText, keyIcon)
        softKey.textColor = textColor
        softKey.textSize = textSize
        softKey.keyBgColor = keyBgColor
        softKey.keyBgImg = keyBgImg
        if (attrKey.pressColor != SoftKey.INVALID_COLOR) {
            softKey.pressColor = attrKey.pressColor
        }
        if (attrKey.pressImg != null) {
            softKey.pressImg = attrKey.pressImg
        }
        softKey.setKeyDimensions(left, top, right, bottom)
        return softKey
    }


    private fun getInteger(xrp: XmlResourceParser, name: String, defValue: Int): Int {
        val resId = xrp.getAttributeResourceValue(null, name, 0)

        val s: String?
        if (resId == 0) {
            s = xrp.getAttributeValue(null, name)
            if (null == s) {
                return defValue
            }
            try {
                return Integer.valueOf(s)!!
            } catch (e: NumberFormatException) {
                return defValue
            }

        } else {
            return Integer.parseInt(mContext!!.resources.getString(resId))
        }
    }

    private fun getColor(xrp: XmlResourceParser, name: String, defValue: Int): Int {
        val resId = xrp.getAttributeResourceValue(null, name, 0)
        val s: String?
        if (resId == 0) {
            s = xrp.getAttributeValue(null, name)
            if (null == s) {
                return defValue
            }
            try {
                return Integer.valueOf(s)!!
            } catch (e: NumberFormatException) {
                return defValue
            }

        } else {
            return mContext!!.resources.getColor(resId)
        }
    }

    private fun getString(xrp: XmlResourceParser, name: String, defValue: String?): String? {
        val resId = xrp.getAttributeResourceValue(null, name, 0)
        return if (resId == 0) {
            xrp.getAttributeValue(null, name)
        } else {
            mContext!!.resources.getString(resId)
        }
    }

    private fun getFloat(xrp: XmlResourceParser, name: String, defValue: Float): Float {
        val resId = xrp.getAttributeResourceValue(null, name, 0)
        if (resId == 0) {
            val s = xrp.getAttributeValue(null, name) ?: return defValue
            try {
                val ret: Float
                if (s.endsWith("%p")) {
                    ret = java.lang.Float.parseFloat(s.substring(0, s.length - 2)) / 100
                } else if (s.endsWith("%sp")) {
                    val sp = Integer.valueOf((s.substring(0, s.length - 3))) / 1f
                    ret = sp2px(sp) / 1f

                } else if (s.endsWith("%dp")) {
                    val sp = Integer.valueOf((s.substring(0, s.length - 3))) / 1f
                    ret = dip2px(sp) / 1f
                } else {
                    ret = java.lang.Float.parseFloat(s)
                }
                Log.i("XSKBLoader", "17:50,getFloat ret:$ret ")// 2017/12/28,yhongm
                return ret
            } catch (e: NumberFormatException) {
                return defValue
            }

        } else {
            return mContext!!.resources.getDimension(resId)
        }
    }

    private fun getBoolean(
        xrp: XmlResourceParser, name: String,
        defValue: Boolean
    ): Boolean {
        val s = xrp.getAttributeValue(null, name) ?: return defValue
        Log.i("XSKBLoader", "17:44,getBoolean getBoolean name:$name ,s:$s ")// 2017/12/28,yhongm
        try {
            return java.lang.Boolean.parseBoolean(s)
        } catch (e: NumberFormatException) {
            return defValue
        }

    }

    private fun getDrawable(
        xrp: XmlResourceParser, name: String,
        defValue: Drawable?
    ): Drawable? {
        val resId = xrp.getAttributeResourceValue(null, name, 0)
        return if (0 == resId) {
            defValue
        } else {
            val drawable = mResources.getDrawable(resId)
            drawable
        }
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = mContext!!.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }


    fun dip2px(dpValue: Float): Int {
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private val XMLATTR_START_POS_X = "start_x"

        private val XMLATTR_START_POS_Y = "start_y"

        private val XMLATTR_ROW_ID = "row_id"

        private val XMLTAG_KEYBOARD = "ckeyboard"

        private val XMLTAG_ROW = "row"

        private val XMLTAG_KEYS = "keys"


        private val XMLTAG_KEY = "key"

        private val XMLATTR_KEY_WIDTH = "width"

        private val XMLATTR_KEY_HEIGHT = "height"

        private val XMLATTR_KEY_PRESS_COLOR = "press_color"

        private val XMLATTR_KEY_PRESS_IMG = "press_img"

        private val XMLATTR_KEY_SPLITTER = "splitter"

        private val XMLATTR_KEY_TEXTS = "ks_texts"
        private val XMLATTR_KEY_CODES = "ks_codes"

        private val XMLATTR_KEY_TEXT = "k_text"

        private val XMLATTR_KEY_CODE = "k_code"

        private val XMLATTR_KEY_ICON = "k_icon"

        private val XMLATTR_KEY_TEXT_COLOR = "k_t_color"

        private val XMLATTR_KET_TEXT_SIZE = "k_t_size"

        private val XMLATTR_KET_BG_COLOR = "k_bg_color"

        private val XMLATTR_KET_BG_IMG = "k_bg_img"

        private val XMLATTR_KEYS_TEXT_COLOR = "ks_t_color"

        private val XMLATTR_KEYS_TEXT_SIZE = "ks_t_size"

        private val XMLATTR_KEYS_BG_COLOR = "ks_bg_color"

        private val XMLATTR_KEYS_BG_IMG = "ks_bg_img"


        private val XMLATTR_SOFT_KET_TEXT_SIZE = "csk_t_size"

        private val XMLATTR_SOFT_KET_TEXT_COLOR = "csk_t_color"


        private val XMLATTR_SOFT_BG_COLOR = "csk_bg_color"

        private val XMLATTR_SOFT_BG_IMG = "csk_bg_img"


    }


}
