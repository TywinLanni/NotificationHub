package com.github.tywinlanni.notification.hub.databaseEntity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class YouTubeChannel(
    @Contextual @kotlinx.serialization.Transient val _id: Id<YouTubeChannel> = newId(),
    val channelId: String,
    val channelName: String,
    val previewURL: String,
    val description: String,
    val lastVideoPublishedAt: String? = null,
)
