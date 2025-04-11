package com.qzwx.feature_todoanddone.viewmodel

import com.qzwx.feature_todoanddone.data.Task

data class DialogUiState(
    val showDialog: Boolean = false,
    val taskTitle: String = "",
    val taskBody: String = ""
)

data class TaskUiState(
    val tasks: List<Task>? = null,
    val errorMessage: String? = null,
    val taskToBeDeleted: Task? = null,
)
