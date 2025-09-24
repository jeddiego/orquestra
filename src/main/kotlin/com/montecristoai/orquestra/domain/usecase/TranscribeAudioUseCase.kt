package com.montecristoai.orquestra.domain.usecase

import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository

// El caso de uso orquesta la lógica, llamando al repositorio.
class TranscribeAudioUseCase(private val repository: ITranscriptionRepository) {
    suspend fun execute(audioBytes: ByteArray): TranscriptionResponse {
        return repository.transcribe(audioBytes)
    }
}