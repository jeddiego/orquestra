package com.montecristoai.orquestra.data.dto

import kotlinx.serialization.Serializable

/**
 * Data classes que modelan la estructura JSON para la API de Gemini.
 * Se usan para la serialización y deserialización automática con Kotlinx.
 */

// --- Estructura de la Petición (Request) ---

@Serializable
data class GeminiRequest(val contents: List<Content>)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

// La API es multimodal, una "Part" puede ser texto o datos binarios (nuestro audio).
@Serializable
data class Part(val text: String? = null, val inlineData: Blob? = null)

@Serializable
data class Blob(val mimeType: String, val data: String) // Los bytes del audio irán aquí, codificados en Base64


// --- Estructura de la Respuesta (Response) ---

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    // --- CAMBIO AQUÍ ---
    // Añadimos el objeto 'usageMetadata' que causó el último error.
    val usageMetadata: UsageMetadata? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val tokenCount: Int? = null,
    val avgLogprobs: Double? = null
)

@Serializable
data class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)

// Añadimos una data class para modelar el objeto usageMetadata.
@Serializable
data class UsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)

