package com.qzwx.feature_wordsmemory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.viewmodel.WordViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewWordsPage(
    viewModel : WordViewModel, // 引入 ViewModel
    navController : NavController,
    modifier : Modifier = Modifier
) {
    var word by remember { mutableStateOf("") }
    var isWordError by remember { mutableStateOf(false) }
    var selectedPos by remember { mutableStateOf("") }
    var isPosMenuExpanded by remember { mutableStateOf(false) }
    var definition by remember { mutableStateOf("") }
    var isBatchMode by remember { mutableStateOf(false) } // 批量插入模式状态
    val posOptions = listOf("名词", "动词", "形容词", "副词")
    val isFormValid = word.isNotBlank() && selectedPos.isNotBlank() && definition.isNotBlank()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 批量插入按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { isBatchMode = !isBatchMode },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isBatchMode) MaterialTheme.colors.primary else Color.LightGray
                    )
                ) {
                    Text(text = if (isBatchMode) "退出批量插入" else "批量插入",
                        color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // 单词名称输入框
            OutlinedTextField(
                value = word,
                onValueChange = {
                    word = it
                    isWordError = false // 这里可以添加数据库校验逻辑
                },
                label = { Text("单词名称") },
                isError = isWordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            if (isWordError) {
                Text(
                    text = "单词已存在",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }
            // 词性选择下拉菜单
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        unfocusedBorderColor = Color.Gray
                    )
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
            // 释义输入框
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
                    .height(120.dp)
                    .padding(bottom = 16.dp),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            // 操作栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate("homepage") // 返回主页
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(text = "取消")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (isFormValid) {
                            val newWord =
                                Word(word = word, pos = selectedPos, definition = definition)
                            viewModel.insert(newWord) // 插入单词
                            if (!isBatchMode) {
                                navController.navigate("homepage") // 非批量插入模式下返回主页
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
                        backgroundColor = if (isFormValid) MaterialTheme.colors.primary else Color.LightGray
                    )
                ) {
                    Text(text = "保存", color = Color.White)
                }
            }
        }
    }
}

