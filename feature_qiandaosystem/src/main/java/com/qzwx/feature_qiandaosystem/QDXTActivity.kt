package com.qzwx.feature_qiandaosystem

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_qiandaosystem.data.CheckInDao
import com.qzwx.feature_qiandaosystem.data.CheckInRepository
import com.qzwx.feature_qiandaosystem.data.CheckInRepositoryImpl
import com.qzwx.feature_qiandaosystem.data.QZXTDatabase
import com.qzwx.feature_qiandaosystem.navigation.NavGraph
import com.qzwx.feature_qiandaosystem.viewmodel.CheckInViewModel

class QDXTActivity : ComponentActivity() {
    private lateinit var checkInRepository : CheckInRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val database : QZXTDatabase = QZXTDatabase.getInstance(this)
        val checkInDao : CheckInDao = database.checkInDao()
        checkInRepository = CheckInRepositoryImpl(checkInDao)

        setContent {
            QZWX_AppTheme {
                // 使用 rememberNavController 和 viewModel 提供导航和 ViewModel
                val navController = rememberNavController()
                val checkInViewModel = CheckInViewModel(checkInRepository)

                NavGraph(checkInRepository = checkInRepository, checkInViewModel = checkInViewModel)
            }
        }
    }
}