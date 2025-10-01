package com.montecristoai.orquestra.data.dto.gemini

import kotlinx.serialization.Serializable

@Serializable
data class Part(val text: String)

@Serializable
data class ContentInput(val role: String, val parts: List<Part>)

@Serializable
data class SystemInstruction(val parts: List<Part>)

@Serializable
data class GenerationConfig(val temperature: Float, val maxOutputTokens: Int)

@Serializable
data class GenerateContentRequest(
    val system_instruction: SystemInstruction,
    val contents: List<ContentInput>,
    val generation_config: GenerationConfig
)