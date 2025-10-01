package com.montecristoai.orquestra.data.repository

import com.google.genai.Client
import com.google.genai.type.Content
import com.google.genai.type.GenerateContentResponse
import com.google.genai.type.Part
import com.google.genai.type.text
import com.montecristoai.orquestra.data.dto.AnalysisResponse
import com.montecristoai.orquestra.data.dto.TranscriptionResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.time.ZonedDateTime

class GeminiManualTranscriptionRepository(
    private val apiKey: String
) : ITranscriptionRepository {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    override suspend fun listModels(): List<String> {
        // This is a simplified implementation. A real implementation would list models from the API.
        return listOf("gemini-1.5-pro-latest", "gemini-pro")
    }

    override suspend fun transcribe(audioStream: InputStream, modelName: String): TranscriptionResponse {
        if (apiKey.isBlank()) {
            return TranscriptionResponse(error = "La API Key de Gemini no fue proporcionada.")
        }

        try {
            val client = Client.builder().apiKey(apiKey).build()
            val audioBytes = audioStream.readBytes()

            val transcriptionPrompt = """
            Transcribe este audio. Sigue estas reglas estrictamente:
            1. Identifica y etiqueta a cada hablante como 'Hablante 1', 'Hablante 2', etc.
            2. Al inicio de CADA frase o evento sonoro, añade una marca de tiempo RELATIVA desde el inicio del audio con el formato [HH:MM:SS].
            3. Coloca CADA nueva marca de tiempo y su frase correspondiente en una nueva línea.
            4. IMPORTANTE: La primera línea de la transcripción NO debe tener ninguna sangría o espacio en blanco al inicio.
            """.trimIndent()

            val content = Content.fromParts(
                Part.fromText(transcriptionPrompt),
                Part.fromBytes(audioBytes, "audio/wav")
            )

            val response: GenerateContentResponse = client.models().generateContent(modelName, content, null)

            val transcription = response.text()
                ?: return TranscriptionResponse(error = "La respuesta de la API no contenía una transcripción válida.")

            return TranscriptionResponse(transcription = transcription)

        } catch (e: Exception) {
            println("Error al contactar la API de Gemini con el SDK: ${e.message}")
            e.printStackTrace()
            return TranscriptionResponse(error = "Error en el backend: ${e::class.simpleName} - ${e.message}")
        }
    }

    override suspend fun analyze(transcription: String, recordingStartTime: ZonedDateTime, modelName: String): AnalysisResponse {
        if (apiKey.isBlank()) {
            return AnalysisResponse(error = "La API Key de Gemini no fue proporcionada.")
        }

        try {
            val client = Client.builder().apiKey(apiKey).build()

            val analysisPrompt = """
            Eres un analista de audio experto. A partir de la siguiente transcripción, genera una "Ficha Descriptiva".
            La transcripción es:
            ---
            $transcription
            ---
            Tu tarea es devolver EXCLUSIVAMENTE un objeto JSON válido con la siguiente estructura:
            {
              "descriptiveCard": {
                "summary": "Un resumen conciso del contenido del audio.",
                "keyPoints": ["Una lista de 2 a 5 puntos clave.", "Cada punto como un string."],
                "detectedSpeakers": "El número total de hablantes distintos que aparecen en la transcripción.",
                "recordingType": "El tipo de grabación (ej: 'Entrevista', 'Monólogo', 'Prueba de audio')."
              },
              "transcription": "Copia la transcripción original aquí, preservando el formato y los saltos de línea sin ninguna alteración."
            }
            No incluyas el campo 'detectedDate' en tu respuesta.
            Asegúrate de que tu respuesta sea solo el JSON, sin texto adicional antes o después.
            """.trimIndent()

            val response: GenerateContentResponse = client.models().generateContent(modelName, analysisPrompt, null)

            val jsonResponseText = response.text()
                ?: return AnalysisResponse(error = "La respuesta de la API de análisis estaba vacía.")

            val cleanedJson = jsonResponseText.substringAfter("```json").substringBeforeLast("```").trim()

            return jsonParser.decodeFromString<AnalysisResponse>(cleanedJson)

        } catch (e: Exception) {
            val errorMessage = "Error al contactar la API de Gemini para analizar: ${e::class.simpleName} - ${e.message}"
            println(errorMessage)
            e.printStackTrace()
            return AnalysisResponse(error = "Error en el backend: ${e::class.simpleName} - ${e.message}")
        }
    }
}