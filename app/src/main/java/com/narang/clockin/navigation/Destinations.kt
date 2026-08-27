package com.narang.clockin.navigation

import androidx.fragment.app.Fragment
import com.narang.clockin.ui.auth.LoginFragment
import com.narang.clockin.ui.auth.SignupFragment
import com.narang.clockin.ui.about.AboutFragment
import com.narang.clockin.ui.tasks.TaskFragment
import com.narang.clockin.ui.splash.SplashFragment

/**
 * Catalogue of first-class destinations.
 *
 * Each object implements [Destination] with a stable [Destination.tag]
 * (used as fragment tag + back-stack name) and a [Destination.newInstance]
 * factory.
 *
 * Keep destinations as `object` singletons so identity is stable across
 * process restores and configuration changes. When evolving the schema
 * (e.g. passing a ViewModel or arguments), create a `data class` destination
 * with typed properties — do not store mutable state inside an `object`.
 *
 * Future ViewModel/args example (typed, like Anki's data classes):
 * ```
 * data class TaskDetailDestination(val taskId: String) : Destination {
 *     override val tag = "TaskDetail"
 *     override fun newInstance(): Fragment = TaskDetailFragment().apply {
 *         arguments = bundleOf("taskId" to taskId)
 *     }
 * }
 * ```
 */
object SplashDestination : Destination {
    override val tag: String = "SplashFragment"
    override fun newInstance(): Fragment = SplashFragment.newInstance()
}

object LoginDestination : Destination {
    override val tag: String = "LoginFragment"
    override fun newInstance(): Fragment = LoginFragment()
}

object SignupDestination : Destination {
    override val tag: String = "SignupFragment"
    override fun newInstance(): Fragment = SignupFragment()
}

object TasksDestination : Destination {
    override val tag: String = "TaskFragment"
    override fun newInstance(): Fragment = TaskFragment()
}

object AboutDestination : Destination {
    override val tag: String = "AboutFragment"
    override fun newInstance(): Fragment = AboutFragment()
}
