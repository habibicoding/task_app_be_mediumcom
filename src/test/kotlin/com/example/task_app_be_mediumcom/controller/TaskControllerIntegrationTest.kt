package com.example.task_app_be_mediumcom.controller

import com.example.task_app_be_mediumcom.data.model.Priority
import com.example.task_app_be_mediumcom.data.model.TaskCreateRequest
import com.example.task_app_be_mediumcom.data.model.TaskDto
import com.example.task_app_be_mediumcom.data.model.TaskUpdateRequest
import com.example.task_app_be_mediumcom.service.TaskService
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerIntegrationTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    private val taskId: Long = 33
    private val dummyDto1 = TaskDto(
        33,
        "test1",
        isReminderSet = false,
        isTaskOpen = false,
        createdOn = LocalDateTime.now(),
        priority = Priority.LOW
    )
    private val mapper = jacksonObjectMapper()


    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
    }

    @Test
    fun `given all tasks when fetch happen then check for size`() {
        // GIVEN
        val taskDto2 = TaskDto(
            44,
            "test2",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )

        // WHEN
        `when`(mockService.getAllTasks()).thenReturn(listOf(dummyDto1, taskDto2))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/all-tasks"))

        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(2))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for bad request`() {
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/404L"))

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val request = TaskUpdateRequest(
            "update task",
            isReminderSet = false,
            isTaskOpen = false,
            priority = Priority.LOW
        )
        val dummyDto = TaskDto(
            44,
            request.description ?: "",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )

        `when`(mockService.updateTask(dummyDto.id, request)).thenReturn(dummyDto)
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/update/${dummyDto.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value(dummyDto.description))
    }


    @Test
    fun `given open tasks when fetch happen then check for size and isTaskOpen is true`() {
        val taskDto2 = TaskDto(
            44,
            "test2",
            isReminderSet = false,
            isTaskOpen = true,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )

        `when`(mockService.getAllOpenTasks()).thenReturn(listOf(taskDto2))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/open-tasks"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(1))
        resultActions.andExpect(jsonPath("$[0].isTaskOpen").value(true))
    }

    @Test
    fun `given closed tasks when fetch happen then check for size  and isTaskOpen is false`() {
        // GIVEN
        // WHEN
        `when`(mockService.getAllClosedTasks()).thenReturn(listOf(dummyDto1))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/closed-tasks"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(1))
        resultActions.andExpect(jsonPath("$[0].isTaskOpen").value(false))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(33)).thenReturn(dummyDto1)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/${dummyDto1.id}"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value("test1"))
    }

    @Test
    fun `given create task request when task gets created then check for correct property`() {
        val request = TaskCreateRequest(
            0,
            "test for db",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )
        val taskDto = TaskDto(
            0,
            "test for db",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )

        `when`(mockService.createTask(request)).thenReturn(taskDto)
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.isTaskOpen").value(taskDto.isTaskOpen))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message`() {
        val expectedMessage = "Task with id: $taskId has been deleted."

        `when`(mockService.deleteTask(taskId)).thenReturn(expectedMessage)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/${taskId}"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().string(expectedMessage))
    }
}
