package com.qzwx.feature_qiandaosystem

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.qzwx.core.room.database.AppDatabase
import com.qzwx.core.room.database.CheckInRepositoryImpl
import com.qzwx.core.room.room_qiandaosystem.CheckInDao
import com.qzwx.core.room.room_qiandaosystem.CheckInRepository
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_qiandaosystem.navigation.NavGraph

class QDXTActivity : ComponentActivity() {
    private lateinit var checkInRepository : CheckInRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val database : AppDatabase = AppDatabase.getInstance(this)
        val checkInDao : CheckInDao = database.checkInDao()
        checkInRepository = CheckInRepositoryImpl(checkInDao)
        setContent {
            QZWX_AppTheme {
                NavGraph(checkInRepository = checkInRepository)
            }
        }

    }
}
