package com.montecristoai.orquestra.domain.usecase

import com.montecristoai.orquestra.data.dto.ChatResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository

class ChatUseCase(private val transcriptionRepository: ITranscriptionRepository) {

    suspend fun execute(message: String, modelName: String): ChatResponse {
        return transcriptionRepository.generateChatResponse(message, modelName)
    }
}