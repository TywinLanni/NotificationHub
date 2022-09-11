package com.github.tywinlanni.notification.hub.configuration

import io.ktor.server.application.*

fun Application.loadConfiguration(): Configuration =
    Configuration(
        youTubeConfiguration = YoutubeConfiguration(
            clientId = environment.config.propertyOrNull("youtube.client_id")?.getString() ?: error("Client id not found"),
            clientSecret = environment.config.propertyOrNull("youtube.client_secret")?.getString() ?: error("Client secret not found"),
            apiKey = environment.config.propertyOrNull("youtube.api_key")?.getString() ?: error("Api key not found"),
            applicationName = environment.config.propertyOrNull("youtube.application_name")?.getString() ?: error("Application name not found"),
        ),
        auth = Auth(
            base = BaseAuth(
                userName = environment.config.propertyOrNull("ktor.auth.base.username")?.getString() ?: error("Base auth username not found"),
                password = environment.config.propertyOrNull("ktor.auth.base.password")?.getString() ?: error("Base auth password not found"),
            )
        ),
        databaseConfiguration = DatabaseConfiguration(
            name = environment.config.propertyOrNull("database.name")?.getString() ?: error("Database name not found"),
        ),
        telegramConfiguration = TelegramConfiguration(
            host = environment.config.propertyOrNull("telegram.host")?.getString() ?: error("Database name not found"),
            port = environment.config.propertyOrNull("telegram.port")?.getString() ?: error("Database name not found"),
            auth = Auth(
                base = BaseAuth(
                    userName = environment.config.propertyOrNull("telegram.auth.base.username")?.getString() ?: error("Base auth username not found"),
                    password = environment.config.propertyOrNull("telegram.auth.base.password")?.getString() ?: error("Base auth password not found"),
                )
            ),
        )
    )
