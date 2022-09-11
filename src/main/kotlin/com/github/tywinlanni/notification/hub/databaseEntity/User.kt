package com.github.tywinlanni.notification.hub.databaseEntity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class User(
    @Contextual val _id: Id<User> = newId(),
    val telegramId: Long,
    val selectedChannels: List<Id<YouTubeChannel>>,
)
