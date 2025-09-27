package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.domain.usecase.TranscribeAudioUseCase
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.transcriptionRoute() {
    routing {
        route("/api") {
            post("/transcribe") {
                val multipart = call.receiveMultipart()
                var modelName: String? = null
                var responseSent = false

                // NOTE: This logic assumes the 'model' form item is sent BEFORE the 'audio' file item.
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "model") {
                                modelName = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "audio" && !responseSent) {
                                val currentModel = modelName
                                if (currentModel == null) {
                                    call.respond(mapOf("error" to "El campo 'model' es requerido y debe enviarse antes del archivo."))
                                } else {
                                    responseSent = true
                                    part.streamProvider().use { audioStream ->
                                        val useCase = call.get<TranscribeAudioUseCase>()
                                        val result = useCase.execute(audioStream, currentModel)
                                        call.respond(result)
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (!responseSent) {
                    call.respond(mapOf("error" to "No se recibió archivo de audio o parte de audio inválida."))
                }
            }
        }
    }
}

