package com.narang.clockin.ui.adapter

import android.content.Context
import com.narang.clockin.domain.Section
import com.narang.clockin.domain.Task

sealed class TaskListItem {
    data class Header(val title: String, val count: Int) : TaskListItem()
    data class TaskRow(val task: Task) : TaskListItem()

    /** Stable id used by DiffUtil / ListAdapter */
    val stableId: String
        get() = when (this) {
            is Header -> "header_$title"
            is TaskRow -> "task_${task.id}"
        }
}

/** Builds the flat list fed to the adapter from the two grouped lists in UiState. */
fun buildTaskListItems(
    context: Context,
    highPriority: List<Task>,
    upcoming: List<Task>
): List<TaskListItem> = buildList {
    if (highPriority.isNotEmpty()) {
        add(TaskListItem.Header(context.getString(Section.HIGH_PRIORITY.labelRes), highPriority.size))
        highPriority.forEach { add(TaskListItem.TaskRow(it)) }
    }
    if (upcoming.isNotEmpty()) {
        add(TaskListItem.Header(context.getString(Section.UPCOMING.labelRes), upcoming.size))
        upcoming.forEach { add(TaskListItem.TaskRow(it)) }
    }
}
