package com.qzwx.feature_qiandaosystem.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SettingsDrawerContent(
    navController : NavController,
    closeDrawer : () -> Unit,
    onShowExperienceRules : () -> Unit,
) {
    val context = LocalContext.current
    var isReminderEnabled by remember { mutableStateOf(true) } // 默认启用提醒

    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        // 经验值递增规则
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.EventNote, contentDescription = null) },
            label = { Text("经验值递增规则") },
            selected = false,
            onClick = {
                onShowExperienceRules()
                closeDrawer()
            }
        )
        // 提醒功能（使用 Switch）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    isReminderEnabled = !isReminderEnabled
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text("提醒功能")
            }
            Switch(
                checked = isReminderEnabled,
                onCheckedChange = {
                    isReminderEnabled = it
                }
            )
        }
        // 语言选择
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Language, contentDescription = null) },
            label = { Text("日历视图") },
            selected = false,
            onClick = {
                navController.navigate("calendar")
            }
        )
    }
}

//经验值规则dialog
@Composable
fun ExperienceRulesDialog(onDismiss : () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("经验值递增规则") },
        text = {
            Column {
                Text("1. 每天能且只能签到一次。")
                Spacer(Modifier.height(8.dp))
                Text("2. 签到经验值为1-7随机获取。")
                Spacer(Modifier.height(8.dp))
                Text("3. 经验值与等级对应关系：")
                Text("   - 50: 等级 1")
                Text("   - 200: 等级 2")
                Text("   - 500: 等级 3")
                Text("   - 800: 等级 4")
                Text("   - 1500: 等级 5")
                Text("   - 3000: 等级 6")
                Text("   - 达到对应经验值，等级自动 +1")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

