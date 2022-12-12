package com.example.task_app_be_mediumcom.service

import com.example.task_app_be_mediumcom.data.Task
import com.example.task_app_be_mediumcom.data.model.AdaptTaskRequest
import com.example.task_app_be_mediumcom.data.model.NewTaskRequest
import com.example.task_app_be_mediumcom.data.model.TaskDto
import com.example.task_app_be_mediumcom.exception.BadRequestException
import com.example.task_app_be_mediumcom.exception.TaskNotFoundException
import com.example.task_app_be_mediumcom.repository.TaskRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import kotlin.reflect.full.memberProperties
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field

@Service
class TaskService(private val repository: TaskRepository) {

    private fun convertEntityToDto(task: Task): TaskDto {
        return TaskDto(
            task.id,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.priority
        )
    }

    private fun assignValuesToEntity(task: Task, taskRequest: NewTaskRequest) {
        task.description = taskRequest.description
        task.isReminderSet = taskRequest.isReminderSet
        task.isTaskOpen = taskRequest.isTaskOpen
        task.createdOn = taskRequest.createdOn
        task.priority = taskRequest.priority
    }

    private fun checkForTaskId(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
    }

    fun getAllTasks(): List<TaskDto> =
        repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllOpenTasks(): List<TaskDto> =
        repository.queryAllOpenTasks().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllClosedTasks(): List<TaskDto> =
        repository.queryAllClosedTasks().stream().map(this::convertEntityToDto).collect(Collectors.toList())


    fun getTaskById(id: Long): TaskDto {
        checkForTaskId(id)
        val task: Task = repository.findTaskById(id)
        return convertEntityToDto(task)
    }

    fun createTask(newTaskRequest: NewTaskRequest): Task {
        if (repository.doesDescriptionExist(newTaskRequest.description)) {
            throw BadRequestException("There is already a task with description: ${newTaskRequest.description}")
        }
        val task = Task()
        assignValuesToEntity(task, newTaskRequest)
        return repository.save(task)
    }

    fun updateTask(id: Long, adaptTaskRequest: AdaptTaskRequest): TaskDto {
        checkForTaskId(id)
        val existingTask: Task = repository.findTaskById(id)

        for (prop in AdaptTaskRequest::class.memberProperties) {
            if (prop.get(adaptTaskRequest) != null) {
                val field: Field? = ReflectionUtils.findField(Task::class.java, prop.name)
                field?.let {
                    it.isAccessible = true
                    ReflectionUtils.setField(it, existingTask, prop.get(adaptTaskRequest))
                }
            }
        }

        val savedTask: Task = repository.save(existingTask)
        return convertEntityToDto(savedTask)
    }

    fun deleteTask(id: Long): String {
        checkForTaskId(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }
}