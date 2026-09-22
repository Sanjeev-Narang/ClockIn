package com.narang.clockin.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.narang.clockin.data.repository.FirebaseAuthRepository
import com.narang.clockin.databinding.FragmentSignupBinding
import com.narang.clockin.navigation.LoginDestination
import com.narang.clockin.navigation.Navigator
import com.narang.clockin.navigation.TasksDestination
import kotlinx.coroutines.launch
import kotlin.getValue

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(FirebaseAuthRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignup.setOnClickListener {
            val email = binding.editSignupEmail.text.toString().trim()
            val password = binding.editSignupPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.signup(email, password)
            } else {
                if (email.isEmpty()) binding.inputLayoutSignupEmail.error = "Email is required"
                if (password.isEmpty()) binding.inputLayoutSignupPassword.error = "Password is required"
            }
        }

        binding.btnGotoLogin.setOnClickListener {
            Navigator.navigate(parentFragmentManager, LoginDestination, addToBackStack = true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressSignup.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnSignup.isEnabled = !state.isLoading

                    if (state.isSuccess) {
                        Navigator.navigate(parentFragmentManager, TasksDestination, addToBackStack = false)
                    }
                    state.errorMessage?.let { error ->
                        binding.inputLayoutSignupEmail.error = error
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}