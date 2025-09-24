plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.montecristoai.orquestra"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.netty") {
            useVersion("4.1.100.Final")
            because("Forzar una versión consistente de Netty para evitar conflictos en runtime")
        }
    }
}

dependencies {
    implementation(platform("io.netty:netty-bom:4.1.100.Final"))
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlinx.html)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.dotenv.kotlin)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Google
//    implementation(libs.google.ai.client)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    // MySql
    implementation(libs.mysql)

    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
}

