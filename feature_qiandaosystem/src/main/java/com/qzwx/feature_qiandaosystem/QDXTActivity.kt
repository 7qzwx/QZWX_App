package com.qzwx.feature_qiandaosystem

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_qiandaosystem.data.AppDatabase
import com.qzwx.feature_qiandaosystem.data.CheckInDao
import com.qzwx.feature_qiandaosystem.data.CheckInRepository
import com.qzwx.feature_qiandaosystem.data.CheckInRepositoryImpl
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
