package com.montecristoai.orquestra.data.repository

import com.montecristoai.orquestra.data.dto.ChatResponse
import com.montecristoai.orquestra.data.dto.gemini.ContentInput
import com.montecristoai.orquestra.data.dto.gemini.GenerateContentRequest
import com.montecristoai.orquestra.data.dto.gemini.GenerationConfig
import com.montecristoai.orquestra.data.dto.gemini.Part
import com.montecristoai.orquestra.data.dto.gemini.SystemInstruction
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class GeminiManualTranscriptionRepository(
    private val apiKey: String
) : ITranscriptionRepository {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    override suspend fun generateChatResponse(message: String, modelName: String): ChatResponse {
        if (apiKey.isBlank()) {
            return ChatResponse(response = "", error = "API Key for Gemini not provided.")
        }

        val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"
        val req = GenerateContentRequest(
            system_instruction = SystemInstruction(parts = listOf(Part("You are a helpful assistant."))),
            contents = listOf(ContentInput(role = "user", parts = listOf(Part(message)))),
            generation_config = GenerationConfig(temperature = 0.7f, maxOutputTokens = 800)
        )

        try {
            val response = httpClient.post(baseUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("x-goog-api-key", apiKey)
                setBody(req)
            }

            if (!response.status.isSuccess()) {
                val body = response.bodyAsText()
                println("Gemini API error ${response.status}: $body")
                return ChatResponse(response = "", error = "Gemini API error ${response.status}: $body")
            }

            val genResp = response.body<com.montecristoai.orquestra.data.dto.gemini.GenerateContentResponse>()
            val textResponse = genResp.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            return ChatResponse(response = textResponse)

        } catch (e: Exception) {
            e.printStackTrace()
            return ChatResponse(response = "", error = "Failed to connect to Gemini API: ${e.message}")
        }
    }
}