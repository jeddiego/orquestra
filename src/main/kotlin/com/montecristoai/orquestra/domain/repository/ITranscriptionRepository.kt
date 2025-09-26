package com.montecristoai.orquestra.domain.repository

import com.montecristoai.orquestra.data.dto.AnalysisResponse
import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import java.io.InputStream
import java.time.ZonedDateTime

// --- Interfaz para desacoplar la implementación ---
interface ITranscriptionRepository {
    suspend fun transcribe(audioStream: InputStream): TranscriptionResponse
    suspend fun analyze(transcription: String, recordingStartTime: ZonedDateTime): AnalysisResponse
}