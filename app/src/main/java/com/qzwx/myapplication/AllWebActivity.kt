package com.qzwx.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.myapplication.data.WebAppDatabase
import com.qzwx.myapplication.data.LinkDao
import com.qzwx.myapplication.ui.AllWebScreen
import com.qzwx.myapplication.viewmodel.LinkViewModel
import com.qzwx.myapplication.viewmodel.LinkViewModelFactory

class AllWebActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val linkDao : LinkDao = WebAppDatabase.getDatabase(this).linkDao()
        val linkViewModel : LinkViewModel =
            ViewModelProvider(this, LinkViewModelFactory(linkDao))[LinkViewModel::class.java]

        setContent {
            QZWX_AppTheme {
                AllWebScreen(linkViewModel)
            }
        }
    }
}