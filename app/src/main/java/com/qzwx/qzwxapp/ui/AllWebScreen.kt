package com.qzwx.qzwxapp.ui

import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import android.widget.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.qzwx.core.*
import com.qzwx.qzwxapp.R
import com.qzwx.qzwxapp.data.*
import com.qzwx.qzwxapp.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.text.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWebScreen(linkViewModel : LinkViewModel, activity : Activity) {
    // 管理对话框的显示状态
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
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
                        label = { Text("网站名称", style = MaterialTheme.typography.labelSmall) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("网站地址", style = MaterialTheme.typography.labelSmall) },
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
            title = { Text("确认删除", style = MaterialTheme.typography.titleLarge) },
            text = { Text("你确定要删除这个链接吗？", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    linkToDelete?.let {
                        linkViewModel.deleteLink(it.id)
                        Toast.makeText(QZWXApplication.getContext(),
                            "删除成功!",
                            Toast.LENGTH_SHORT).show()
                    }
                    showDeleteDialog = false
                    linkToDelete = null
                }) {
                    Text("确认", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    linkToDelete = null
                }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
    // 显示备份数据对话框
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("备份数据", style = MaterialTheme.typography.titleLarge) },
            text = { Text("是否备份所有链接数据？", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showBackupDialog = false
                    exportDatabaseFromLinkViewModel(linkViewModel, QZWXApplication.getContext())
                }) {
                    Text("备份", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
    // 显示恢复数据对话框
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("恢复数据", style = MaterialTheme.typography.titleLarge) },
            text = { Text("是否从文件恢复链接数据？", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showRestoreDialog = false
                    importDatabaseToLinkViewModel(linkViewModel, activity)
                }) {
                    Text("恢复", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("取消", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    websiteName = ""
                    websiteUrl = ""
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
                // 添加导出和导入按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showBackupDialog = true }) {
                        Text("导出数据", style = MaterialTheme.typography.labelLarge)
                    }
                    TextButton(onClick = { showRestoreDialog = true }) {
                        Text("导入数据", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // 标题和网格内容
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "下面是一些可能有用的网站：",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFCAEAE),
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
                                linkToDelete = link
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// 修改后的导出函数（增强版）
fun exportDatabaseFromLinkViewModel(linkViewModel : LinkViewModel, context : Context) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val links = linkViewModel.allLinks.first()
            val resolver = context.contentResolver
            // 获取当前日期，格式化为 "XX月XX日"
            val dateFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            // 1. 构建增强版文件元数据
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "links_$currentDate.csv") // 修改文件名
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/七种文学APP备份")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            // 2. 多维度文件清理
            val backupDir = File("/storage/emulated/0/Download/七种文学APP备份/")
            if (backupDir.exists() && backupDir.isDirectory) {
                val files = backupDir.listFiles()
                files?.forEach { file ->
                    if (file.name.startsWith("links_") && file.name.endsWith(".csv")) {
                        file.delete() // 删除旧文件
                    }
                }
            }
            // 3. 延迟写入机制（解决Android媒体库缓存问题）
            delay(800)
            // 4. 创建新文件（增强模式）
            val newUri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues.apply {
                    put(MediaStore.Downloads.IS_PENDING, 0)
                }
            ) ?: throw IOException("无法创建文件")
            // 5. 安全写入流程（强制覆盖模式）
            resolver.openOutputStream(newUri, "wt")?.use { outputStream ->
                outputStream.write("id,url,iconResId,description\n".toByteArray())
                links.forEach { link ->
                    val line = "${link.id},${link.url},${link.iconResId},${link.description}\n"
                    outputStream.write(line.toByteArray())
                }
            }
            // 6. 媒体库刷新机制（确保文件立即可见）
            context.sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri)
            )

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "✅ 备份成功！路径：${getFilePathFromUri(context, newUri)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e : Exception) {
            withContext(Dispatchers.Main) {
                val errorMsg = when {
                    e is SecurityException -> "请开启存储权限后再试"
                    e is IOException       -> "文件系统访问失败，请检查目录权限"
                    else                   -> "操作失败：${e.message?.take(50)}"
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}

// 辅助函数：从 Uri 获取真实文件路径
private fun getFilePathFromUri(context : Context, uri : Uri) : String {
    return when {
        DocumentsContract.isDocumentUri(context, uri) -> {
            val docId = DocumentsContract.getDocumentId(uri)
            when {
                isExternalStorageDocument(uri) -> {
                    val split = docId.split(":")
                    "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }

                isDownloadsDocument(uri)       -> {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        docId.toLong()
                    )
                    getDataColumn(context, contentUri, null, null)
                }

                else                           -> uri.path ?: "未知路径"
            }
        }

        else                                          -> uri.path ?: "未知路径"
    }
}

private fun getDataColumn(
    context : Context,
    uri : Uri,
    selection : String?,
    selectionArgs : Array<String>?
) : String {
    var result = ""
    context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)?.use {
        if (it.moveToFirst()) {
            result = it.getString(it.getColumnIndexOrThrow("_data"))
        }
    }
    return result
}

private fun isExternalStorageDocument(uri : Uri) : Boolean {
    return uri.authority == "com.android.externalstorage.documents"
}

private fun isDownloadsDocument(uri : Uri) : Boolean {
    return uri.authority == "com.android.providers.downloads.documents"
}

// 导入数据的逻辑
fun importDatabaseToLinkViewModel(linkViewModel : LinkViewModel, activity : Activity) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "text/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    activity.startActivityForResult(intent, REQUEST_CODE_IMPORT)
}

// 处理文件选择结果
fun handleImportResult(linkViewModel : LinkViewModel, context : Context, data : Intent?) {
    val uri = data?.data
    if (uri == null) {
        Toast.makeText(context, "未选择文件", Toast.LENGTH_SHORT).show()
        return
    }
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = inputStream?.bufferedReader()
            val lines = reader?.readLines() ?: emptyList()
            val links = mutableListOf<LinkEntity>()
            for (line in lines.drop(1)) {
                val parts = line.split(",")
                if (parts.size == 4) {
                    val id = parts[0].toInt()
                    val url = parts[1]
                    val iconResId = parts[2].toInt()
                    val description = parts[3]
                    links.add(LinkEntity(id, url, iconResId, description))
                }
            }
            // 清空数据库
            linkViewModel.deleteAllLinks()
            // 插入新数据
            linkViewModel.insertAll(links)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "数据导入成功", Toast.LENGTH_LONG).show()
            }
        } catch (e : Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "失败：咩有给我存储权限", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// 定义文件选择请求码
const val REQUEST_CODE_IMPORT = 1001

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
                val validLink = if (link.startsWith("http://") || link.startsWith("https://")) {
                    link
                } else {
                    "http://$link"
                }
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(validLink)))
            }
            .padding(8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(
                brush =
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2CD8D5), // 柔和紫色
                        Color(0xFFC5C1FF), // 柔和橘粉色
                        Color(0xFFFFBAC3)  // 柔和橘粉色
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(
                    2.dp,
                    brush = Brush.verticalGradient(colors = listOf(Color(0xFF7986CB),
                        Color(0xFFFBC2EB)))
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
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
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEditClick) {
                    Text("编辑",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground)
                }
                TextButton(onClick = onDeleteClick) {
                    Text("删除",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}