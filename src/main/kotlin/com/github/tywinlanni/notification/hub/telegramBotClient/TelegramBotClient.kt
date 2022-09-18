package com.github.tywinlanni.notification.hub.telegramBotClient

import com.github.tywinlanni.notification.hub.configuration.TelegramConfiguration
import com.github.tywinlanni.notification.hub.youtubeClient.Item
import com.github.tywinlanni.notification.hub.youtubeClient.SearchResult
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
        /*install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTP
                host = "${config.host}:${config.port}"
            }
        }*/
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
            delayMillis { 30_000L }
        }
    }

    suspend fun sendNotification(telegramId: Long, video: Item.Video) = client.put("http://${config.host}:${config.port}/sendNotification") {
        parameter(key = "telegramId", value = telegramId)
        contentType(ContentType.Application.Json)
        setBody(video)
    }

    suspend fun listChannels(telegramId: Long, searchResult: SearchResult.ChannelSearchResult) = client.post("http://${config.host}:${config.port}/listChannels") {
        parameter(key = "telegramId", value = telegramId)
        contentType(ContentType.Application.Json)
        setBody(searchResult)
    }
}
