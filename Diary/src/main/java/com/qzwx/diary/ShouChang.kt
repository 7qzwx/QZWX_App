package com.qzwx.diary

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShouChangScreen() {
    // 用于存储搜索框文本内容的状态变量
    var text by remember { mutableStateOf("") }
    // 用于存储搜索框是否处于活动状态的状态变量
    var active by remember { mutableStateOf(false) }
    // 用于存储搜索历史的可变状态列表
    var items = remember {
        mutableStateListOf(
            "如何让富婆爱上我",
            "日记寄语"
        )
    }
    // 用于存储搜索框焦点状态的状态变量
    var isFocused by remember { mutableStateOf(false) }
    // 用于控制搜索框缩放比例的状态变量
    val scale = if (isFocused) 1.0f else 0.8f

    // Scaffold 是一个 Material Design 布局结构
    Scaffold {
        // 搜索栏组件
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale) // 根据焦点状态调整缩放
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }, // 监听焦点状态
            query = text,
            // 当搜索框内容改变时的回调函数
            onQueryChange = { text = it },
            // 当点击搜索按钮时的回调函数
            onSearch = {
                items.add(text)
                active = false
                text = ""
            },
            // 搜索框是否处于活动状态的布尔值
            active = active,
            // 当搜索框活动状态改变时的回调函数
            onActiveChange = { active = it },
            // 搜索框的占位符
            placeholder = {
                Text(text = "搜索")
            },
            // 搜索框前置图标
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "搜索图标")
            },
            // 搜索框后置图标
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (text.isNotEmpty()) {
                                text = ""
                            } else {
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭图标"
                    )
                }
            }
        ) {
            // 显示搜索历史列表
            items.forEach {
                Row(modifier = Modifier.padding(all = 14.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 10.dp),
                        imageVector = Icons.Default.History,
                        contentDescription = "历史图标"
                    )
                    Text(text = it)
                }
            }
        }
    }
}
