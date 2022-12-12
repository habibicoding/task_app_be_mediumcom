package com.example.task_app_be_mediumcom.data.model

data class AdaptTaskRequest(
    val description: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val priority: Priority?
)
