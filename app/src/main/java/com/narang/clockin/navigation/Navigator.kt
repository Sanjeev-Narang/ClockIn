    package com.narang.clockin.navigation

import androidx.fragment.app.FragmentManager
import com.narang.clockin.R
import timber.log.Timber

/**
 * Central navigation helper — owns all FragmentManager transactions.
 *
 * Design:
 * - Single container: [R.id.nav_host_fragment_container] hosted by
 *   `MainActivity`. All screens replace this id.
 * - [Destination.tag] is used as fragment tag and back-stack name so
 *   restores after configuration change can find fragments via tag.
 * - Uses `FragmentManager` directly (no Jetpack Navigation component).
 *
 * Modularity / future evolution:
 * - To add ViewModel argument passing, create `data class` destinations with
 *   typed props (e.g. `data class Detail(val taskId: String): Destination`) and
 *   have `newInstance()` put them into `arguments` via `bundleOf`.
 * - To support Activity destinations, introduce `sealed interface Destination`
 *   with `ActivityDestination` subtype and branch in [navigate] via `when`.
 * - To add animations/transitions, add optional params to [Destination] or
 *   to these methods.
 *
 * All methods are idempotent with respect to `AuthStateListener` — they can be
 * called on every `onStart` without duplicating transactions.
 */
object Navigator {

    /**
     * canonical navigate method. the primary method to replace fragments.
     * uses manual fragment transaction
     */
    fun navigate(
        fragmentManager: FragmentManager,
        destination: Destination,
        addToBackStack: Boolean = true,
    ) {
        Timber.d("Navigator.navigate -> ${destination.tag} addToBackStack=$addToBackStack")
        val transaction = fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, destination.newInstance(), destination.tag)

        if (addToBackStack) {
            transaction.addToBackStack(destination.tag)
        }
        transaction.commit()
    }

    /**
     * overload for 'navigate method'
     */
    fun navigate(
        destination: Destination,
        fragmentManager: FragmentManager,
        addToBackStack: Boolean = true,
    ) = navigate(fragmentManager, destination, addToBackStack)

    /**
     * Canonical root navigation — clears the entire back stack before replacing.
     * Use for auth transitions (e.g. login success, logout) so the user cannot
     * navigate back to the previous auth screen.
     */
    fun navigateToRoot(
        fragmentManager: FragmentManager,
        destination: Destination,
    ) {
        Timber.d("Navigator.navigateToRoot -> ${destination.tag} (clearing back stack)")
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        navigate(fragmentManager, destination, addToBackStack = false)
    }

    /**
     * Overload for `navigateToRoot` method.
     */
    fun navigateToRoot(
        destination: Destination,
        fragmentManager: FragmentManager,
    ) = navigateToRoot(fragmentManager, destination)
}
