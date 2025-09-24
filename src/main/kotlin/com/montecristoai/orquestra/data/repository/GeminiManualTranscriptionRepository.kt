package com.montecristoai.orquestra.data.repository

import com.montecristoai.orquestra.data.dto.*
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Implementación del repositorio que llama a la API de Gemini manualmente
 * usando el cliente HTTP de Ktor.
 */
class GeminiManualTranscriptionRepository(
    private val client: HttpClient,
    private val apiKey_: String
) : ITranscriptionRepository {

    // Instancia del parser de JSON que ignora campos desconocidos.
    private val jsonParser = Json { ignoreUnknownKeys = true }
    val hardcodeKey = "AIzaSyCbJ097Hx3HqaJNYiwYOwDXAg0BPv0OwfE"

    /**
     * Paso 1: Convierte el audio en bruto a texto, identificando hablantes.
     */
    override suspend fun transcribe(audioBytes: ByteArray): TranscriptionResponse {

/*        if (apiKey.isBlank()) {
            return TranscriptionResponse(error = "La API Key de Gemini no fue proporcionada.")
        }*/

        // El endpoint de la API para el modelo que queremos usar.
        val geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

        try {
            // 1. Codificar el audio en Base64, que es como la API REST lo espera.
            val audioBase64 = Base64.getEncoder().encodeToString(audioBytes)
            val transcriptionPrompt = """
            Transcribe este audio. Sigue estas reglas estrictamente:
            1. Identifica y etiqueta a cada hablante como 'Hablante 1', 'Hablante 2', etc.
            2. Al inicio de CADA frase o evento sonoro, añade una marca de tiempo RELATIVA desde el inicio del audio con el formato [HH:MM:SS].
            3. Coloca CADA nueva marca de tiempo y su frase correspondiente en una nueva línea.
            4. IMPORTANTE: La primera línea de la transcripción NO debe tener ninguna sangría o espacio en blanco al inicio.
            """

            // 2. Construir el cuerpo de la petición usando nuestras data classes (DTOs).
            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = transcriptionPrompt),
                            Part(inlineData = Blob(mimeType = "audio/wav", data = audioBase64))
                        )
                    )
                )
            )

            // 3. Realizar la llamada POST con el cliente de Ktor.
            val response: GeminiResponse = client.post(geminiEndpoint) {
                parameter("key", hardcodeKey) // La API Key se pasa como un query parameter.
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            // 4. Extraer el texto de la respuesta.
            val transcription = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return TranscriptionResponse(error = "La respuesta de la API no contenía una transcripción válida.")

            return TranscriptionResponse(transcription = transcription)

        } catch (e: Exception) {
            println("Error al contactar la API de Gemini manualmente: ${e.message}")
            // Aquí podrías añadir un log más detallado del error 'e'.
            return TranscriptionResponse(error = "No se pudo conectar con el servicio de transcripción de Gemini.")
        }
    }

    /**
     * Paso 2: Toma el texto transcrito y le pide a Gemini que lo analice
     * y genere la Ficha Descriptiva en formato JSON.
     */
    override suspend fun analyze(transcription: String, recordingStartTime: ZonedDateTime): AnalysisResponse {
        if (hardcodeKey.isBlank()) {
            return AnalysisResponse(error = "La API Key de Gemini no fue proporcionada.")
        }
        val geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

        // --- CAMBIO CLAVE ---
        // El prompt ahora es más simple. Solo pedimos el análisis,
        // ya que el cálculo de la hora y la fecha se hará en el UseCase.
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

        try {
            val requestBody = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = analysisPrompt)))))
            val response: GeminiResponse = client.post(geminiEndpoint) {
                parameter("key", hardcodeKey)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val jsonResponseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return AnalysisResponse(error = "La respuesta de la API de análisis estaba vacía.")

            val cleanedJson = jsonResponseText.substringAfter("```json").substringBeforeLast("```").trim()

            return jsonParser.decodeFromString<AnalysisResponse>(cleanedJson)

        } catch (e: Exception) {
            // Mejora en el log de errores
            val errorMessage = "Error al contactar la API de Gemini para analizar: ${e::class.simpleName} - ${e.message}"
            println(errorMessage)
            return AnalysisResponse(error = "El servicio de análisis de Gemini falló.")
        }
    }
}