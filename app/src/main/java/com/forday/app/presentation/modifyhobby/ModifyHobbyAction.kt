package com.forday.app.presentation.modifyhobby

sealed interface ModifyHobbyAction {
    data class FetchMyHobbyList(val inProgress: String? = null) : ModifyHobbyAction
    data class ModifyHobbyStatus(val hobbyId: Long, val hobbyStatus: String) : ModifyHobbyAction
    data object DismissHobbyLimitDialog : ModifyHobbyAction
    data object ClearToast : ModifyHobbyAction
}
