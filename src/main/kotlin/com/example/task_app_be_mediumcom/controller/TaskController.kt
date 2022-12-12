package com.example.task_app_be_mediumcom.controller

import com.example.task_app_be_mediumcom.data.model.AdaptTaskRequest
import com.example.task_app_be_mediumcom.data.model.NewTaskRequest
import com.example.task_app_be_mediumcom.data.model.TaskDto
import com.example.task_app_be_mediumcom.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class TaskController(private val service: TaskService) {

    @GetMapping("all-tasks")
    fun getAllTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllTasks(), HttpStatus.OK)

    @GetMapping("open-tasks")
    fun getAllOpenTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllOpenTasks(), HttpStatus.OK)

    @GetMapping("closed-tasks")
    fun getAllClosedTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity(service.getAllClosedTasks(), HttpStatus.OK)

    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> =
        ResponseEntity(service.getTaskById(id), HttpStatus.OK)

    @PostMapping("create")
    fun createTask(
        @Valid @RequestBody newTaskRequest: NewTaskRequest
    ): ResponseEntity<TaskDto> = ResponseEntity(service.createTask(newTaskRequest), HttpStatus.OK)

    @PatchMapping("update/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody adaptTaskRequest: AdaptTaskRequest
    ): ResponseEntity<TaskDto> = ResponseEntity(service.updateTask(id, adaptTaskRequest), HttpStatus.OK)

    @DeleteMapping("delete/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<String> =
        ResponseEntity(service.deleteTask(id), HttpStatus.OK)
}