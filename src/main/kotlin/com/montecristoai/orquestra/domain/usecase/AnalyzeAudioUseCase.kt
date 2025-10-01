package com.montecristoai.orquestra.domain.usecase

import com.montecristoai.orquestra.data.dto.AnalysisResponse
import com.montecristoai.orquestra.domain.repository.ITranscriptionRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.io.InputStream
import java.util.Locale

class AnalyzeAudioUseCase(private val repository: ITranscriptionRepository) {

    suspend fun execute(audioStream: InputStream, modelName: String): AnalysisResponse {
        // 1. Capturamos la hora de inicio EXACTA en la zona horaria de la Ciudad de México.
        val recordingStartTime = ZonedDateTime.now(ZoneId.of("America/Mexico_City"))

        // 2. Realizamos la transcripción para obtener el texto con timestamps relativos.
        val transcriptionResponse = repository.transcribe(audioStream, modelName)

        val relativeTranscription = transcriptionResponse.transcription ?: return AnalysisResponse(
            error = transcriptionResponse.error ?: "La transcripción falló."
        )

        // 3. Pasamos la transcripción Y la hora de inicio al método de análisis.
        val analysisResult = repository.analyze(relativeTranscription, recordingStartTime, modelName)

        // --- CAMBIOS CLAVE: Post-procesamiento final ---
        // Si el análisis falló, lo devolvemos tal cual.
        if (analysisResult.error != null || analysisResult.transcription == null || analysisResult.descriptiveCard == null) {
            return analysisResult
        }

        // 4. Convertimos los timestamps relativos a la hora local real.
        val absoluteTranscription = convertTimestampsToLocalTime(analysisResult.transcription, recordingStartTime)

        // 5. Formateamos la fecha para mostrarla correctamente.
        val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
        val formattedDate = recordingStartTime.format(dateFormatter)

        // 6. Construimos la respuesta final, reemplazando la transcripción y la fecha.
        return analysisResult.copy(
            transcription = absoluteTranscription.trim(), // Usamos .trim() para eliminar la sangría
            descriptiveCard = analysisResult.descriptiveCard.copy(detectedDate = formattedDate)
        )
    }

    /**
     * Convierte una transcripción con timestamps relativos [HH:MM:SS] a una con la hora local real.
     */
    private fun convertTimestampsToLocalTime(text: String, startTime: ZonedDateTime): String {
        val regex = Regex("""\[(\d{2}):(\d{2}):(\d{2})\]""")
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        return regex.replace(text) { matchResult ->
            try {
                val (hours, minutes, seconds) = matchResult.destructured
                val realTime = startTime.plusHours(hours.toLong()).plusMinutes(minutes.toLong()).plusSeconds(seconds.toLong())
                // Devolvemos la hora real sin corchetes para que coincida con tu última captura.
                realTime.format(formatter)
            } catch (e: Exception) {
                matchResult.value // Si algo falla, dejamos el timestamp original
            }
        }
    }
}