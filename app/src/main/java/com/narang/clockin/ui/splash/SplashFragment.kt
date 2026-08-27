package com.narang.clockin.ui.splash

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.narang.clockin.R

/**
 * Minimal splash screen — shown on cold start
 *
 * Keep it lightweight; no ViewBinding needed. Replace with a proper layout
 * (e.g. `fragment_splash.xml`) when enriching it — just keep the tag
 * `SplashFragment` stable so [com.narang.clockin.navigation.SplashDestination.tag]
 * and the logout interceptor's `is SplashFragment` check remain valid.
 */
class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return TextView(requireContext()).apply {
            text = getString(R.string.app_name)
            textSize = 36f // scaled pixels, 4 grid (or 2 grid sometimes)
            gravity = Gravity.CENTER
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.screen_background))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }
    }

    companion object {
        fun newInstance(): SplashFragment = SplashFragment()
    }
}
