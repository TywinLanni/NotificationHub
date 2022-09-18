package com.github.tywinlanni.notification.hub.configuration

data class Configuration(
    val youTubeConfiguration: YoutubeConfiguration,
    val ktorConfiguration: KtorConfiguration,
    val databaseConfiguration: DatabaseConfiguration,
    val telegramConfiguration: TelegramConfiguration,
)

data class YoutubeConfiguration(
    val clientId: String,
    val clientSecret: String,
    val apiKey: String,
    val applicationName: String,
)

data class KtorConfiguration(
    val port: Int,
    val auth: Auth,
)

data class Auth(
    val base: BaseAuth
)

data class BaseAuth(
    val userName: String,
    val password: String,
)

data class DatabaseConfiguration(
    val name: String,
)

data class TelegramConfiguration(
    val host: String,
    val port: String,
    val auth: Auth,
)
