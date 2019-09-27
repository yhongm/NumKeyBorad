# NumKeyBorad
一个简单灵活高度可配置的安卓自定义键盘，a simple  flexible configurable keyboad for android

# 相比原生安卓自定义键盘增加了按键颜色,按键图片等等
# 效果图
<img src="screen.png">
# 用法
1. 配置xml  
```xml
<?xml version="1.0" encoding="utf-8"?>
<ckeyboard     <!--键盘根节点-->
    csk_bg_color="@color/bgColor" <!--键盘背景色-->
    csk_t_color="@color/softKeyColor" <!--键盘全局按键颜色-->
    csk_t_size="24%sp"  <!--按键大小-->
    height="100%p" <!--键盘高度-->
    start_x="0%p" <!--键盘开始X位置-->
    start_y="0%p" <!--键盘Y位置-->
    width="100%p"> <!--键盘宽度-->
    <row height="25%p">  <!--键盘行节点-->
        <keys   <!--按键数组 通过解析splitter属性分割成多个按键渲染-->
            height="25%p"
            ks_codes="1|2|3"  <!--按键keyCode数组属性 通过解析splitter分割成单个键盘按键并渲染 -->
            ks_t_size="24%sp"
            ks_texts="1|2|3"    <!--按键keyText数组属性 通过解析splitter分割成单个键盘按键并渲染-->
            press_color="@color/softKeyColor"  <!--键盘按键keyCode数组 通过splitter分割成单个键盘按键并渲染-->
            splitter="|"    <!--键盘按键数组分割符号属性-->
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
        <key  <!--单个键盘按键-->
            height="25%p"
            k_code="-9"
            k_icon="@mipmap/ic_del"   <!--键盘按键图标,用于在按键上显示复杂效果的图片-->
            k_size="24%sp"
            press_color="@color/softKeyColor"
            width="33%p"></key>
    </row>

</ckeyboard>

```

```kotlin
val keyBoardUtil = KeyBoardUtil(applicationContext, R.layout.keyboardview, R.xml.num_pad)
        keyBoardUtil.applyToEdit(edit_text) //应用到对应的EditText
        keyBoardUtil.mKeyClickIntercept = { keyCode, keyText, editTextContent ->
            // click keyboard do something yhongm
            //按键点击,处理按键点击事件
            var resultStr = handleKeyBoard(keyCode, keyText, editTextContent)
            if (resultStr != "error") {
                resultStr
            } else {
                editTextContent
            }

        }
        keyBoardUtil.mTextChangeListener = {
        //editText内容变化
            // text change do something yhongm
        }
```
