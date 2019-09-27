package com.yhongm.demo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.yhongm.numberkeyboard.view.inputview.KeyBoardUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val keyBoardUtil = KeyBoardUtil(applicationContext, R.layout.keyboardview, R.xml.num_pad)
        keyBoardUtil.applyToEdit(edit_text)
        keyBoardUtil.mKeyClickIntercept = { keyCode, keyText, editTextContent ->
            // click keyboard do something yhongm
            var resultStr = handleKeyBoard(keyCode, keyText, editTextContent)
            if (resultStr != "error") {
                resultStr
            } else {
                editTextContent
            }

        }
        keyBoardUtil.mTextChangeListener = {
            // text change do something yhongm
        }


    }

    fun handleKeyBoard(keyCode: Int, keyText: String?, editTextContent: String): String {
        var resultStr = ""
        if (!TextUtils.isEmpty(editTextContent)) {
            resultStr = editTextContent
        }
        if (keyCode >= 0) {
            if (resultStr.length > 1) {
                val firstChar = resultStr.get(0)
                val secondChar = resultStr.get(1)

                if (!resultStr.contains(".")) {
                    if (resultStr.length >= 6) {
                        //                                Toast.makeText(InputAmountActivity.this, "input max length", Toast.LENGTH_SHORT).show();
                        showToast("输入达到最大长度")
                        return "error"
                    }
                }
                if (firstChar == '0' && secondChar != '.') {

                    resultStr = resultStr.substring(1)
                }

                if (resultStr.contains(".")) {
                    val split =
                        resultStr.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() })
                            .toTypedArray()
                    if (split.size > 1) {
                        val s = split[1]
                        if (s.length >= 2) {
                            //                                    Toast.makeText(InputAmountActivity.this, "input invalid", Toast.LENGTH_SHORT).show();
                            showToast("金额只能包含两位")
                            return "error"
                        }
                    }
                }


            } else if (resultStr.length == 1) {
                val firstChar = resultStr.get(0)
                if (firstChar == '.') {
                    resultStr = "0."
                } else if (firstChar == '0') {
                    resultStr = ""
                }
            }
            resultStr += keyCode

        } else if (keyCode == -10) {
            if (!resultStr.contains(".")) {
                if (resultStr.length == 1) {
                    resultStr += "."
                } else {

                    resultStr += "."
                }
            } else {
                showToast("输入无效")
                return "error"
                //                        Toast.makeText(InputAmountActivity.this, "input invalid", Toast.LENGTH_SHORT).show();
            }
        } else if (keyCode == -9) {
            if (resultStr.length > 1) {
                resultStr = resultStr.substring(0, resultStr.length - 1)
            } else if (resultStr.length == 1) {
                resultStr = "0"
            }

        }
        return resultStr

    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
