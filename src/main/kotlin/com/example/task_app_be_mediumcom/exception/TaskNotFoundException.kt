package com.example.task_app_be_mediumcom.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
data class TaskNotFoundException(override val message: String) : RuntimeException()