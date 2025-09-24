package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

// Data class para la respuesta que esperamos de la API de OpenAI.
@Serializable
data class OpenAIResponse(
    val text: String
)