package com.qzwx.feature_wordsmemory.ui

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewWordsPage(
    viewModel : WordViewModel,
    navController : NavController,
    modifier : Modifier = Modifier
) {
    var word by remember { mutableStateOf("") }
    var isWordError by remember { mutableStateOf(false) }
    var selectedPos by remember { mutableStateOf("") }
    var isPosMenuExpanded by remember { mutableStateOf(false) }
    var definition by remember { mutableStateOf("") }
    var isBatchMode by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val posOptions = listOf("n", "v", "adj", "adv", "其它")
    val isFormValid =
        word.isNotBlank() && selectedPos.isNotBlank() && definition.isNotBlank() && !isWordError
    val coroutineScope = rememberCoroutineScope()
    // 成功消息显示逻辑
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(1000)
            showSuccessMessage = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(), color = androidx.compose.material3.MaterialTheme.colorScheme.surface
    ) {
        // 添加卡片效果包装整个表单
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),  // 适应内容大小,
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 添加标题
                    Text(
                        text = "添加新单词",
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // 批量插入按钮 - 移到右上角
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { isBatchMode = !isBatchMode },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isBatchMode) MaterialTheme.colors.primary else Color.LightGray
                            ),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Text(
                                text = if (isBatchMode) "退出批量插入" else "批量插入",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    // 单词名称输入框 - 添加英文键盘与首字母大写
                    OutlinedTextField(
                        value = word,
                        onValueChange = { newWord ->
                            word = newWord
                            if (newWord.isNotBlank()) {
                                // 检查单词是否存在于数据库中
                                coroutineScope.launch {
                                    val exists = viewModel.wordExists(newWord.trim())
                                    isWordError = exists
                                }
                            } else {
                                isWordError = false
                            }
                        },
                        label = { Text("单词名称") },
                        isError = isWordError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words, // 首字母大写
                            keyboardType = KeyboardType.Ascii, // 使用英文键盘
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = Color.Gray,
                            backgroundColor = Color(0xFFF8F8F8),
                            errorBorderColor = Color.Red
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "单词图标",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    )

                    if (isWordError) {
                        Text(
                            text = "单词已存在",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                    // 词性选择下拉菜单 - 添加图标
                    ExposedDropdownMenuBox(
                        expanded = isPosMenuExpanded,
                        onExpandedChange = { isPosMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedPos,
                            onValueChange = {},
                            label = { Text("词性") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPosMenuExpanded)
                            },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = Color.Gray,
                                backgroundColor = Color(0xFFF8F8F8)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "词性图标",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = isPosMenuExpanded,
                            onDismissRequest = { isPosMenuExpanded = false }
                        ) {
                            posOptions.forEach { pos ->
                                DropdownMenuItem(onClick = {
                                    selectedPos = pos
                                    isPosMenuExpanded = false
                                }) {
                                    Text(text = pos)
                                }
                            }
                        }
                    }
                    // 释义输入框 - 添加图标和更好的视觉效果
                    OutlinedTextField(
                        value = definition,
                        onValueChange = { definition = it },
                        label = { Text("释义") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        placeholder = { Text("多条释义用分号隔开") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = Color.Gray,
                            backgroundColor = Color(0xFFF8F8F8)
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "释义图标",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    )
                    // 成功消息
                    AnimatedVisibility(
                        visible = showSuccessMessage,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            backgroundColor = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "成功",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "保存成功!",
                                    color = Color.White
                                )
                            }
                        }
                    }
                    // 操作栏 - 改进按钮样式
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                navController.navigate("homepage")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Gray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = Color.Gray
                            ),
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "取消",
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "取消")
                            }
                        }

                        Button(
                            onClick = {
                                if (isFormValid) {
                                    val newWord = Word(word = word,
                                        pos = selectedPos,
                                        definition = definition)
                                    viewModel.insert(newWord)
                                    showSuccessMessage = true

                                    if (!isBatchMode) {
                                        // 延迟导航以显示成功消息
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            navController.popBackStack()
                                        }, 400)
                                    } else {
                                        word = ""
                                        selectedPos = ""
                                        definition = ""
                                    }
                                }
                            },
                            enabled = isFormValid,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isFormValid) MaterialTheme.colors.primary else Color.LightGray,
                                disabledBackgroundColor = Color.LightGray
                            ),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "保存",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "保存", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}