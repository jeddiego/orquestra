package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

// Data class para la respuesta que enviaremos al frontend.
@Serializable
data class TranscriptionResponse(
    val transcription: String? = null,
    val error: String? = null
)