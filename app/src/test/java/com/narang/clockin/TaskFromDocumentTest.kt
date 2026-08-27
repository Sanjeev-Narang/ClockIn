package com.narang.clockin

import com.google.firebase.Timestamp
import com.narang.clockin.data.model.Priority
import com.narang.clockin.data.model.Section
import com.narang.clockin.data.model.Task
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskFromDocumentTest {

    @Test
    fun emptyDocument_yieldsDefaults() {
        val task = Task.fromData(emptyMap(), id = "doc1")

        assertEquals("doc1", task.id)
        assertEquals("", task.title)
        assertEquals(0L, task.dueDateTime)
        assertEquals("", task.dueDateLabel)
        assertEquals("", task.tag)
        assertEquals(Priority.NORMAL.name, task.priority)
        assertFalse(task.isCompleted)
        assertEquals(Task.SCHEMA_VERSION, task.schemaVersion)
        assertEquals(Section.UPCOMING, task.section)
    }

    @Test
    fun nullData_yieldsDefaults() {
        val task = Task.fromData(null, id = "doc1")

        assertEquals("doc1", task.id)
        assertEquals("", task.title)
        assertEquals(Section.UPCOMING, task.section)
    }

    @Test
    fun dueDateTime_asLong_isCoerced() {
        val task = Task.fromData(mapOf("dueDateTime" to 1_700_000_000_000L), id = "doc1")

        assertEquals(1_700_000_000_000L, task.dueDateTime)
    }

    @Test
    fun dueDateTime_asDouble_isCoerced() {
        val task = Task.fromData(mapOf("dueDateTime" to 1_700_000_000_000.0), id = "doc1")

        assertEquals(1_700_000_000_000L, task.dueDateTime)
    }

    @Test
    fun dueDateTime_asTimestamp_isConvertedToMillis() {
        val date = Date(1_700_000_000_000L)
        val task = Task.fromData(mapOf("dueDateTime" to Timestamp(date)), id = "doc1")

        assertEquals(date.time, task.dueDateTime)
    }

    @Test
    fun isCompleted_asString_isParsed() {
        val task = Task.fromData(mapOf("isCompleted" to "true"), id = "doc1")

        assertTrue(task.isCompleted)
    }

    @Test
    fun missingPriority_defaultsToNormalAndUpcoming() {
        val task = Task.fromData(mapOf("title" to "Buy milk"), id = "doc1")

        assertEquals(Priority.NORMAL.name, task.priority)
        assertEquals(Section.UPCOMING, task.section)
    }

    @Test
    fun highPriority_mapsToHighPrioritySection() {
        val task = Task.fromData(mapOf("priority" to Priority.HIGH.name), id = "doc1")

        assertEquals(Section.HIGH_PRIORITY, task.section)
    }

    @Test
    fun extraUnknownFields_areIgnored() {
        val task = Task.fromData(
            mapOf("title" to "Plan trip", "unexpectedField" to listOf(1, 2, 3), "nested" to mapOf("a" to 1)),
            id = "doc1"
        )

        assertEquals("Plan trip", task.title)
    }

    @Test
    fun schemaVersion_isPreserved() {
        val task = Task.fromData(mapOf("schemaVersion" to 3), id = "doc1")

        assertEquals(3, task.schemaVersion)
    }

    @Test
    fun toMap_writesExplicitVersionedShapeWithoutId() {
        val task = Task(
            title = "Call dentist",
            dueDateTime = 123L,
            dueDateLabel = "Today, 2:00 PM",
            tag = "Personal",
            priority = Priority.HIGH.name,
            isCompleted = true
        )

        val map = task.toMap()

        assertEquals("Call dentist", map["title"])
        assertEquals(123L, map["dueDateTime"])
        assertEquals("Today, 2:00 PM", map["dueDateLabel"])
        assertEquals("Personal", map["tag"])
        assertEquals(Priority.HIGH.name, map["priority"])
        assertEquals(true, map["isCompleted"])
        assertEquals(Task.SCHEMA_VERSION, map["schemaVersion"])
        assertEquals(7, map.size)
        assertFalse("id must not be persisted", map.containsKey("id"))
    }
}
