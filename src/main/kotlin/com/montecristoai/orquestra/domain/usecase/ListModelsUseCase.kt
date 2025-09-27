package com.montecristoai.orquestra.domain.usecase

import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository

class ListModelsUseCase(private val repository: ITranscriptionRepository) {
    suspend fun execute(): List<String> {
        return repository.listModels()
    }
}