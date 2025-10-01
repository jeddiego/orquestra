package com.montecristoai.orquestra.domain.usecase

import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import java.io.InputStream

// El caso de uso orquesta la lógica, llamando al repositorio.
class TranscribeAudioUseCase(private val repository: ITranscriptionRepository) {
    suspend fun execute(audioStream: InputStream, modelName: String): TranscriptionResponse {
        return repository.transcribe(audioStream, modelName)
    }
}