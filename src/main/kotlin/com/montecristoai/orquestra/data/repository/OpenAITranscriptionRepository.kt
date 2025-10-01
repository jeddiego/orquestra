package com.montecristoai.orquestra.data.repository

import com.montecristoai.orquestra.data.dto.ChatResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import io.ktor.client.HttpClient

class OpenAITranscriptionRepository(
    private val client: HttpClient,
    private val apiKey: String
) : ITranscriptionRepository {

    override suspend fun generateChatResponse(message: String, modelName: String): ChatResponse {
        // This repository is not intended for chat functionality at this time.
        // Returning a default error response.
        return ChatResponse(response = "", error = "Chat functionality is not implemented for the OpenAI repository.")
    }
}