package com.trezanix.mytreza.presentation.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading:  StateFlow<Boolean> = _isLoading

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?>  = _startDestination

    init {
        checkToken()
    }

    private fun checkToken() {
        viewModelScope.launch {
            val minSplashTime = 1500L
            val startTime = System.currentTimeMillis()
            val token = tokenManager.accessToken.first()
            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < minSplashTime) {
                delay(minSplashTime - elapsedTime)
            }
            if  (!token.isNullOrBlank()) {
                _startDestination.value = "main"
            } else {
                _startDestination.value = "login"
            }
            _isLoading.value = false
        }
    }
}