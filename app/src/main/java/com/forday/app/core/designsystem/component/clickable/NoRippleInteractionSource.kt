package com.forday.app.core.designsystem.component.clickable

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 리플 효과를 제거하기 위한 InteractionSource.
 * interactions가 빈 Flow이므로 Indication이 아무것도 그리지 않는다.
 * Button / IconButton 등 Material3 컴포넌트에 interactionSource로 전달해 사용한다.
 */
class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}
