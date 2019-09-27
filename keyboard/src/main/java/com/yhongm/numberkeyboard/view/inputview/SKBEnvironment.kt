package com.yhongm.numberkeyboard.view.inputview

import android.content.Context
import android.content.res.Configuration
import android.view.WindowManager

class SKBEnvironment private constructor() {

    var screenWidth: Int = 0
        private set
    var screenHeight: Int = 0
        private set
    var keyHeight: Int = 0
        private set


    private var mNormalKeyTextSize: Int = 0


    val configuration = Configuration()

    val keyXMarginFactor: Float
        get() = 1.0f

    val keyYMarginFactor: Float
        get() = if (Configuration.ORIENTATION_LANDSCAPE == configuration.orientation) {
            0.7f
        } else 1.0f

    fun updateConfig(newConfig: Configuration, context: Context) {
        if (configuration.orientation != newConfig.orientation) {
            val wm = context
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = wm.defaultDisplay
            screenWidth = d.width
            screenHeight = d.height
            val scale: Int
            if (screenHeight > screenWidth) {
                keyHeight = (screenHeight * KEY_HEIGHT_RATIO_PORTRAIT).toInt()
                scale = screenWidth
            } else {
                keyHeight = (screenHeight * KEY_HEIGHT_RATIO_LANDSCAPE).toInt()
                scale = screenHeight
            }
            mNormalKeyTextSize = (scale * NORMAL_KEY_TEXT_SIZE_RATIO).toInt()
        }

        configuration.updateFrom(newConfig)
    }

    fun getKeyTextSize(): Int {
        return mNormalKeyTextSize
    }


    companion object {
        private val KEY_HEIGHT_RATIO_PORTRAIT = 0.105f

        private val KEY_HEIGHT_RATIO_LANDSCAPE = 0.147f



        private val NORMAL_KEY_TEXT_SIZE_RATIO = 0.075f


        private var mInstance: SKBEnvironment? = null

        val instance: SKBEnvironment
            get() {
                if (null == mInstance) {
                    mInstance = SKBEnvironment()
                }
                return mInstance!!
            }

    }

}
