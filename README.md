# NumKeyBorad
一个简单灵活高度可配置的安卓自定义键盘，a simple  flexible configurable keyboad for android


# 效果图
<img src="screen.png">
# 用法
1 .配置xml
  
```xml
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

```

```kotlin
val keyBoardUtil = KeyBoardUtil(applicationContext, R.layout.keyboardview, R.xml.num_pad)
        keyBoardUtil.applyToEdit(edit_text) //TextView应用
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
```
