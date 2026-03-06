package com.forday.app.presentation.main

import androidx.lifecycle.ViewModel
import com.forday.app.core.session.AuthEvent
import com.forday.app.core.session.AuthEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class MainEventViewModel @Inject constructor(
    authEventBus: AuthEventBus
) : ViewModel() {
    val authEvents: SharedFlow<AuthEvent> = authEventBus.events
}
