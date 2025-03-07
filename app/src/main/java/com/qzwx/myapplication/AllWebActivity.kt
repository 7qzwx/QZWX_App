package com.qzwx.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.myapplication.data.LinkDao
import com.qzwx.myapplication.data.WebAppDatabase
import com.qzwx.myapplication.ui.AllWebScreen
import com.qzwx.myapplication.ui.REQUEST_CODE_IMPORT
import com.qzwx.myapplication.ui.handleImportResult
import com.qzwx.myapplication.viewmodel.LinkViewModel
import com.qzwx.myapplication.viewmodel.LinkViewModelFactory

class AllWebActivity : ComponentActivity() {
    private lateinit var linkViewModel : LinkViewModel
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val linkDao : LinkDao = WebAppDatabase.getDatabase(this).linkDao()
        linkViewModel =
            ViewModelProvider(this, LinkViewModelFactory(linkDao))[LinkViewModel::class.java]
        setContent {
            QZWX_AppTheme {
                AllWebScreen(linkViewModel, this)
            }
        }
    }
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMPORT && resultCode == RESULT_OK) {
            handleImportResult(linkViewModel, this, data)
        }
    }
}