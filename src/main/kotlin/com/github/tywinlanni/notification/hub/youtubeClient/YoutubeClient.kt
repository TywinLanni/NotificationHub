package com.github.tywinlanni.notification.hub.youtubeClient

import com.github.tywinlanni.notification.hub.configuration.YoutubeConfiguration
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class YoutubeClient(private val config: YoutubeConfiguration) {

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
                host = "www.googleapis.com"
                parameters.append(name = "key", value = config.apiKey)
            }
        }
    }

    suspend fun searchChannelByName(channelName: String) = client.get("youtube/v3/search") {
        parameter(key = "part", value = "snippet")
        parameter(key = "q", value = channelName)
        parameter(key = "type", value = "channel")
    }.body<SearchResult.ChannelSearchResult>()

    suspend fun searchChannelByNameNextPage(pageToken: String) = client.get("youtube/v3/search") {
        parameter(key = "part", value = "snippet")
        parameter(key = "type", value = "channel")
        parameter(key = "pageToken", value = pageToken)
    }.body<SearchResult.ChannelSearchResult>()

    suspend fun searchLastVideoOnChannel(channelId: String, publishedAfter: String?) = client.get("youtube/v3/search") {
        parameter(key = "part", value = "snippet")
        parameter(key = "type", value = "video")
        parameter(key = "channelId", value = channelId)
        parameter(key = "order", value = "date")
        if (publishedAfter != null)
            parameter(key = "publishedAfter", value = publishedAfter)
    }.body<SearchResult.VideoSearchResult>()
}
