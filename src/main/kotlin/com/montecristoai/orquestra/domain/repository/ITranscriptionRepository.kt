package com.montecristoai.orquestra.domain.repository

import com.montecristoai.orquestra.data.dto.AnalysisResponse
import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import java.io.InputStream
import java.time.ZonedDateTime

// --- Interfaz para desacoplar la implementación ---
interface ITranscriptionRepository {
    suspend fun listModels(): List<String>
    suspend fun transcribe(audioStream: InputStream, modelName: String): TranscriptionResponse
    suspend fun analyze(transcription: String, recordingStartTime: ZonedDateTime, modelName: String): AnalysisResponse
}