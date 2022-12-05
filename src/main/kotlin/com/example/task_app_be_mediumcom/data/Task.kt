package com.example.task_app_be_mediumcom.data

import com.example.task_app_be_mediumcom.data.model.Priority
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "task", uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])])
class Task {

    @Id
    @GeneratedValue(generator = "task_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1)
    val id: Long = 0

    @NotBlank
    @Column(name = "description", nullable = false, unique = true)
    var description: String = ""

    @Column(name = "is_reminder_set", nullable = false)
    var isReminderSet: Boolean = false

    @Column(name = "is_task_open", nullable = false)
    var isTaskOpen: Boolean = true

    @Column(name = "created_on", nullable = false)
    var createdOn: LocalDateTime = LocalDateTime.now()

    @NotNull
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW
}