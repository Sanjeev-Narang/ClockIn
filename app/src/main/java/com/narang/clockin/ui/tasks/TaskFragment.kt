package com.narang.clockin.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.narang.clockin.R
import com.narang.clockin.data.model.Task
import com.narang.clockin.data.repository.FirestoreTaskRepository
import com.narang.clockin.databinding.FragmentTasksBinding
import com.narang.clockin.ui.adapter.TasksAdapter
import com.narang.clockin.ui.adapter.buildTaskListItems
import kotlinx.coroutines.launch

class TaskFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        TaskViewModelFactory(FirestoreTaskRepository(), userId)
    }

    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TasksAdapter(
            onTaskChecked = { task, checked -> viewModel.onTaskCheckedChanged(task, checked) },
            onTaskMenuClicked = { task -> showTaskMenu(task) }
        )
        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        binding.fabAddTask.setOnClickListener {
            AddTaskDialogFragment.newInstance()
                .show(childFragmentManager, AddTaskDialogFragment.TAG)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: TaskUiState) {
        binding.textFocusSubtitle.text =
            "You have ${state.totalHighPriorityCount} high priority tasks remaining."

        adapter.submitList(buildTaskListItems(requireContext(), state.highPriorityTasks, state.upcomingTasks))

        binding.progressLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        
        // Only show empty state if not loading and both lists are empty
        val showEmpty = !state.isLoading && state.isEmpty
        binding.recyclerTasks.visibility = if (showEmpty) View.GONE else View.VISIBLE
        binding.emptyStateContainer.visibility = if (showEmpty) View.VISIBLE else View.GONE

        state.errorMessage?.let {
            com.google.android.material.snackbar.Snackbar
                .make(binding.root, "Error: $it", com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun showTaskMenu(task: Task) {
        val context = requireContext()
        val options = TaskMenuOption.entries.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(task.title)
            .setItems(options.map { context.getString(it.actionTextId) }.toTypedArray()) { _, index ->
                when (options[index]) {
                    TaskMenuOption.EDIT_TASK -> editTask(task)
                    TaskMenuOption.DELETE_TASK -> viewModel.onDeleteTask(task)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun editTask(task: Task) {
        AddTaskDialogFragment.newInstance(task)
            .show(childFragmentManager, AddTaskDialogFragment.TAG)
    }

    private enum class TaskMenuOption(@StringRes val actionTextId: Int) {
        EDIT_TASK(R.string.menu_task_edit),
        DELETE_TASK(R.string.menu_task_delete)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid leaking the RecyclerView/adapter reference
    }
}
