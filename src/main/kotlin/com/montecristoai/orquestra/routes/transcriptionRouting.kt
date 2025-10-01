package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.domain.usecase.TranscribeAudioUseCase
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
import java.io.InputStream

fun Application.transcriptionRoute() {
    routing {
        route("/api") {
            post("/transcribe") {
                val multipart = call.receiveMultipart()
                var modelName: String? = null
                var audioStreamProvider: (() -> InputStream)? = null

                // First, iterate over all parts to find the model name and the audio stream provider.
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "model") {
                                modelName = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "audio") {
                                audioStreamProvider = part.streamProvider
                            }
                        }
                        else -> {}
                    }
                    // Do not dispose of the part here, as we need the stream later.
                    // Ktor will handle the disposal automatically after the call is complete.
                }

                // Now that we have processed all parts, check if we have what we need.
                val finalModelName = modelName
                val finalAudioStreamProvider = audioStreamProvider

                if (finalModelName != null && finalAudioStreamProvider != null) {
                    finalAudioStreamProvider().use { audioStream ->
                        val useCase = call.get<TranscribeAudioUseCase>()
                        val result = useCase.execute(audioStream, finalModelName)
                        call.respond(result)
                    }
                } else {
                    val missingFields = mutableListOf<String>()
                    if (finalModelName == null) missingFields.add("model")
                    if (finalAudioStreamProvider == null) missingFields.add("audio")
                    call.respond(mapOf("error" to "Faltan campos en la petición: ${missingFields.joinToString(", ")}"))
                }
            }
        }
    }
}

