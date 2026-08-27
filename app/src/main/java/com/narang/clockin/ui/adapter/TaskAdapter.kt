package com.narang.clockin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.narang.clockin.databinding.ItemSectionHeaderBinding
import com.narang.clockin.databinding.ItemTaskBinding
import com.narang.clockin.data.model.Task

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_TASK = 1

class TasksAdapter(
    private val onTaskChecked: (task: Task, checked: Boolean) -> Unit,
    private val onTaskMenuClicked: (task: Task) -> Unit
) : ListAdapter<TaskListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is TaskListItem.Header -> VIEW_TYPE_HEADER
        is TaskListItem.TaskRow -> VIEW_TYPE_TASK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(ItemSectionHeaderBinding.inflate(inflater, parent, false))
        } else {
            TaskViewHolder(
                ItemTaskBinding.inflate(inflater, parent, false),
                onTaskChecked,
                onTaskMenuClicked
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TaskListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is TaskListItem.TaskRow -> (holder as TaskViewHolder).bind(item.task)
        }
    }

    class HeaderViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: TaskListItem.Header) {
            binding.textSectionTitle.text = header.title
            binding.textSectionCount.text = "${header.count} Task${if (header.count == 1) "" else "s"}"
        }
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onTaskChecked: (Task, Boolean) -> Unit,
        private val onTaskMenuClicked: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.checkboxTask.setOnCheckedChangeListener(null)
            binding.textTaskTitle.text = task.title
            binding.textDueDate.text = task.dueDateLabel
            binding.checkboxTask.isChecked = task.isCompleted

            if (task.tag.isNotBlank()) {
                binding.chipTag.text = task.tag
                binding.chipTag.visibility = android.view.View.VISIBLE
            } else {
                binding.chipTag.visibility = android.view.View.GONE
            }

            binding.checkboxTask.setOnCheckedChangeListener { _, isChecked ->
                onTaskChecked(task, isChecked)
            }
            binding.buttonOverflow.setOnClickListener { onTaskMenuClicked(task) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskListItem>() {
            override fun areItemsTheSame(old: TaskListItem, new: TaskListItem) =
                old.stableId == new.stableId

            override fun areContentsTheSame(old: TaskListItem, new: TaskListItem) =
                old == new
        }
    }
}
