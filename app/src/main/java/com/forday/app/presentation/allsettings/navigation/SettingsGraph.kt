package com.forday.app.presentation.allsettings.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.nonTabEntry
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.navigation.CancelAccount
import com.forday.app.presentation.allsettings.cancelaccount.screen.CancelAccountScreenRoot
import com.forday.app.presentation.allsettings.privacypolicy.navigation.PrivacyPolicy
import com.forday.app.presentation.allsettings.privacypolicy.screen.PrivacyPolicyScreen
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.allsettings.settings.screen.SettingsScreenRoot
import com.forday.app.presentation.allsettings.termsofservice.navigation.TermsOfService
import com.forday.app.presentation.allsettings.termsofservice.screen.TermsOfServiceScreen
import com.forday.app.presentation.main.Navigator

fun EntryProviderScope<NavKey>.settingsGraph(
    navigator: Navigator,
    settingsViewModel: SettingsViewModel,
) {
    nonTabEntry<Settings> {
        SettingsScreenRoot(
            onBackClick = { navigator.goBack() },
            onTermsOfServiceClick = { navigator.navigate(TermsOfService) },
            onPrivacyPolicyClick = { navigator.navigate(PrivacyPolicy) },
            onCancelAccountClick = { navigator.navigate(CancelAccount) },
            viewModel = settingsViewModel
        )
    }

    nonTabEntry<TermsOfService>(backgroundColor = Color(0xFFF9F9F9)) {
        TermsOfServiceScreen(
            onCloseClick = { navigator.goBack() }
        )
    }

    nonTabEntry<PrivacyPolicy>(backgroundColor = Color(0xFFF9F9F9)) {
        PrivacyPolicyScreen(
            onCloseClick = { navigator.goBack() }
        )
    }

    nonTabEntry<CancelAccount> {
        CancelAccountScreenRoot(
            onBackClick = { navigator.goBack() },
            viewModel = settingsViewModel
        )
    }
}
