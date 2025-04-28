package com.qzwx.qzwxapp.page.allweb

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qzwx.qzwxapp.data.LinkEntity
import kotlin.random.Random

// 定义网站类别
enum class WebsiteCategory {
    SEARCH, SOCIAL, NEWS, ENTERTAINMENT, EDUCATION, TOOLS, OTHER
}

// 为不同类别定义颜色
val categoryColors = mapOf(
    WebsiteCategory.SEARCH to Color(0xFF4285F4),        // Google 蓝
    WebsiteCategory.SOCIAL to Color(0xFF1877F2),        // Facebook 蓝
    WebsiteCategory.NEWS to Color(0xFFE71D36),          // 新闻红
    WebsiteCategory.ENTERTAINMENT to Color(0xFFFF9F1C), // 娱乐橙
    WebsiteCategory.EDUCATION to Color(0xFF2EC4B6),     // 教育青绿
    WebsiteCategory.TOOLS to Color(0xFF7B1FA2),         // 工具紫
    WebsiteCategory.OTHER to Color(0xFF78909C)          // 其他灰蓝
)

// 为链接实体添加扩展属性获取颜色
fun LinkEntity.getColor(): Color {
    // 根据链接的 URL 或描述确定类别
    val category = when {
        url.contains("google") || url.contains("baidu") || url.contains("bing") -> WebsiteCategory.SEARCH
        url.contains("facebook") || url.contains("twitter") || url.contains("weibo") || url.contains("instagram") -> WebsiteCategory.SOCIAL
        url.contains("news") || url.contains("sina") || url.contains("163") -> WebsiteCategory.NEWS
        url.contains("youtube") || url.contains("bilibili") || url.contains("movie") || url.contains("film") || url.contains("video") -> WebsiteCategory.ENTERTAINMENT
        url.contains("edu") || url.contains("learn") || url.contains("course") -> WebsiteCategory.EDUCATION
        url.contains("tool") || url.contains("util") || url.contains("converter") -> WebsiteCategory.TOOLS
        else -> WebsiteCategory.OTHER
    }
    
    return categoryColors[category] ?: Color(0xFF78909C)
}

// 为链接生成随机梯度色
fun LinkEntity.getGradientColors(): List<Color> {
    val baseColor = getColor()
    val seed = url.hashCode() + description.hashCode()
    val random = Random(seed)
    
    // 创建基于基础颜色的变种
    return listOf(
        baseColor,
        Color(
            red = (baseColor.red + random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            green = (baseColor.green + random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            blue = (baseColor.blue + random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            alpha = 1f
        ),
        Color(
            red = (baseColor.red - random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            green = (baseColor.green - random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            blue = (baseColor.blue - random.nextFloat() * 0.2f).coerceIn(0f, 1f),
            alpha = 1f
        )
    )
}

// 获取网站首字母
fun String.firstLetterOrSymbol(): String {
    return this.trim().firstOrNull()?.uppercase() ?: "#"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp
    ) {
        Column {
            TopAppBar(
                title = { 
                    Text(
                        "网站便签",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onBackupClick) {
                        Icon(
                            Icons.Outlined.Backup, 
                            contentDescription = "备份数据",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onRestoreClick) {
                        Icon(
                            Icons.Outlined.RestorePage, 
                            contentDescription = "恢复数据",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 2.dp
    ) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { /* 搜索时使用同一个查询 */ },
            active = true,
            onActiveChange = { if (!it) onCloseSearch() },
            placeholder = { Text("搜索网站...") },
            leadingIcon = {
                IconButton(onClick = onCloseSearch) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                dividerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                inputFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        ) {
            // 搜索建议可以在这里添加
        }
    }
}

@Composable
fun LinkList(
    links: List<LinkEntity>,
    onLinkClick: (String) -> Unit,
    onEditClick: (LinkEntity) -> Unit,
    onDeleteClick: (LinkEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        items(links) { link ->
            LinkCard(
                link = link,
                onClick = { onLinkClick(link.url) },
                onEditClick = { onEditClick(link) },
                onDeleteClick = { onDeleteClick(link) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkCard(
    link: LinkEntity,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val gradientColors = link.getGradientColors()
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            if (expanded) {
                expanded = false
            } else {
                onClick()
            }
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 网站首字母图标
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(gradientColors),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = link.description.firstLetterOrSymbol(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = link.description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = link.url,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开"
                    )
                }
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = "访问链接",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("访问")
                    }
                    
                    OutlinedButton(
                        onClick = onEditClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑链接",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("编辑")
                    }
                    
                    OutlinedButton(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除链接",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    isSearching: Boolean,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Outlined.SearchOff else Icons.Outlined.Collections,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isSearching) "未找到匹配的网站" else "还没有收藏网站",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isSearching) "尝试使用不同的搜索词" else "点击下方按钮添加你喜欢的网站",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        if (!isSearching) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加网站")
            }
        }
    }
}

@Composable
fun AddLinkDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, url: String) -> Unit
) {
    var websiteName by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "添加新网站",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = websiteName,
                    onValueChange = { websiteName = it },
                    label = { Text("网站名称") },
                    placeholder = { Text("例如：百度") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = websiteUrl,
                    onValueChange = { websiteUrl = it },
                    label = { Text("网站地址") },
                    placeholder = { Text("例如：www.baidu.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(websiteName, websiteUrl)
                    // 注意：这里不能清空内容，因为这个函数会在Dialog被关闭后重置状态
                },
                enabled = websiteName.isNotBlank() && websiteUrl.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun EditLinkDialog(
    initialName: String,
    initialUrl: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, url: String) -> Unit
) {
    var websiteName by remember { mutableStateOf(initialName) }
    var websiteUrl by remember { mutableStateOf(initialUrl) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "编辑网站",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = websiteName,
                    onValueChange = { websiteName = it },
                    label = { Text("网站名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = websiteUrl,
                    onValueChange = { websiteUrl = it },
                    label = { Text("网站地址") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(websiteName, websiteUrl) },
                enabled = websiteName.isNotBlank() && websiteUrl.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun DeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除", style = MaterialTheme.typography.titleLarge) },
        text = { Text("你确定要删除这个网站吗？此操作无法撤销。", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun BackupConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("备份数据", style = MaterialTheme.typography.titleLarge) },
        text = { 
            Column {
                Text(
                    "备份将保存你所有的网站数据",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "备份文件将保存到：Download/七种文学APP备份/",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("备份")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun RestoreConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("恢复数据", style = MaterialTheme.typography.titleLarge) },
        text = { 
            Column {
                Text(
                    "此操作将替换当前所有网站数据。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "请确保选择正确的备份文件。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("选择文件")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
} 