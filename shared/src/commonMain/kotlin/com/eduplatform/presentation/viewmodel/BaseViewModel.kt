package com.eduplatform.presentation.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<S, I>(initial: S) {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun setState(block: S.() -> S) {
        _state.update { it.block() }
    }

    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun onIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)

    open fun clear() {
        viewModelScope.cancel()
    }
}
