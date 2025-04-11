package com.qzwx.feature_todoanddone.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(taskAppDatabase : TaskAppDatabase) {
    private val taskDao = taskAppDatabase.taskDao()

    suspend fun addTask(title : String, body : String) {
        val task = Task(
            title = title,
            body = body
        )

        taskDao.addTask(task = task)
    }

    suspend fun getAllTasks() : List<Task> {
        var allTasks : List<Task>

        withContext(Dispatchers.IO) {
            allTasks = taskDao.getAllTask()
        }
        return allTasks
    }

    suspend fun deleteTask(id : Int) {
        withContext(Dispatchers.IO) {
            val task = taskDao.getTask(id = id)

            taskDao.deleteTask(task = task)
        }
    }
}