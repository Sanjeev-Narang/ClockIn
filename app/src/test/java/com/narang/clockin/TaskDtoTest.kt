package com.narang.clockin

import com.narang.clockin.data.TaskDto
import com.narang.clockin.data.toDomain
import com.narang.clockin.domain.Priority
import com.narang.clockin.domain.Section
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TaskDtoTest {

    @Test
    fun fullDto_mapsToDomain() {
        val dto = TaskDto(
            id = "doc1",
            title = "Call dentist",
            tag = "Personal",
            priority = Priority.HIGH.name,
            isCompleted = true
        )

        val task = dto.toDomain()

        assertEquals("doc1", task.id)
        assertEquals("Call dentist", task.title)
        assertEquals("Personal", task.tag)
        assertEquals(Priority.HIGH.name, task.priority)
        assertEquals(true, task.isCompleted)
        assertEquals(Section.HIGH_PRIORITY, task.section)
    }

    @Test
    fun nullFields_yieldDefaults() {
        val dto = TaskDto(
            id = "doc1",
            title = null,
            tag = null,
            priority = null,
            isCompleted = null
        )

        val task = dto.toDomain()

        assertEquals("doc1", task.id)
        assertEquals("", task.title)
        assertEquals("", task.tag)
        assertEquals(Priority.NORMAL.name, task.priority)
        assertFalse(task.isCompleted)
        assertEquals(Section.UPCOMING, task.section)
    }

    @Test
    fun normalPriority_mapsToUpcomingSection() {
        val dto = TaskDto(
            id = "doc1",
            title = "Buy milk",
            tag = "",
            priority = Priority.NORMAL.name,
            isCompleted = false
        )

        assertEquals(Section.UPCOMING, dto.toDomain().section)
    }
}
