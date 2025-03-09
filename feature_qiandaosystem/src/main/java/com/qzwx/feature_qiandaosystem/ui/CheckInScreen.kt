package com.qzwx.feature_qiandaosystem.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.qzwx.core.ui.BezierShapes
import com.qzwx.feature_qiandaosystem.data.CheckIn
import com.qzwx.feature_qiandaosystem.data.CheckInRepository
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(navController : NavHostController, checkInRepository : CheckInRepository) {
    val viewModel : CheckInViewModel = viewModel(
        factory = CheckInViewModelFactory(
            checkInRepository
        )
    )
    var showDialog by remember { mutableStateOf(false) }
    var checkInName by remember { mutableStateOf("") }
    val allCheckIns by viewModel.allCheckIns.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf<String?>(null) }
    var showDeleteDialogSecond by remember { mutableStateOf<String?>(null) }
    var showResetDialogSecond by remember { mutableStateOf<String?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showExperienceRulesDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<String?>(null) } // 新增状态变量，用于控制编辑对话框
    var editCheckInName by remember { mutableStateOf("") } // 新增状态变量，用于存储编辑后的打卡系统名称
    // 显示 Toast
    ShowToast(toastMessage)
    // 侧边栏状态
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(modifier = Modifier.width((LocalConfiguration.current.screenWidthDp.dp * 0.8f))) {
                SettingsDrawerContent(
                    navController = navController,
                    closeDrawer = { scope.launch { drawerState.close() } },
                    onShowExperienceRules = { showExperienceRulesDialog = true } // 显示规则
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                    title = { Text("打卡系统") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "打开侧边栏"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加打卡类型",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        ) { paddingValues ->
            CheckInList(
                allCheckIns = allCheckIns,
                onCheckIn = { checkInName -> viewModel.checkIn(checkInName) },
                onDelete = { checkInName -> showDeleteDialog = checkInName },
                onReset = { checkInName -> showResetDialog = checkInName },
                onHistory = { checkInName -> navController.navigate("history/${checkInName}") },
                onLockToggle = { checkInName -> viewModel.toggleLockCheckIn(checkInName) },
                onEdit = { checkInName -> showEditDialog = checkInName },
                paddingValues = paddingValues
            )
        }
    }
    // 添加打卡类型对话框
    if (showDialog) {
        AddCheckInDialog(
            checkInName = checkInName,
            onCheckInNameChange = { checkInName = it },
            onConfirm = {
                if (checkInName.isNotBlank()) {
                    viewModel.checkIfCheckInExists(checkInName) { exists ->
                        if (!exists) {
                            viewModel.insertCheckIn(checkInName)
                            checkInName = ""
                            showDialog = false
                        } else {
                            toastMessage = "打卡类型 '$checkInName' 已经存在！"
                            checkInName = ""
                            showDialog = false
                        }
                    }
                } else {
                    toastMessage = "老鼠爬进来了!"
                    checkInName = ""
                    showDialog = false
                }
            },
            onDismiss = { showDialog = false },
            onShowToast = { message -> toastMessage = message }
        )
    }
    // 编辑打卡系统名称对话框
    showEditDialog?.let { checkInName ->
        EditCheckInDialog(
            initialName = checkInName, // 当前打卡系统的名称
            onCheckInNameChange = { editCheckInName = it }, // 更新 editCheckInName
            onConfirm = {
                if (editCheckInName.isNotBlank()) {
                    viewModel.updateCheckInName(checkInName, editCheckInName)
                    showEditDialog = null
                    editCheckInName = ""
                } else {
                    toastMessage = "老鼠爬进来了!"
                    editCheckInName = ""
                    showEditDialog = null
                }
            },
            onDismiss = { showEditDialog = null }
        )
    }
    // 经验值规则对话框
    if (showExperienceRulesDialog) {
        ExperienceRulesDialog(onDismiss = { showExperienceRulesDialog = false })
    }
    // 删除确认对话框
    showDeleteDialog?.let { checkInName ->
        ConfirmDialog(
            title = "删除警告",
            message = "你真的想好不要  '$checkInName'  了吗？",
            onConfirm = {
                showDeleteDialog = null
                showDeleteDialogSecond = checkInName
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
    // 第二次删除确认对话框
    showDeleteDialogSecond?.let { checkInName ->
        ConfirmDialog(
            title = "最后警告",
            message = "抛弃  '$checkInName'  可无法挽回了!",
            onConfirm = {
                viewModel.deleteCheckIn(checkInName)
                showDeleteDialogSecond = null
            },
            onDismiss = { showDeleteDialogSecond = null }
        )
    }
    // 重置确认对话框
    showResetDialog?.let { checkInName ->
        ConfirmDialog(
            title = "重置警告",
            message = "真的想好要重置 '$checkInName' 吗？",
            onConfirm = {
                showResetDialog = null
                showResetDialogSecond = checkInName
            },
            onDismiss = { showResetDialog = null }
        )
    }
    // 第二次重置确认对话框
    showResetDialogSecond?.let { checkInName ->
        ConfirmDialog(
            title = "最后警告",
            message = "重置  '$checkInName'  可无法挽回了!",
            onConfirm = {
                viewModel.resetCheckIn(checkInName)
                showResetDialogSecond = null
            },
            onDismiss = { showResetDialogSecond = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCheckInDialog(
    initialName : String, // 当前打卡系统的名称
    onCheckInNameChange : (String) -> Unit,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    var editName by remember { mutableStateOf(initialName) } // 使用 mutableStateOf 初始化编辑名称

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "编辑打卡系统名称",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column {
                // 使用 editName 作为绑定变量
                TextField(
                    value = editName, // 当前编辑的名称
                    onValueChange = {
                        editName = it // 更新 editName
                        onCheckInNameChange(it) // 同时调用回调更新外部变量
                    },
                    placeholder = {
                        Text(
                            "在这里输入新名称!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (editName.isEmpty()) {
                    Text(
                        text = "介个地方可不能留空,不然老鼠会溜进来",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editName.isNotBlank()) {
                        onConfirm() // 调用外部传入的确认逻辑
                    } else {
                        // 如果名称为空，直接关闭对话框
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("确认",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        shape = RoundedCornerShape(16.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Composable
fun CheckInList(
    allCheckIns : List<CheckIn>,
    onCheckIn : (String) -> Unit,
    onDelete : (String) -> Unit,
    onReset : (String) -> Unit,
    onHistory : (String) -> Unit,
    onLockToggle : (String) -> Unit,
    onEdit : (String) -> Unit,
    paddingValues : PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = LocalConfiguration.current.screenWidthDp.dp * 0.06f,
                end = LocalConfiguration.current.screenWidthDp.dp * 0.07f),
        contentPadding = paddingValues
    ) {
        items(allCheckIns) { checkIn ->
            CheckInCard(
                checkIn = checkIn,
                onDelete = { onDelete(checkIn.name) },
                onReset = { onReset(checkIn.name) },
                onHistory = { onHistory(checkIn.name) },
                onCheckIn = { onCheckIn(checkIn.name) },
                onLockToggle = { onLockToggle(checkIn.name) },
                onEdit = { onEdit(checkIn.name) }
            )
        }
    }
}

@Composable
fun CheckInCard(
    checkIn : CheckIn,
    onDelete : () -> Unit,
    onReset : () -> Unit,
    onHistory : () -> Unit,
    onCheckIn : () -> Unit,
    onLockToggle : () -> Unit,
    onEdit : () -> Unit
) {
    // 定义每个等级所需经验值
    val levelExp = arrayOf(50, 200, 500, 800, 1500, 3000) // 1级到5级的经验值
    // 获取当前等级和经验值
    val currentLevel = checkIn.level
    val currentExp = checkIn.experience
    // 计算进度条的进度
    var expProgress = 0f
    expProgress = if (currentLevel == 1 && currentExp == 0) {
        0f
    } else {
        when (currentLevel) {
            1    -> currentExp.toFloat() / levelExp[0]
            2    -> currentExp.toFloat() / levelExp[1]
            3    -> currentExp.toFloat() / levelExp[2]
            4    -> currentExp.toFloat() / levelExp[3]
            5    -> currentExp.toFloat() / levelExp[4]
            else -> 1f
        }
    }
    Card(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(0.9f) // 设置卡片背景颜色（只能使用单一颜色）
    ),
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxSize()
            .clip(BezierShapes())
            .shadow(2.dp, spotColor = Color.Black, ambientColor = Color.Black, clip = true)
            .border(1.4.dp, brush = Brush.verticalGradient(colors = listOf(Color(0xFFA18CD1),
                Color(0xFFFBC2EB))), BezierShapes())
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = " ${checkIn.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.Bottom)
                            .padding(start = 8.dp) // 与签到名称之间的水平距离
                    ) {
                        Text(
                            text = "等级: $currentLevel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    // 新增编辑按钮
                    IconButton(
                        onClick = onEdit,
                        enabled = !checkIn.isLocked // 如果打卡系统被锁定，编辑按钮不可用
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑打卡类型",
                            tint = if (!checkIn.isLocked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = onLockToggle
                    ) {
                        Icon(
                            imageVector = if (checkIn.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (checkIn.isLocked) "解锁" else "锁定",
                            tint = if (checkIn.isLocked) Color.Red else Color.Green
                        )
                    }
                }
            }
            LinearProgressIndicator(
                progress = { expProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "连续打卡 ${checkIn.consecutiveDays} 天",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(text = "累计 ${checkIn.days} 天",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface)
            }

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = checkIn.lastCheckInDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.Absolute.Right,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDelete,
                        enabled = !checkIn.isLocked,  // 只有在未上锁时才允许删除
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除打卡类型",
                            tint = if (!checkIn.isLocked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    // 重置按钮
                    IconButton(
                        onClick = onReset,
                        enabled = !checkIn.isLocked, // 未上锁时才允许重置
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "重置打卡记录",
                            tint = if (!checkIn.isLocked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    // 历史按钮
                    IconButton(
                        onClick = onHistory,
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "查看打卡历史",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            // 累计经验值
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // EXP 文本部分
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary, // 背景颜色
                            shape = RoundedCornerShape(2.dp) // 圆角
                        )
                ) {
                    Text(
                        "EXP：",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Text(
                        text = "${checkIn.experience}",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
                // 签到按钮部分
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val isSignedToday = checkIn.lastCheckInDate == currentDate

                Button(
                    modifier = Modifier.height(35.dp), shape = RoundedCornerShape(2.dp),
                    onClick = {
                        if (!isSignedToday) {
                            onCheckIn()
                        }
                    },
                    enabled = !isSignedToday,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(
                        text = if (isSignedToday) "已签到" else "签到",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCheckInDialog(
    checkInName : String,
    onCheckInNameChange : (String) -> Unit,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit,
    onShowToast : (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "你希望创建系统的名称",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column {
                TextField(
                    value = checkInName,
                    onValueChange = onCheckInNameChange,
                    placeholder = {
                        Text(
                            "在这里输入名称吧!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Transparent, // 获得焦点时的背景颜色
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary, // 未获得焦点时的背景颜色
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (checkInName.isEmpty()) {
                    Text(
                        text = "介个地方可不能留空,不然老鼠会溜进来",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (checkInName.isNotBlank()) {
                        onConfirm() // 调用外部传入的确认逻辑
                    } else {
                        // 如果名称为空，显示提示
                        onShowToast("老鼠爬进来了!") // 通过回调显示 Toast
                        onDismiss() // 直接关闭对话框
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("确认",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        shape = RoundedCornerShape(16.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@Composable
fun ConfirmDialog(
    title : String,
    message : String,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = Color.Red,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
fun ShowToast(message : String?) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

