package com.github.tywinlanni.notification.hub.dao

import com.github.tywinlanni.notification.hub.databaseEntity.User
import com.github.tywinlanni.notification.hub.databaseEntity.YouTubeChannel
import org.litote.kmongo.Id

interface DAO {

    suspend fun addMonitoredChannel(userTelegramId: Long, youTubeChannel: YouTubeChannel)
    suspend fun removeMonitoredChannel(userTelegramId: Long, youTubeChannel: YouTubeChannel)

    suspend fun addNewUser(userTelegramId: Long)

    suspend fun addNewChannel(youTubeChannel: YouTubeChannel): YouTubeChannel

    suspend fun getAllChannels(): List<YouTubeChannel>

    suspend fun updateLastVideoPublishDate(publishDate: String, channelId: Id<YouTubeChannel>)
    suspend fun getUsersSubscribedToChannel(channelId: Id<YouTubeChannel>): List<User>
}
