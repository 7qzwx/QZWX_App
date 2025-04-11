package com.qzwx.feature_todoanddone

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_todoanddone.data.TaskAppDatabase
import com.qzwx.feature_todoanddone.data.TaskRepository
import com.qzwx.feature_todoanddone.ui.AddTaskDialogComponent
import com.qzwx.feature_todoanddone.ui.EmptyComponent
import com.qzwx.feature_todoanddone.ui.TaskCardComponent
import com.qzwx.feature_todoanddone.ui.WelcomeMessageComponent
import com.qzwx.feature_todoanddone.viewmodel.TaskViewModel
import com.qzwx.feature_todoanddone.viewmodel.TaskViewModelFactory

class TodoAndDoneActivity : ComponentActivity() {
    private lateinit var taskViewModel : TaskViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            val taskAppDatabase = TaskAppDatabase.getInstance(this)
            val taskRepository = TaskRepository(taskAppDatabase)
            val factory = TaskViewModelFactory(taskRepository)
            taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)
            val taskUiState = taskViewModel.tasks.collectAsState().value
            val dialogUiState = taskViewModel.dialogUiState.value

            QZWX_AppTheme {
                Scaffold(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            text = {
                                Text(
                                    text = "Add Todo",
                                    color = Color.White
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.Rounded.AddCircle,
                                    contentDescription = "Add Todo",
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                taskViewModel.showDialog(true)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(8.dp) // 使用正确的 Material 3 默认值
                        )
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) {
                    if (taskUiState.tasks.isNullOrEmpty()) {
                        EmptyComponent()
                    }
                    if (dialogUiState.showDialog) {
                        AddTaskDialogComponent(
                            setTaskTitle = { title ->
                                taskViewModel.setTaskTitle(title)
                            },
                            setTaskBody = { body ->
                                taskViewModel.setTaskBody(body)
                            },
                            saveTask = {
                                taskViewModel.addTask()
                            },
                            dialogUiState = dialogUiState,
                            closeDialog = {
                                taskViewModel.showDialog(false)
                            }
                        )
                    }
                    if (!taskUiState.tasks.isNullOrEmpty()) {
                        LazyColumn(contentPadding = PaddingValues(14.dp)) {
                            item {
                                WelcomeMessageComponent()
                                Spacer(modifier = Modifier.height(30.dp))
                            }

                            items(taskUiState.tasks ?: emptyList()) { task ->
                                TaskCardComponent(
                                    title = task.title,
                                    body = task.body,
                                    id = task.id,
                                    deleteTask = { id ->
                                        taskViewModel.deleteTask(id)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

