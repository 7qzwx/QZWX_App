package com.qzwx.qzwxapp.page.allweb

import android.content.*
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.qzwx.qzwxapp.data.LinkEntity
import com.qzwx.qzwxapp.viewmodel.LinkViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 定义文件选择请求码
const val REQUEST_CODE_IMPORT = 1001

@Composable
fun AllWebPage(linkViewModel: LinkViewModel, navController: NavController) {
    // 状态管理
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    var websiteName by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var selectedLinkId by remember { mutableStateOf(0) }
    var linkToDelete by remember { mutableStateOf<LinkEntity?>(null) }
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val links by linkViewModel.allLinks.collectAsState(initial = emptyList())
    val filteredLinks = if (searchQuery.isBlank()) links else {
        // 分割搜索查询为多个关键词
        val keywords = searchQuery.trim().lowercase().split(Regex("\\s+"))
        
        links.filter { link -> 
            // 如果任何一个关键词匹配，则包含此链接
            keywords.any { keyword ->
                link.description.lowercase().contains(keyword) || 
                link.url.lowercase().contains(keyword)
            }
        }.sortedByDescending { link ->
            // 计算匹配度分数
            var score = 0
            keywords.forEach { keyword ->
                // 标题匹配权重更高
                if (link.description.lowercase().contains(keyword)) {
                    score += 2
                    // 完全匹配加分
                    if (link.description.lowercase() == keyword) {
                        score += 3
                    }
                }
                // URL匹配
                if (link.url.lowercase().contains(keyword)) {
                    score += 1
                    // URL中的域名部分匹配加分
                    val domain = link.url.lowercase().replace(Regex("^https?://|www\\.|/.*$"), "")
                    if (domain.contains(keyword)) {
                        score += 1
                    }
                }
            }
            score
        }
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { handleImportResult(linkViewModel, context, it) } }
    
    // 对话框组件
    if (showDialog) {
        AddLinkDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, url ->
                if (name.isNotBlank() && url.isNotBlank()) {
                    linkViewModel.insertLink(LinkEntity(url = url, description = name))
                    Toast.makeText(context, "添加成功!", Toast.LENGTH_SHORT).show()
                    showDialog = false
                } else {
                    Toast.makeText(context, "网站名称和网址不能为空!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    if (showEditDialog) {
        EditLinkDialog(
            initialName = websiteName,
            initialUrl = websiteUrl,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, url ->
                linkViewModel.updateLink(LinkEntity(id = selectedLinkId, url = url, description = name))
                Toast.makeText(context, "修改成功!", Toast.LENGTH_SHORT).show()
                showEditDialog = false
            }
        )
    }
    
    if (showDeleteDialog && linkToDelete != null) {
        DeleteConfirmDialog(
            onDismiss = { 
                showDeleteDialog = false
                linkToDelete = null
            },
            onConfirm = {
                linkToDelete?.let {
                    linkViewModel.deleteLink(it.id)
                    Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show()
                }
                showDeleteDialog = false
                linkToDelete = null
            }
        )
    }
    
    if (showBackupDialog) {
        BackupConfirmDialog(
            onDismiss = { showBackupDialog = false },
            onConfirm = {
                showBackupDialog = false
                exportDatabaseFromLinkViewModel(linkViewModel, context)
            }
        )
    }
    
    if (showRestoreDialog) {
        RestoreConfirmDialog(
            onDismiss = { showRestoreDialog = false },
            onConfirm = {
                showRestoreDialog = false
                launcher.launch("text/*")
            }
        )
    }

    // 主界面布局
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onCloseSearch = { isSearchActive = false }
                )
            } else {
                MainTopBar(
                    onBackClick = { navController.navigateUp() },
                    onSearchClick = { isSearchActive = true },
                    onBackupClick = { showBackupDialog = true },
                    onRestoreClick = { showRestoreDialog = true }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加链接")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (filteredLinks.isEmpty()) {
                // 空状态显示
                EmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    onAddClick = { showDialog = true }
                )
            } else {
                // 链接列表显示
                LinkList(
                    links = filteredLinks,
                    onLinkClick = { link ->
                        val validLink = if (link.startsWith("http://") || link.startsWith("https://")) {
                            link
                        } else {
                            "http://$link"
                        }
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(validLink)))
                    },
                    onEditClick = { link ->
                        websiteName = link.description
                        websiteUrl = link.url
                        selectedLinkId = link.id
                        showEditDialog = true
                    },
                    onDeleteClick = { link ->
                        linkToDelete = link
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

// 处理文件选择结果
fun handleImportResult(linkViewModel: LinkViewModel, context: Context, uri: Uri) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = inputStream?.bufferedReader()
            val lines = reader?.readLines() ?: emptyList()
            val links = mutableListOf<LinkEntity>()
            for (line in lines.drop(1)) {
                val parts = line.split(",")
                if (parts.size >= 3) {
                    val id = parts[0].toInt()
                    val url = parts[1]
                    val description = parts[parts.size - 1]
                    links.add(LinkEntity(id, url, description))
                }
            }
            // 清空数据库
            linkViewModel.deleteAllLinks()
            // 插入新数据
            linkViewModel.insertAll(links)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "数据导入成功", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "失败：咩有给我存储权限", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// 导出数据库函数
fun exportDatabaseFromLinkViewModel(linkViewModel: LinkViewModel, context: Context) {
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
                outputStream.write("id,url,description\n".toByteArray())
                links.forEach { link ->
                    val line = "${link.id},${link.url},${link.description}\n"
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
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMsg = when {
                    e is SecurityException -> "请开启存储权限后再试"
                    e is IOException -> "文件系统访问失败，请检查目录权限"
                    else -> "操作失败：${e.message?.take(50)}"
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}

// 辅助函数：从 Uri 获取真实文件路径
private fun getFilePathFromUri(context: Context, uri: Uri): String {
    return when {
        DocumentsContract.isDocumentUri(context, uri) -> {
            val docId = DocumentsContract.getDocumentId(uri)
            when {
                isExternalStorageDocument(uri) -> {
                    val split = docId.split(":")
                    "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }

                isDownloadsDocument(uri) -> {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        docId.toLong()
                    )
                    getDataColumn(context, contentUri, null, null)
                }

                else -> uri.path ?: "未知路径"
            }
        }

        else -> uri.path ?: "未知路径"
    }
}

private fun getDataColumn(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
): String {
    var result = ""
    context.contentResolver.query(uri, arrayOf("_data"), selection, selectionArgs, null)?.use {
        if (it.moveToFirst()) {
            result = it.getString(it.getColumnIndexOrThrow("_data"))
        }
    }
    return result
}

private fun isExternalStorageDocument(uri: Uri): Boolean {
    return uri.authority == "com.android.externalstorage.documents"
}

private fun isDownloadsDocument(uri: Uri): Boolean {
    return uri.authority == "com.android.providers.downloads.documents"
} 