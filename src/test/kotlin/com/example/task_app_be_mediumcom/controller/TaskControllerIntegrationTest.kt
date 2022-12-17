package com.example.task_app_be_mediumcom.controller

import com.example.task_app_be_mediumcom.data.model.Priority
import com.example.task_app_be_mediumcom.data.model.TaskDto
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

    /*
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
        every { mockService.getAllTasks() } returns listOf(dummyDto1, taskDto2)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/all-tasks"))

        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(2))
    }*/
}