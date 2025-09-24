package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

// --- Data class para la respuesta de la API ---
@Serializable
data class TranscriptionResult(
    val text: String? = null,
    val error: String? = null
)