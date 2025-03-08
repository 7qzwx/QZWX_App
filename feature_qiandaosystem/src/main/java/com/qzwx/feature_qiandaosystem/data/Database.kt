package com.qzwx.feature_qiandaosystem.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [CheckIn::class, CheckInHistory::class], version = 1)
abstract class QZXTDatabase : RoomDatabase() {
    abstract fun checkInDao() : CheckInDao

    companion object {
        @Volatile
        private var INSTANCE : QZXTDatabase? = null

        fun getInstance(context : Context) : QZXTDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QZXTDatabase::class.java,
                    "Feature_QianDaoSystem"
                ).addCallback(WordDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // 数据库创建时候自动插入默认数据
        private class WordDatabaseCallback(private val context : Context) :
            Callback() {
            override fun onCreate(db : SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val wordDao = getInstance(context).checkInDao()
                    // 插入默认数据
                    populateDatabase(wordDao)
                }
            }

            private suspend fun populateDatabase(checkInDao : CheckInDao) {
                defaultWords.forEach { checkin ->
                    checkInDao.insertAllCheckIns(listOf(checkin))
                }
            }
        }

        fun exportDatabase(context : Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val checkInDao = getInstance(context).checkInDao()
                    val checkIns = checkInDao.getAllCheckInsSync()
                    val checkInHistories = checkInDao.getAllCheckInHistoriesSync()
                    val resolver = context.contentResolver
                    val dateFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, "checkIn_$currentDate.csv")
                        put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                        put(MediaStore.Downloads.RELATIVE_PATH, "Download/七种文学APP备份")
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }
                    val backupDir = File("/storage/emulated/0/Download/七种文学APP备份/")
                    if (backupDir.exists() && backupDir.isDirectory) {
                        val files = backupDir.listFiles()
                        files?.forEach { file ->
                            if (file.name.startsWith("checkIn_") && file.name.endsWith(".csv")) {
                                file.delete()
                            }
                        }
                    }

                    delay(800)
                    val newUri = resolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues.apply {
                            put(MediaStore.Downloads.IS_PENDING, 0)
                        }
                    ) ?: throw IOException("无法创建文件")

                    resolver.openOutputStream(newUri, "wt")?.use { outputStream ->
                        outputStream.write("id,name,experience,days,level,lastCheckInDate,isLocked,consecutiveDays\n".toByteArray())
                        checkIns.forEach { checkIn ->
                            val line =
                                "${checkIn.id},${checkIn.name},${checkIn.experience},${checkIn.days},${checkIn.level},${checkIn.lastCheckInDate},${checkIn.isLocked},${checkIn.consecutiveDays}\n"
                            outputStream.write(line.toByteArray())
                        }
                        outputStream.write("\nid,checkInName,date,experience,checkInCount,level\n".toByteArray())
                        checkInHistories.forEach { history ->
                            val line =
                                "${history.id},${history.checkInName},${history.date},${history.experience},${history.checkInCount},${history.level}\n"
                            outputStream.write(line.toByteArray())
                        }
                    }

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

        private fun isExternalStorageDocument(uri : Uri) : Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri : Uri) : Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun getDataColumn(
            context : Context,
            uri : Uri,
            selection : String?,
            selectionArgs : Array<String>?
        ) : String {
            context.contentResolver.query(uri,
                arrayOf(MediaStore.Downloads.DATA),
                selection,
                selectionArgs,
                null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA))
                }
            }
            return "未知路径"
        }

        // 修改后的导入逻辑
        fun importDatabase(context : Context, uri : Uri) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resolver : ContentResolver = context.contentResolver
                    val inputStream = resolver.openInputStream(uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val lines = reader.readLines()
                    reader.close()
                    val checkIns = mutableListOf<CheckIn>()
                    val checkInHistories = mutableListOf<CheckInHistory>()
                    var isCheckInSection = true

                    for (line in lines) {
                        if (line.isEmpty()) {
                            isCheckInSection = false
                            continue
                        }

                        if (isCheckInSection) {
                            if (line.startsWith("id")) continue // 跳过表头
                            val parts = line.split(",")
                            if (parts.size == 8) {
                                val checkIn = CheckIn(
                                    id = parts[0].toLong(),
                                    name = parts[1],
                                    experience = parts[2].toInt(),
                                    days = parts[3].toInt(),
                                    level = parts[4].toInt(),
                                    lastCheckInDate = parts[5],
                                    isLocked = parts[6].toBoolean(),
                                    consecutiveDays = parts[7].toInt()
                                )
                                checkIns.add(checkIn)
                            }
                        } else {
                            if (line.startsWith("id")) continue // 跳过表头
                            val parts = line.split(",")
                            if (parts.size == 6) {
                                val checkInHistory = CheckInHistory(
                                    id = parts[0].toLong(),
                                    checkInName = parts[1],
                                    date = parts[2],
                                    experience = parts[3].toInt(),
                                    checkInCount = parts[4].toInt(),
                                    level = parts[5].toInt()
                                )
                                checkInHistories.add(checkInHistory)
                            }
                        }
                    }
                    val checkInDao = getInstance(context).checkInDao()
                    checkInDao.deleteAllCheckIns()
                    checkInDao.deleteAllCheckInHistories()
                    checkInDao.insertAllCheckIns(checkIns)
                    checkInDao.insertAllCheckInHistories(checkInHistories)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ 导入成功！", Toast.LENGTH_LONG).show()
                    }
                } catch (e : Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ 导入失败：${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}