package com.montecristoai.orquestra.data.repository

import com.montecristoai.orquestra.data.dto.AnalysisResponse
import com.montecristoai.orquestra.data.dto.OpenAIResponse
import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import java.io.InputStream
import java.time.ZonedDateTime

// --- Implementación específica para OpenAI Whisper ---
// Ahora recibe el HttpClient y la apiKey a través de inyección de dependencias.
class OpenAITranscriptionRepository(
    private val client: HttpClient,
    private val apiKey: String
) : ITranscriptionRepository {

    override suspend fun listModels(): List<String> {
        return listOf("whisper-1")
    }

    override suspend fun transcribe(audioStream: InputStream, modelName: String): TranscriptionResponse {
        val audioBytes = audioStream.readBytes()

        if (apiKey.isBlank()) {
            return TranscriptionResponse(error = "La API Key de Whisper no fue proporcionada por la configuración.")
        }

        val openAIEndpoint = "https://api.openai.com/v1/audio/transcriptions"

        try {
            val response = client.post(openAIEndpoint) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                }
                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", audioBytes, Headers.build {
                            append(HttpHeaders.ContentType, "audio/wav")
                            append(HttpHeaders.ContentDisposition, "filename=\"audio.wav\"")
                        })
                        append("model", modelName)
                    }
                ))
            }

            if (response.status != HttpStatusCode.OK) {
                val errorBody = response.body<String>()
                println("Error de la API de OpenAI: $errorBody")
                return TranscriptionResponse(error = "El servicio de transcripción devolvió un error: ${response.status}")
            }

            val result = response.body<OpenAIResponse>()
            return TranscriptionResponse(transcription = result.text)

        } catch (e: Exception) {
            println("Error al contactar la API de OpenAI: ${e.message}")
            return TranscriptionResponse(error = "No se pudo conectar con el servicio de transcripción.")
        }
    }

    override suspend fun analyze(transcription: String, recordingStartTime: ZonedDateTime, modelName: String): AnalysisResponse {
        return AnalysisResponse()
    }
}