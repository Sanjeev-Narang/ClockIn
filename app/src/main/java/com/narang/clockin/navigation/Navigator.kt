    package com.narang.clockin.navigation

import androidx.fragment.app.FragmentManager
import com.narang.clockin.R
import timber.log.Timber

/**
 * central navigation helper and owns all FragmentManager transactions.
 */
object Navigator {

    /**
     * canonical navigate method to replace fragments.
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
     * Canonical root navigation to clear the entire back stack before replacing.
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
