package com.qzwx.myapplication.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qzwx.core.QZWXApplication
import com.qzwx.myapplication.R
import com.qzwx.myapplication.data.LinkEntity
import com.qzwx.myapplication.viewmodel.LinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWebScreen(linkViewModel : LinkViewModel) {
    // 管理对话框的显示状态
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    // 用户输入的数据
    var websiteName by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(R.drawable.app_svg_web) }
    var selectedLinkId by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var linkToDelete by remember { mutableStateOf<LinkEntity?>(null) }
    // 可供选择的默认图标
    val defaultIcons = listOf(
        R.drawable.svg_sousuo,
        R.drawable.svg_rijiben,
        R.drawable.svg_music,
        R.drawable.svg_movie,
        R.drawable.svg_dongman,
        R.drawable.app_svg_web
    )
    // 显示添加链接的对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("添加新链接", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    OutlinedTextField(
                        value = websiteName,
                        onValueChange = { websiteName = it },
                        label = { Text("网站名称", style = MaterialTheme.typography.labelMedium) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("网站地址", style = MaterialTheme.typography.labelMedium) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("选择图标：", style = MaterialTheme.typography.bodyMedium)
                    LazyRow {
                        items(defaultIcons.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        selectedIcon = defaultIcons[index]
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = defaultIcons[index]),
                                    contentDescription = "Icon $index",
                                    modifier = Modifier.size(40.dp)
                                )
                                if (selectedIcon == defaultIcons[index]) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Green,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .offset(x = 20.dp, y = 20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (websiteName.isNotBlank() && websiteUrl.isNotBlank()) {
                        linkViewModel.insertLink(
                            LinkEntity(
                                url = websiteUrl,
                                iconResId = selectedIcon,
                                description = websiteName
                            )
                        )
                        showDialog = false
                        Toast.makeText(QZWXApplication.getContext(),
                            "添加成功!",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(QZWXApplication.getContext(),
                            "网站名称和网址不能为空!",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text("添加", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
    // 显示编辑链接的对话框
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("编辑链接", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    OutlinedTextField(
                        value = websiteName,
                        onValueChange = { websiteName = it },
                        label = {
                            Text("网站名称",
                                style = MaterialTheme.typography.labelSmall)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = {
                            Text("网站地址",
                                style = MaterialTheme.typography.labelSmall)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("选择图标：", style = MaterialTheme.typography.bodyMedium)
                    LazyRow {
                        items(defaultIcons.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        selectedIcon = defaultIcons[index]
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = defaultIcons[index]),
                                    contentDescription = "Icon $index",
                                    modifier = Modifier.size(40.dp)
                                )
                                if (selectedIcon == defaultIcons[index]) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Green,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .offset(x = 20.dp, y = 20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    linkViewModel.updateLink(
                        LinkEntity(
                            id = selectedLinkId,
                            url = websiteUrl,
                            iconResId = selectedIcon,
                            description = websiteName
                        )
                    )
                    showEditDialog = false
                    Toast.makeText(QZWXApplication.getContext(),
                        "修改成功!",
                        Toast.LENGTH_SHORT)
                        .show()
                }) {
                    Text("保存", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
    // 显示删除确认对话框
    if (showDeleteDialog && linkToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("确认删除",
                    style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text("你确定要删除这个链接吗？",
                    style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = {
                    linkToDelete?.let {
                        linkViewModel.deleteLink(it.id)
                        Toast.makeText(QZWXApplication.getContext(),
                            "删除成功!",
                            Toast.LENGTH_SHORT).show()
                    }
                    showDeleteDialog = false
                    linkToDelete = null // 清空要删除的链接
                }) {
                    Text("确认", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    linkToDelete = null // 清空要删除的链接
                }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    websiteName = "" // 清空网站名称
                    websiteUrl = "" // 清空网站地址
                    selectedIcon = R.drawable.app_svg_web
                    showDialog = true
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加链接"
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 标题和网格内容
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "下面是一些可能有用的网站：",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFCAEAE), // 更深的颜色提高可读性
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                // 显示数据库中的链接
                val links by linkViewModel.allLinks.collectAsState(initial = emptyList())
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(links) { link ->
                        LinkItemView(
                            link = link.url,
                            iconResId = link.iconResId,
                            description = link.description,
                            onEditClick = {
                                websiteName = link.description
                                websiteUrl = link.url
                                selectedIcon = link.iconResId
                                selectedLinkId = link.id
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                linkToDelete = link // 设置要删除的链接
                                showDeleteDialog = true // 显示删除确认对话框
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinkItemView(
    link : String,
    iconResId : Int,
    description : String,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .clickable {
                // 确保链接以 http:// 或 https:// 开头
                val validLink =
                    if (link.startsWith("http://") || link.startsWith("https://")) {
                        link
                    } else {
                        "http://$link"
                    }
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(validLink)))
            }
            .padding(8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp,
                brush = Brush.verticalGradient(colors = listOf(Color(0xFFA18CD1),
                    Color(0xFFFBC2EB)))),
                shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = description,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis, // 省略文本
                maxLines = 1 // 最大行数
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEditClick) {
                    Text("编辑", style = MaterialTheme.typography.labelSmall)
                }
                TextButton(onClick = onDeleteClick) {
                    Text("删除", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}