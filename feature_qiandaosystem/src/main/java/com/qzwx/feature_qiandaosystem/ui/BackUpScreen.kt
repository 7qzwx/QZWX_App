package com.qzwx.feature_qiandaosystem.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Pressed
import me.nikhilchaudhari.library.shapes.Punched

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackUpScreen(viewModel : CheckInViewModel) {
    val context = LocalContext.current
    // 文件选择器
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importDatabase(context, uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Backup, contentDescription = "Backup Icon")
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .neumorphic(
                    neuShape = Pressed.Rounded(radius = 8.dp),
                    strokeWidth = 3.dp,
                    elevation = 3.dp
                )
        ) {
            Text(
                text = "点击下面按钮执行该数据库的备份功能!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.exportDatabase(context)
                    Toast.makeText(context, "备份成功！", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minWidth = 0.dp)
                    .neumorphic(neuShape = Punched.Rounded())
            ) {
                Text(text = "导出")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { filePickerLauncher.launch("text/*") },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minWidth = 0.dp)
                    .neumorphic(neuShape = Punched.Rounded())
            ) {
                Text(text = "导入")
            }
        }
    }
}