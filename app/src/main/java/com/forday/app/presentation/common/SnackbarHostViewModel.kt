package com.forday.app.presentation.common

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class SnackbarHostViewModel @Inject constructor(
    private val snackbarManager: SnackbarManager
) : ViewModel() {
    val sideEffects: SharedFlow<AppSideEffect> = snackbarManager.sideEffects

    fun show(message: String) {
        snackbarManager.show(message)
    }
}
