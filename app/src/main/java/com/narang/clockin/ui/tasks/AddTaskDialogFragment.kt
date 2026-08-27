package com.narang.clockin.ui.tasks

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.narang.clockin.data.model.Priority
import com.narang.clockin.data.model.Task
import com.narang.clockin.databinding.DialogAddTaskBinding

class AddTaskDialogFragment : DialogFragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels({ requireParentFragment() })

    private var editingTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Check the suitcase
        @Suppress("DEPRECATION")
        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("key_task", Task::class.java)
        } else {
            arguments?.getParcelable<Task>("key_task")
        }
        editingTask = task

        // 2. The "if" statement
        if (task != null) {
            // You are in Edit Mode.
            binding.dialogTitle.text = "Edit Task"
            binding.editTaskTitle.setText(task.title)
            // Prefill additional fields for a complete edit experience
            binding.switchPriority.isChecked = task.priority == Priority.HIGH.name
            when (task.tag) {
                "Work" -> binding.chipGroupTags.check(com.narang.clockin.R.id.chipWork)
                "Personal" -> binding.chipGroupTags.check(com.narang.clockin.R.id.chipPersonal)
                "Urgent" -> binding.chipGroupTags.check(com.narang.clockin.R.id.chipUrgent)
            }
        } else {
            // You are in Add Mode.
            binding.dialogTitle.text = "Add Task"
        }

        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        setupDialogWindow()
    }

    private fun setupDialogWindow() {
        dialog?.window?.apply {
            // Make the dialog background transparent so the card shape is visible
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set width to match parent with some margin
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Implement blurry background for Android 12+ (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                attributes.blurBehindRadius = 60 // Adjust intensity here
            } else {
                // Fallback for older versions: increase dim intensity
                setDimAmount(0.7f)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveTask.setOnClickListener {
            val title = binding.editTaskTitle.text.toString().trim()
            
            val selectedChipId = binding.chipGroupTags.checkedChipId
            val tag = if (selectedChipId != View.NO_ID) {
                val chip = binding.chipGroupTags.findViewById<com.google.android.material.chip.Chip>(selectedChipId)
                chip.text.toString()
            } else ""
            
            val isHighPriority = binding.switchPriority.isChecked

            if (title.isNotEmpty()) {
                val currentEditingTask = editingTask
                if (currentEditingTask != null) {
                    // Edit Mode: update existing task
                    val updatedTask = currentEditingTask.copy(
                        title = title,
                        tag = tag,
                        priority = if (isHighPriority) Priority.HIGH.name else Priority.NORMAL.name
                    )
                    viewModel.updateTask(updatedTask)
                } else {
                    // Add Mode: create new task
                    val newTask = Task(
                        title = title,
                        tag = tag,
                        priority = if (isHighPriority) Priority.HIGH.name else Priority.NORMAL.name,
                        dueDateTime = System.currentTimeMillis(), // Default to now
                        dueDateLabel = "Today"
                    )
                    viewModel.addNewTask(newTask)
                }
                dismiss()
            } else {
                binding.editTaskTitle.error = "Title is required"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddTaskDialogFragment"
        private const val ARG_TASK = "key_task"

        fun newInstance() = AddTaskDialogFragment()

        fun newInstance(task: Task) = AddTaskDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_TASK, task)
            }
        }
    }
}
