package com.narang.clockin.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.narang.clockin.databinding.FragmentLoginBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.narang.clockin.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch
import com.narang.clockin.navigation.Navigator
import com.narang.clockin.navigation.SignupDestination
import com.narang.clockin.navigation.TasksDestination

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        // We assume AuthViewModelFactory and AuthRepository are already made
        AuthViewModelFactory(FirebaseAuthRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. Navigation to Signup ---
        binding.btnGotoSignup.setOnClickListener {
            Navigator.navigate(parentFragmentManager, SignupDestination)
        }

        binding.btnLogin.setOnClickListener {
            // 3. Capture the text from the EditText fields
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString()

            // 4. Validate and pass to ViewModel
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // This is where the Fragment "hands off" the data to the ViewModel
                viewModel.login(email, password)
            } else {
                // Handle empty fields
                if (email.isEmpty()) binding.inputLayoutEmail.error = "Email is required"
                if (password.isEmpty()) binding.inputLayoutPassword.error = "Password is required"
            }
        }

        // --- 3. Observing the ViewModel ---
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> // collect is the suspending method
                    binding.progressAuth.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnLogin.isEnabled = !state.isLoading

                    if (state.isSuccess) {
                        Navigator.navigate(parentFragmentManager, TasksDestination, addToBackStack = false)
                    }

                    state.errorMessage?.let { error ->
                        // You could show a Toast or an error dialog here
                        binding.inputLayoutEmail.error = error
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