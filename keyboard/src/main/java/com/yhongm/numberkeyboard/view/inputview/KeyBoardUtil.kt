package com.yhongm.numberkeyboard.view.inputview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build

import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.annotation.XmlRes

import com.yhongm.numberkeyboard.view.inputview.xmlbean.SoftKeyboard

/**
 *  demo softkeyboard.xml配置
<?xml version="1.0" encoding="utf-8"?>
<ckeyboard
csk_bg_color="@color/bgColor"
csk_t_color="@color/softKeyColor"
csk_t_size="24%sp"
height="100%p"
start_x="0%p"
start_y="0%p"
width="100%p">
<row height="25%p">
<keys
height="25%p"
ks_codes="1|2|3"
ks_t_size="24%sp"
ks_texts="1|2|3"
press_color="@color/softKeyColor"
splitter="|"
width="33%p"></keys>
</row>
<row height="25%p">
<keys
height="25%p"
ks_codes="4|5|6"
ks_t_size="24%sp"
ks_texts="4|5|6"
press_color="@color/softKeyColor"
splitter="|"
width="33%p"></keys>
</row>
<row height="25%p">
<keys
height="25%p"
ks_codes="7|8|9"
ks_t_size="24%sp"
ks_texts="7|8|9"
press_color="@color/softKeyColor"
splitter="|"
width="33%p"></keys>
</row>
<row height="25%p">
<keys
height="25%p"
ks_codes="-10|0"
ks_t_size="24%sp"
ks_texts=".|0"
press_color="@color/softKeyColor"
splitter="|"
width="33%p"></keys>
<key
height="25%p"
k_code="-9"
k_icon="@mipmap/ic_del"
k_size="24%sp"
press_color="@color/softKeyColor"
width="33%p"></key>
</row>

</ckeyboard>
 */

class KeyBoardUtil(
    private val mContext: Context, @LayoutRes val layoutId: Int, @XmlRes val keyBoardXml: Int,
    val keyBoardWidth: Float = 380f, val keyBoradHeight: Float = 200f
) {
    private var mSkbView: SKBView? = null


    private var mEditText: EditText? = null

    private var mViewContainer: ViewGroup? = null

    private var mXskbLoader: XSKBLoader
    private var mSoftKeyboard: SoftKeyboard? = null
    private lateinit var mWindowLayoutParams: WindowManager.LayoutParams
    private var mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    public var mTextChangeListener: (() -> Unit)? = null

    public var mKeyClickIntercept: ((keyCode: Int, keyText: String?, editTextContent: String) -> String)? =
        null
    private var isApply = false

    init {
        mXskbLoader = XSKBLoader(mContext)
        mSkbView = SKBView(mContext)
    }


    val isActivate: Boolean
        get() = if (null == mViewContainer) {
            false
        } else mViewContainer!!.visibility == View.VISIBLE

    fun applyToEdit(editText: EditText) {
        this.mEditText = editText
        isApply = true
        closeEditTextSystemSoftKeyboard()
        focusChangeListener()
        initSoftKeyBorard()

    }

    private fun initSoftKeyBorard() {
        checkApply()
        if (null == mViewContainer) {
            mViewContainer = LayoutInflater.from(mContext).inflate(layoutId, null) as ViewGroup
        } else {
            if (null != mViewContainer!!.parent) {
                return
            }
        }

        mSoftKeyboard =
            mXskbLoader.loadSKB(
                keyBoardXml,
                mXskbLoader.dip2px(keyBoardWidth),
                mXskbLoader.dip2px(keyBoradHeight)
            )

        this.mSkbView!!.setSoftKeyboard(mSoftKeyboard)
        this.mSkbView!!.isEnabled = true
        var paramsHeight = mXskbLoader.dip2px(keyBoradHeight)

        mWindowLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, paramsHeight
        )
        mWindowLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        mWindowLayoutParams.height = paramsHeight
        mWindowLayoutParams.format = PixelFormat.RGBA_8888

        mWindowLayoutParams.gravity = Gravity.BOTTOM
        this.mSkbView!!.setKeyListener(object : SKBView.Listener {
            override fun clickKey(keyCode: Int, keyText: String?) {
                keyClick(keyCode, keyText)
            }
        })
        mViewContainer!!.addView(mSkbView)


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun closeEditTextSystemSoftKeyboard() {
        checkApply()
        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt < 11) {
            this.mEditText!!.inputType = InputType.TYPE_NULL
        } else {
            try {
                val cls = EditText::class.java
                val setShowSoftInputOnFocus =
                    cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
                setShowSoftInputOnFocus.isAccessible = true
                setShowSoftInputOnFocus.invoke(this.mEditText, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun hiddenSystemSoftKeyboard() {
        checkApply()
        val inputMethodManager =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(
                this.mEditText!!.windowToken,
                HIDE_NOT_ALWAYS
            )
        }

    }


    private fun focusChangeListener() {
        checkApply()
        if (null == mEditText) {
            return
        }
        mEditText!!.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showSoftKeyboard()
                } else {
                    hideSoftKeyboard()
                }
            }
    }

    private fun showSoftKeyboard() {
        checkApply()
        hiddenSystemSoftKeyboard()

        mWindowManager.addView(mViewContainer, mWindowLayoutParams)
    }

    private fun hideSoftKeyboard() {
        checkApply()
        if (null != mViewContainer && null != mViewContainer!!.parent) {
            mWindowManager.removeView(mViewContainer)
        }
    }

    private fun keyClick(keyCode: Int, keyText: String?) {
        checkApply()
        mKeyClickIntercept?.let {
            val handleAfterText = it(keyCode, keyText, this.mEditText!!.text.toString())
            mEditText!!.setText(handleAfterText)
            if (mEditText!!.text.toString() != handleAfterText) {
                mTextChangeListener?.let {
                    it()
                }
            }
        }
    }

    fun checkApply() {
        if (!isApply) {
            throw NoApplyEditTextException()
        }
    }
}


