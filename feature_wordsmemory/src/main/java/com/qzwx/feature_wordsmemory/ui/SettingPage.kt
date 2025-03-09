package com.qzwx.feature_wordsmemory.ui

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qzwx.feature_wordsmemory.WordsMemoryActivity
import com.qzwx.feature_wordsmemory.data.AppDatabase
import com.qzwx.feature_wordsmemory.data.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.nikhilchaudhari.library.NeuInsets
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Pot
import me.nikhilchaudhari.library.shapes.Pressed
import me.nikhilchaudhari.library.shapes.Punched
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun SettingPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center // 让整个内容居中
    ) {
        SimpleDesignCard()
    }
}

@Composable
fun SimpleDesignCard() {
    val context = LocalContext.current
    val activity = context as? WordsMemoryActivity  // 获取 Activity 实例
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center // 让 Card 居中
    ) {
        Card(
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .size(400.dp, 350.dp)
                .neumorphic(
                    neuShape = Pot.Rounded(6.dp),
                    strokeWidth = 6.dp,
                    neuInsets = NeuInsets(1.dp, 1.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = com.qzwx.core.R.drawable.qzwx_words),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(60.dp, 60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(
                    text = "这是一个记单词功能,你可以在单词库中通过左右滑动快速删除/标记/收藏单词,效率高且方便直观!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .neumorphic(neuShape = Pressed.Rounded(12.dp),
                            neuInsets = NeuInsets(3.dp),
                            strokeWidth = 1.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            exportWordsDatabase(context)
                        },

                    ) {
                        Text(text = "导出")
                    }
                    Button(
                        onClick = {
                            activity?.importWordsDatabase() // 调用 Activity 中的 importWordsDatabase 方法
                        }
                    ) {
                        Text(text = "导入")
                    }
                }
            }
        }
    }
}

fun exportWordsDatabase(context : Context) {
    // 立即显示一个操作开始的Toast
    Toast.makeText(context, "开始导出单词...", Toast.LENGTH_SHORT).show()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 获取数据库实例
            val db = AppDatabase.getDatabase(context)
            val wordDao = db.wordDao()
            // 使用first()获取一次性列表，而不是Flow
            val words = wordDao.getAllWords().first()

            if (words.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "没有单词数据需要导出", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val resolver = context.contentResolver
            // 获取当前日期，格式化为 "XX月XX日"
            val dateFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            // 文件名和路径
            val fileName = "words_$currentDate.csv"
            val relativePath = "Download/七种文学APP备份"
            // 先尝试查找和删除已存在的同名文件
            try {
                // 构建查询
                val selection =
                    "${MediaStore.Downloads.DISPLAY_NAME} = ? AND ${MediaStore.Downloads.RELATIVE_PATH} = ?"
                val selectionArgs = arrayOf(fileName, "$relativePath/")
                // 执行查询
                resolver.query(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Downloads._ID),
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->
                    // 如果找到匹配文件
                    if (cursor.moveToFirst()) {
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                        val id = cursor.getLong(idColumn)
                        val deleteUri =
                            ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                id)
                        // 删除文件
                        val deleteCount = resolver.delete(deleteUri, null, null)
                        if (deleteCount > 0) {
                            // 文件已成功删除
                        }
                    }
                }
            } catch (e : Exception) {
                // 删除失败，但我们继续创建新文件
                e.printStackTrace()
            }
            // 构建文件元数据
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
            }

            try {
                // 尝试创建目录（可能不需要，Android会自动创建）
                val backupDir =
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "七种文学APP备份")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
            } catch (e : Exception) {
                // 忽略目录创建失败，我们会使用MediaStore API
            }
            // 创建新文件URI
            val newUri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IOException("无法创建文件")
            // 写入文件
            resolver.openOutputStream(newUri)?.use { outputStream ->
                outputStream.write("id,word,pos,definition,tag\n".toByteArray())
                words.forEach { word ->
                    // 确保CSV格式正确，处理可能包含逗号的字段
                    val cleanWord = word.word.replace(",", "，") // 替换英文逗号为中文逗号
                    val cleanPos = word.pos.replace(",", "，")
                    val cleanDef = word.definition.replace(",", "，")
                    val cleanTag = word.tag.replace(",", "，")
                    val line = "${word.id},$cleanWord,$cleanPos,$cleanDef,$cleanTag\n"
                    outputStream.write(line.toByteArray())
                }
            }
            // 确保文件可见
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri))
            // 获取实际保存的路径
            val filePath = "Download/七种文学APP备份/words_$currentDate.csv"

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "✅ 成功备份 ${words.size} 个单词！\n保存至：$filePath",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e : Exception) {
            e.printStackTrace() // 打印详细错误信息以便调试
            withContext(Dispatchers.Main) {
                val errorMsg = when {
                    e is SecurityException -> "请开启存储权限后再试"
                    e is IOException       -> "文件系统访问失败：${e.message}"
                    else                   -> "导出失败：${e.message ?: "未知错误"}"
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}

// 处理导入结果的方法
fun handleWordsImportResult(context : Context, data : Intent?) {
    val uri = data?.data
    if (uri == null) {
        Toast.makeText(context, "未选择文件", Toast.LENGTH_SHORT).show()
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("无法打开文件")
            val reader = inputStream.bufferedReader()
            val lines = reader.readLines()
            reader.close()

            if (lines.isEmpty()) {
                throw IOException("文件为空")
            }
            val words = mutableListOf<Word>()
            // 检查CSV头部格式
            val header = lines.firstOrNull()
            if (header != "id,word,pos,definition,tag") {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,
                        "文件格式不正确，请选择正确的备份文件",
                        Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            for (line in lines.drop(1)) {
                val parts = line.split(",")
                if (parts.size >= 5) { // 使用 >= 以处理可能的特殊情况
                    try {
                        val id = parts[0].toIntOrNull() ?: 0
                        val word = parts[1]
                        val pos = parts[2]
                        // 保证definition和tag能够正确处理，即使有逗号
                        val definition = if (parts.size > 5) {
                            // 如果definition包含逗号，则合并多个部分
                            parts.slice(3 until parts.size - 1).joinToString(",")
                        } else {
                            parts[3]
                        }
                        val tag = parts.last()

                        words.add(
                            Word(
                                id = id,
                                word = word,
                                pos = pos,
                                definition = definition,
                                tag = tag
                            )
                        )
                    } catch (e : Exception) {
                        // 跳过错误的行，继续处理
                        continue
                    }
                }
            }

            if (words.isEmpty()) {
                throw IOException("未找到有效的单词数据")
            }
            // 清空数据库并插入新数据
            val db = AppDatabase.getDatabase(context)
            val wordDao = db.wordDao()

            wordDao.deleteAll()
            wordDao.insertAll(words)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "✅ 成功导入 ${words.size} 个单词", Toast.LENGTH_LONG).show()
            }
        } catch (e : Exception) {
            e.printStackTrace() // 打印详细错误信息以便调试
            withContext(Dispatchers.Main) {
                val message = when (e) {
                    is IOException       -> "文件读取失败：${e.message}"
                    is SecurityException -> "权限不足，无法读取文件"
                    else                 -> "导入失败：${e.message ?: "未知错误"}"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}