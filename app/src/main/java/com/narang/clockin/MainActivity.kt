package com.narang.clockin

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.narang.clockin.navigation.AboutDestination
import com.narang.clockin.navigation.LoginDestination
import com.narang.clockin.navigation.Navigator
import com.narang.clockin.navigation.TasksDestination
import com.narang.clockin.navigation.SplashDestination
import com.narang.clockin.ui.about.AboutFragment
import com.narang.clockin.ui.auth.LoginFragment
import com.narang.clockin.ui.auth.SignupFragment
import com.narang.clockin.ui.tasks.TaskFragment
import com.narang.clockin.ui.splash.SplashFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navView: NavigationView
    private lateinit var appBarLayout: View

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser != null) return@AuthStateListener

        val top = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        val isOnAuthOrSplash = top is SplashFragment || top is LoginFragment || top is SignupFragment
        if (isOnAuthOrSplash) {
            Timber.d("Auth listener: already on auth/splash, no redirect")
            return@AuthStateListener
        }
        Timber.d("Auth listener: user null while on $top -> redirect to Login")
        Navigator.navigateToRoot(supportFragmentManager, LoginDestination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.nav_view)
        appBarLayout = findViewById(R.id.appBarLayout)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_tasks -> {
                    val top = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                    if (top !is TaskFragment) {
                        if (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) is AboutFragment) {
                            supportFragmentManager.popBackStack()
                        }
                        Navigator.navigate(supportFragmentManager, TasksDestination, addToBackStack = false)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_about -> {
                    val top = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                    if (top !is AboutFragment) {
                        Navigator.navigate(supportFragmentManager, AboutDestination, addToBackStack = true)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            Navigator.navigate(supportFragmentManager, SplashDestination, addToBackStack = false)
            supportFragmentManager.executePendingTransactions()
            updateToolbarAndDrawerState()

            lifecycleScope.launch {
                delay(SPLASH_DURATION_MS)

                val loggedIn = FirebaseAuth.getInstance().currentUser != null
                if (loggedIn) {
                    Navigator.navigateToRoot(supportFragmentManager, TasksDestination)
                } else {
                    Navigator.navigateToRoot(supportFragmentManager, LoginDestination)
                }
                supportFragmentManager.executePendingTransactions()
                updateToolbarAndDrawerState()
                // Post one more frame for safety (commit animation)
                appBarLayout.post { updateToolbarAndDrawerState() }
            }
        } else {
            supportFragmentManager.executePendingTransactions()
            updateToolbarAndDrawerState()
        }

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        // Back-stack changes cover addToBackStack=true navigations (About)
        supportFragmentManager.addOnBackStackChangedListener { updateToolbarAndDrawerState() }
        // For addToBackStack=false replaces (Splash, Tasks root) the callback above doesn't fire,
        // so also listen to any fragment attach/replace.
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(fm: androidx.fragment.app.FragmentManager, f: androidx.fragment.app.Fragment, v: View, savedInstanceState: Bundle?) {
                    updateToolbarAndDrawerState()
                }
            }, false
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
                    else -> {
                        // Let FragmentManager handle back stack; if empty, finish activity
                        if (supportFragmentManager.backStackEntryCount > 0) {
                            supportFragmentManager.popBackStack()
                        } else {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                            isEnabled = true
                        }
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    /**
     * Central state: toolbar visibility/title + drawer lock + checked item.
     * */
    private fun updateToolbarAndDrawerState() {
        val top = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)

        val isAuthOrSplash = top == null || top is SplashFragment || top is LoginFragment || top is SignupFragment
        if (isAuthOrSplash) {
            appBarLayout.visibility = View.GONE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            return
        }

        appBarLayout.visibility = View.VISIBLE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        when (top) {
            is TaskFragment -> {
                toolbar.title = getString(R.string.title_tasks)
                navView.setCheckedItem(R.id.nav_tasks)
            }
            is AboutFragment -> {
                toolbar.title = getString(R.string.title_about)
                navView.setCheckedItem(R.id.nav_about)
            }
            else -> {
                // Fallback
                toolbar.title = getString(R.string.app_name)
            }
        }
    }

    companion object {
        private const val SPLASH_DURATION_MS = 1500L
    }
}
