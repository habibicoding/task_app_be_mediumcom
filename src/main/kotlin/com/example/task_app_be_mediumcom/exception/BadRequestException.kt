package com.example.task_app_be_mediumcom.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
data class BadRequestException(override val message: String) : RuntimeException()