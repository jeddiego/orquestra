package com.montecristoai.orquestra.routes

import com.montecristoai.orquestra.domain.usecase.AnalyzeAudioUseCase
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.analysisRoute() {
    routing {
        route("/api") {
            post("/analyze") {
                val multipart = call.receiveMultipart()
                var audioBytes: ByteArray? = null
                var fileName: String? = null

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem && part.name == "audio") {
                        fileName = part.originalFileName
                        audioBytes = part.streamProvider().readBytes()
                    }
                    part.dispose()
                }

                val bytes = audioBytes
                if (bytes == null) {
                    call.respond(mapOf("error" to "No se recibió archivo de audio"))
                    return@post
                }

                val useCase = call.get<AnalyzeAudioUseCase>()
                val result = useCase.execute(bytes)

                // Añadimos el nombre del archivo al resultado si lo tenemos
                val finalResult = result.descriptiveCard?.let {
                    result.copy(
                        descriptiveCard = it.copy(
                           // Aquí podríamos añadir más metadatos si los tuviéramos
                        )
                    )
                } ?: result

                call.respond(finalResult)
            }
        }
    }
}
