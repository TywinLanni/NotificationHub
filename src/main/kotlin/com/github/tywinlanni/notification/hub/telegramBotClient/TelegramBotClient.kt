package com.github.tywinlanni.notification.hub.telegramBotClient

import com.github.tywinlanni.notification.hub.configuration.TelegramConfiguration
import com.github.tywinlanni.notification.hub.youtubeClient.Item
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class TelegramBotClient(private val config: TelegramConfiguration) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "${config.host}:${config.port}"
            }
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = config.auth.base.userName, password = config.auth.base.password)
                }
                realm = "Access to the '/' path"
            }
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
    }

    suspend fun sendNotification(telegramId: Long, video: Item.Video) = client.put("/sendNotification") {
        parameter(key = "telegramId", value = telegramId)
        contentType(ContentType.Application.Json)
        setBody(video)
    }
}
