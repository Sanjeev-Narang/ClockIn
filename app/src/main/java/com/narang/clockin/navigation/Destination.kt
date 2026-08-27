package com.narang.clockin.navigation

import androidx.fragment.app.Fragment

/**
 * Future evolution (typed args, like Anki's `data class CardInfoDestination(val cardId: Long)`):
 * ```
 * data class TaskDetailDestination(val taskId: String) : Destination {
 *     override val tag = "TaskDetail"
 *     override fun newInstance() = TaskDetailFragment().apply {
 *         arguments = bundleOf("taskId" to taskId)
 *     }
 * }
 * ```
 * When you have 10+ destinations, change to `sealed interface Destination` for
 * exhaustive `when` in Navigator. For Activity destinations, add
 * `sealed interface Destination { class FragmentDest : Destination; class ActivityDest : Destination }`.
 */
interface Destination {
    val tag: String
    fun newInstance(): Fragment
}
