package com.qzwx.qzwxapp

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.lifecycle.*
import com.qzwx.core.theme.*
import com.qzwx.qzwxapp.data.*
import com.qzwx.qzwxapp.ui.*
import com.qzwx.qzwxapp.viewmodel.*

class AllWebActivity : ComponentActivity() {
    private lateinit var linkViewModel : LinkViewModel
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val linkDao : LinkDao = WebAppDatabase.getDatabase(this).linkDao()
        linkViewModel = ViewModelProvider(
            this,
            LinkViewModelFactory(linkDao)
        )[LinkViewModel::class.java]
        setContent {
            QZWX_AppTheme {
                AllWebScreen(
                    linkViewModel,
                    this
                )
            }
        }
    }

    override fun onActivityResult(requestCode : Int,resultCode : Int,data : Intent?) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (requestCode == REQUEST_CODE_IMPORT && resultCode == RESULT_OK) {
            handleImportResult(
                linkViewModel,
                this,
                data
            )
        }
    }
}