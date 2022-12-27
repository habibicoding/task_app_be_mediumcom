package com.example.task_app_be_mediumcom.service

import com.example.task_app_be_mediumcom.data.Task
import com.example.task_app_be_mediumcom.data.model.Priority
import com.example.task_app_be_mediumcom.data.model.TaskCreateRequest
import com.example.task_app_be_mediumcom.data.model.TaskDto
import com.example.task_app_be_mediumcom.data.model.TaskUpdateRequest
import com.example.task_app_be_mediumcom.exception.BadRequestException
import com.example.task_app_be_mediumcom.exception.TaskNotFoundException
import com.example.task_app_be_mediumcom.repository.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Assertions.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime


@ExtendWith(MockKExtension::class)
internal class TaskServiceTest {

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskService

    private val task = Task()
    private lateinit var createRequest: TaskCreateRequest


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createRequest = TaskCreateRequest(
            "test task",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW
        )
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        // GIVEN
        val expectedTasks = listOf(Task(), Task())

        // WHEN
        every { mockRepository.findAll() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllTasks()

        // THEN
        assertThat(actualList.size).isEqualTo(expectedTasks.size)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = createRequest.createdOn

        every { mockRepository.save(any()) } returns task
        val actualTaskDto: TaskDto = objectUnderTest.createTask(createRequest)

        assertThat(actualTaskDto.description).isEqualTo(task.description)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        every { mockRepository.doesDescriptionExist(any()) } returns true

        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(createRequest) }

        assertThat(exception.message).isEqualTo("There is already a task with description: test task")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        every { mockRepository.existsById(any()) } returns false

        val exception = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(123) }

        assertThat(exception.message).isEqualTo("Task with ID: 123 does not exist!")
        verify { mockRepository.findTaskById(any()) wasNot called }
    }

    @Test
    fun `when open tasks get fetched then check if the first property has true for isTaskOpen`() {
        task.isTaskOpen = true
        val expectedTasks = listOf(task)
        every { mockRepository.queryAllOpenTasks() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllOpenTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(true)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has false for isTaskOpen`() {
        task.isTaskOpen = false
        val expectedTasks = listOf(task)
        every { mockRepository.queryAllClosedTasks() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllClosedTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(false)
    }

    @Test
    fun `when save task is called then check if argument could be captured`() {
        val taskSlot = slot<Task>()
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = createRequest.createdOn

        every { mockRepository.save(capture(taskSlot)) } returns task
        val actualTaskDto: TaskDto = objectUnderTest.createTask(createRequest)

        verify { mockRepository.save(capture(taskSlot)) }
        assertThat(taskSlot.captured.id).isEqualTo(actualTaskDto.id)
        assertThat(taskSlot.captured.description).isEqualTo(actualTaskDto.description)
        assertThat(taskSlot.captured.priority).isEqualTo(actualTaskDto.priority)
    }

    @Test
    fun `when get task by id is called then expect a specific description`() {
        task.description = "getTaskById"
        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        val taskDto = objectUnderTest.getTaskById(1234)

        assertThat(taskDto.description).isEqualTo("getTaskById")
    }

    @Test
    fun `when find task by id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(capture(taskIdSlot)) } returns task
        objectUnderTest.getTaskById(2345)

        verify { mockRepository.findTaskById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(2345)
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        val taskId: Long = 1234

        every { mockRepository.existsById(any()) } returns true
        val deleteTaskMsg: String = objectUnderTest.deleteTask(taskId)

        assertThat(deleteTaskMsg).isEqualTo("Task with id: $taskId has been deleted.")
    }


    @Test
    fun `when delete by task id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.deleteById(capture(taskIdSlot)) } returns Unit
        objectUnderTest.deleteTask(234)

        verify { mockRepository.deleteById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(234)
    }

    @Test
    fun `when update task is called with task request argument then expect specific description fpr actual task`() {
        task.description = "test task"
        val updateRequest =
            TaskUpdateRequest(
                task.description,
                isReminderSet = false,
                isTaskOpen = false,
                priority = Priority.LOW
            )

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        every { mockRepository.save(any()) } returns task
        val actualTask = objectUnderTest.updateTask(task.id, updateRequest)

        assertThat(actualTask.description).isEqualTo("test task")
    }
}