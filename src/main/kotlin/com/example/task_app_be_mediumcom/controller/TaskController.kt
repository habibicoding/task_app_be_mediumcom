package com.example.task_app_be_mediumcom.controller

import com.example.task_app_be_mediumcom.data.model.TaskDto
import com.example.task_app_be_mediumcom.data.model.TaskRequest
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
    fun createTask(@Valid @RequestBody taskRequest: TaskRequest): ResponseEntity<TaskDto> {
        val task = service.createTask(taskRequest)
        return ResponseEntity(
            TaskDto(
                task.id,
                task.description,
                task.isReminderSet,
                task.isTaskOpen,
                task.createdOn,
                task.priority
            ), HttpStatus.OK
        )
    }

    @PutMapping("update")
    fun updateTask(@Valid @RequestBody taskRequest: TaskRequest): ResponseEntity<TaskDto> =
        ResponseEntity(service.updateTask(taskRequest), HttpStatus.OK)

    @DeleteMapping("delete/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<String> =
        ResponseEntity(service.deleteTask(id), HttpStatus.OK)
}