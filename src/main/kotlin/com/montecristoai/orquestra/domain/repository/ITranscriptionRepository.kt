package com.montecristoai.orquestra.domain.repository

import com.montecristoai.orquestra.data.dto.ChatResponse

// --- Interfaz para desacoplar la implementación ---
interface ITranscriptionRepository {
    suspend fun generateChatResponse(message: String, modelName: String): ChatResponse
}