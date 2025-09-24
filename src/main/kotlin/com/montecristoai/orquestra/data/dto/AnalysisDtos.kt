package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

/**
 * DTO para la Ficha Descriptiva generada por la IA.
 * Modela la estructura que le pedimos a Gemini en formato JSON.
 */
@Serializable
data class DescriptiveCard(
    val summary: String? = null,
    val keyPoints: List<String>? = null,
    val detectedSpeakers: Int? = null,
    val recordingType: String? = null,
    val detectedDate: String? = "No detectado",
    val detectedPlace: String? = "No detectado",
    val detectedProject: String? = "No detectado"
)

/**
 * DTO para la respuesta completa del nuevo endpoint /api/analyze.
 * Combina la Ficha Descriptiva con la transcripción detallada.
 */
@Serializable
data class AnalysisResponse(
    val descriptiveCard: DescriptiveCard? = null,
    val transcription: String? = null,
    val error: String? = null
)
