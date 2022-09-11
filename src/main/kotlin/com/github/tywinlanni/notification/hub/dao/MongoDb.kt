package com.github.tywinlanni.notification.hub.dao

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import com.github.tywinlanni.notification.hub.configuration.DatabaseConfiguration
import com.github.tywinlanni.notification.hub.databaseEntity.User
import com.github.tywinlanni.notification.hub.databaseEntity.YouTubeChannel
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineClient
import org.slf4j.LoggerFactory

class MongoDb(client: CoroutineClient, databaseConfig: DatabaseConfiguration) : DAO {

    class MongoException(override val message: String) : Exception()

    private val database = client.getDatabase(databaseConfig.name)
    private val usersCollection = database.getCollection<User>()
    private val channelCollection = database.getCollection<YouTubeChannel>()

    init {
        // Change log level to WARN
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger: Logger = loggerContext.getLogger("org.mongodb.driver")
        rootLogger.level = Level.WARN
    }

    override suspend fun addMonitoredChannel(userTelegramId: Long, youTubeChannel: YouTubeChannel) {
        usersCollection.findOne(User::telegramId eq userTelegramId)
            ?.let { user: User ->
                val channel = channelCollection.findOne(YouTubeChannel::channelId eq youTubeChannel.channelId)
                    ?: addNewChannel(youTubeChannel)
                usersCollection.updateOneById(
                    id = user._id,
                    update = user.copy(
                        selectedChannels = mutableListOf<Id<YouTubeChannel>>().apply {
                            addAll(user.selectedChannels)
                            add(channel._id)
                        }
                    )
                )
            } ?: run {
                addNewUser(userTelegramId)
                addMonitoredChannel(userTelegramId, youTubeChannel)
            }
    }

    override suspend fun removeMonitoredChannel(userTelegramId: Long, youTubeChannel: YouTubeChannel) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewUser(userTelegramId: Long) {
        try {
            usersCollection.insertOne(
                User(
                    telegramId = userTelegramId,
                    selectedChannels = emptyList()
                )
            )
        } catch (e: Exception) {
            throw MongoException("Failed to add user. Reason: ${e.message}")
        }
    }

    override suspend fun addNewChannel(youTubeChannel: YouTubeChannel) =
        try {
            channelCollection.insertOne(
                document = youTubeChannel
            )
            youTubeChannel
        } catch (e: Exception) {
            throw MongoException("Failed to add channel. Reason: ${e.message}")
        }

    override suspend fun getAllChannels(): List<YouTubeChannel> =
        channelCollection
            .find()
            .toList()

    override suspend fun updateLastVideoPublishDate(publishDate: String, channelId: Id<YouTubeChannel>) {
        channelCollection.updateOneById(
            id = channelId,
            update = YouTubeChannel::lastVideoPublishedAt setTo publishDate,
        )
    }

    override suspend fun getUsersSubscribedToChannel(channelId: Id<YouTubeChannel>): List<User> =
        usersCollection
            .find(User::selectedChannels contains channelId)
            .toList()
}
