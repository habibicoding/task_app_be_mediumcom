package com.example.task_app_be_mediumcom.repository

import com.example.task_app_be_mediumcom.data.Task
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.assertj.core.api.Assertions.assertThat

@DataJpaTest(properties = ["spring.jpa.properties.javax.persistence.validation.mode=none"])
internal class TaskRepositoryTestEmbedded {

    @Autowired
    private lateinit var objectUnderTest: TaskRepository

    private val numberOfRecordsInTestDataSql = 3
    private val numberOfClosedTasksInTestDataSql = 2
    private val numberOfOpenTasksInTestDataSql = 1

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check if it is not null`() {
        // GIVEN
        // WHEN
        val task: Task = objectUnderTest.findTaskById(111)

        // THEN
        assertThat(task).isNotNull
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of tasks`() {
        val tasks: List<Task> = objectUnderTest.findAll()
        assertThat(tasks.size).isEqualTo(numberOfRecordsInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then remove it by id`() {
        objectUnderTest.deleteById(112)
        val tasks: List<Task> = objectUnderTest.findAll()
        assertThat(tasks.size).isEqualTo(numberOfRecordsInTestDataSql - 1)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of open tasks`() {
        val tasks: List<Task> = objectUnderTest.queryAllOpenTasks()
        assertThat(tasks.size).isEqualTo(numberOfOpenTasksInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of closed tasks`() {
        val tasks: List<Task> = objectUnderTest.queryAllClosedTasks()
        assertThat(tasks.size).isEqualTo(numberOfClosedTasksInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when description is queried then check if descriptions already exists`() {
        val isDescriptionAlreadyGiven1 = objectUnderTest.doesDescriptionExist("first test todo")
        val isDescriptionAlreadyGiven2 = objectUnderTest.doesDescriptionExist("any todo")

        assertThat(isDescriptionAlreadyGiven1).isTrue
        assertThat(isDescriptionAlreadyGiven2).isFalse
    }
}