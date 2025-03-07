package com.qzwx.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.qzwx.core.QZWXApplication
import com.qzwx.feature_qiandaosystem.data.CheckIn
import com.qzwx.feature_qiandaosystem.data.CheckInHistory
import com.qzwx.feature_qiandaosystem.data.QZXTDatabase
import com.qzwx.myapplication.data.LinkEntity
import com.qzwx.myapplication.data.WebAppDatabase
import kotlinx.coroutines.runBlocking
import me.nikhilchaudhari.library.neumorphic
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

// 主应用数据库
@SuppressLint("StaticFieldLeak")
val db1 = WebAppDatabase.getDatabase(QZWXApplication.getContext())

// 模块1数据库
@SuppressLint("StaticFieldLeak")
val db2 = QZXTDatabase.getInstance(QZWXApplication.getContext())

fun exportDatabases(context : Context) {
    // 备份目录路径
    val backupDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "七种文学APP备份"
    )
    if (!backupDir.exists()) {
        val isCreated = backupDir.mkdirs()
        println("备份目录创建结果: $isCreated")
    }
    // 导出 db1 的所有表
    exportAllTablesToCsv(db1, "web_database", backupDir)
    // 导出 db2 的所有表
    exportAllTablesToCsv(db2, "Feature_QianDaoSystem", backupDir)
    // 提示备份成功
    Toast.makeText(
        context,
        "备份成功，路径: ${backupDir.absolutePath}",
        Toast.LENGTH_LONG
    ).show()
}

fun exportAllTablesToCsv(db : androidx.room.RoomDatabase, dbName : String, backupDir : File) {
    // 定义需要导出的表名
    val tablesToExport = when (dbName) {
        "web_database"          -> listOf("LinkEntity") // 主应用数据库的表
        "Feature_QianDaoSystem" -> listOf("CheckIn", "CheckInHistory") // 模块1数据库的表
        else                    -> emptyList() // 其他数据库不导出
    }
    // 获取所有表名
    val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table'", null)
    while (cursor.moveToNext()) {
        val tableName = cursor.getString(0)
        // 只导出指定的表
        if (tablesToExport.contains(tableName)) {
            val csvFile = File(backupDir, "${dbName}_${tableName}.csv") // 生成 CSV 文件路径
            exportTableToCsv(db, tableName, csvFile) // 导出表数据为 CSV 文件
        }
    }
    cursor.close()
}

@SuppressLint("Range")
fun exportTableToCsv(db : androidx.room.RoomDatabase, tableName : String, outputFile : File) {
    val cursor = db.query("SELECT * FROM $tableName", null)

    try {
        // 如果文件已存在，则先删除
        if (outputFile.exists()) {
            val isDeleted = outputFile.delete()
            println("删除旧文件结果: $isDeleted")
        }
        val writer = FileWriter(outputFile)
        // 写入表头（列名）
        val columns = cursor.columnNames
        writer.write(columns.joinToString(",") + "\n")
        // 写入数据
        while (cursor.moveToNext()) {
            val row = columns.joinToString(",") { column ->
                cursor.getString(cursor.getColumnIndex(column)) ?: ""
            }
            writer.write(row + "\n")
        }

        writer.close()
        cursor.close()
        println("文件写入成功: ${outputFile.absolutePath}")
    } catch (e : IOException) {
        e.printStackTrace()
        println("文件写入失败: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataBackUpScreen(modifier : Modifier = Modifier) {
    val context = LocalContext.current
    // 权限请求 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            exportDatabases(context)
        } else {
            Toast.makeText(context, "需要存储权限才能备份数据", Toast.LENGTH_SHORT).show()
        }
    }
    // 文件选择 Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importDatabaseFromCsv(context, uri, "LinkEntity")
        }
    }
    val checkInPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importDatabaseFromCsv(context, uri, "CheckIn")
        }
    }
    val checkInHistoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            importDatabaseFromCsv(context, uri, "CheckInHistory")
        }
    }
    // 控制对话框显示的状态
    var showLinkEntityDialog by remember { mutableStateOf(false) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showCheckInHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("数据备份与恢复", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        // 导出数据库按钮
        Button(
            onClick = {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES // Android 13+ 需要媒体权限
                } else {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    exportDatabases(context)
                } else {
                    permissionLauncher.launch(permission)
                }
            },
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth()
                .neumorphic()
        ) {
            Text("导出数据库")
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 导入 LinkEntity 数据库按钮
        Button(
            onClick = { showLinkEntityDialog = true },
            modifier = Modifier
                .neumorphic()
        ) {
            Text("导入 LinkEntity 数据")
        }

        Spacer(modifier = Modifier.height(8.dp))
        // 导入 CheckIn 数据库按钮
        Button(
            onClick = { showCheckInDialog = true },
            modifier = Modifier
                .neumorphic()
        ) {
            Text("导入 CheckIn 数据")
        }

        Spacer(modifier = Modifier.height(8.dp))
        // 导入 CheckInHistory 数据库按钮
        Button(
            onClick = { showCheckInHistoryDialog = true },
            modifier = Modifier
                .neumorphic()
        ) {
            Text("导入 CheckInHistory 数据")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
    // 显示 LinkEntity 导入确认对话框
    if (showLinkEntityDialog) {
        ConfirmationDialog(
            title = "警告",
            message = "导入数据将覆盖现有数据，是否继续？",
            onConfirm = {
                showLinkEntityDialog = false
                filePickerLauncher.launch("text/csv")
            },
            onDismiss = { showLinkEntityDialog = false }
        )
    }
    // 显示 CheckIn 导入确认对话框
    if (showCheckInDialog) {
        ConfirmationDialog(
            title = "警告",
            message = "导入数据将覆盖现有数据，是否继续？",
            onConfirm = {
                showCheckInDialog = false
                checkInPickerLauncher.launch("text/csv")
            },
            onDismiss = { showCheckInDialog = false }
        )
    }
    // 显示 CheckInHistory 导入确认对话框
    if (showCheckInHistoryDialog) {
        ConfirmationDialog(
            title = "警告",
            message = "导入数据将覆盖现有数据，是否继续？",
            onConfirm = {
                showCheckInHistoryDialog = false
                checkInHistoryPickerLauncher.launch("text/csv")
            },
            onDismiss = { showCheckInHistoryDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    title : String,
    message : String,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "继续")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "取消")
            }
        }
    )
}

fun importDatabaseFromCsv(context : Context, uri : Uri, tableName : String) {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    val reader = BufferedReader(InputStreamReader(inputStream))

    try {
        val lines = reader.readLines()
        val db = when (tableName) {
            "LinkEntity"                -> WebAppDatabase.getDatabase(QZWXApplication.getContext())
            "CheckIn", "CheckInHistory" -> QZXTDatabase.getInstance(QZWXApplication.getContext())
            else                        -> throw IllegalArgumentException("Unsupported table: $tableName")
        }
        val entities = lines.drop(1).map { line -> // 跳过表头
            val columns = line.split(",")
            when (tableName) {
                "LinkEntity"     -> LinkEntity(
                    id = columns[0].toInt(),
                    url = columns[1],
                    iconResId = columns[2].toInt(),
                    description = columns[3]
                )

                "CheckIn"        -> CheckIn(
                    id = columns[0].toLong(),
                    name = columns[1],
                    experience = columns[2].toInt(),
                    days = columns[3].toInt(),
                    level = columns[4].toInt(),
                    lastCheckInDate = columns[5],
                    isLocked = columns[6].toBoolean(),
                    consecutiveDays = columns[7].toInt()
                )

                "CheckInHistory" -> CheckInHistory(
                    id = columns[0].toLong(),
                    checkInName = columns[1],
                    date = columns[2],
                    experience = columns[3].toInt(),
                    checkInCount = columns[4].toInt(),
                    level = columns[5].toInt()
                )

                else             -> throw IllegalArgumentException("Unsupported table: $tableName")
            }
        }
        // 插入数据
        runBlocking {
            when (db) {
                is WebAppDatabase -> {
                    val dao = db.linkDao()
                    dao.insertAll(entities as List<LinkEntity>)
                }

                is QZXTDatabase   -> {
                    val dao = db.checkInDao()
                    when (tableName) {
                        "CheckIn"        -> dao.insertAllCheckIns(entities as List<CheckIn>)
                        "CheckInHistory" -> dao.insertAllCheckInHistories(entities as List<CheckInHistory>)
                    }
                }
            }
        }

        Toast.makeText(context, "导入成功: $tableName", Toast.LENGTH_LONG).show()
    } catch (e : FileNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "文件未找到: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e : NumberFormatException) {
        e.printStackTrace()
        Toast.makeText(context, "CSV 文件格式错误: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e : IllegalArgumentException) {
        e.printStackTrace()
        Toast.makeText(context, "不支持的表: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e : SecurityException) {
        e.printStackTrace()
        Toast.makeText(context, "权限不足: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e : Exception) {
        e.printStackTrace()
        Toast.makeText(context, "导入失败: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        reader.close()
    }
}