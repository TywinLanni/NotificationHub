package com.github.tywinlanni.notification.hub.watcher

import com.github.tywinlanni.notification.hub.dao.DAO
import com.github.tywinlanni.notification.hub.databaseEntity.User
import com.github.tywinlanni.notification.hub.databaseEntity.YouTubeChannel
import com.github.tywinlanni.notification.hub.telegramBotClient.TelegramBotClient
import com.github.tywinlanni.notification.hub.youtubeClient.SearchResult
import com.github.tywinlanni.notification.hub.youtubeClient.YoutubeClient
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class YoutubeWatcher private constructor(
    private val dao: DAO,
    private val youtubeClient: YoutubeClient,
    private val telegramBotClient: TelegramBotClient
) : CoroutineScope {

    data class Builder(
        val dao: DAO,
        val youtubeClient: YoutubeClient,
        val telegramBotClient: TelegramBotClient,
    ) {
        private val youtubeWatcher = YoutubeWatcher(dao, youtubeClient, telegramBotClient)
        suspend fun loadChannels() = apply { youtubeWatcher.loadChannels() }
        fun startWatching() = apply { youtubeWatcher.startWatching() }
        fun build() = youtubeWatcher
    }

    override val coroutineContext = Dispatchers.IO

    private val targetChannels = mutableListOf<YouTubeChannel>()
    private val mutex = Mutex()
    private val watcherJob = launch(start = CoroutineStart.LAZY) {
        while (isActive) {
            delay(30_000)
            checkChannels()
            loadChannels()
        }
    }
    private val notificationJob = SupervisorJob()

    private suspend fun loadChannels() = mutex
        .withLock {
            targetChannels.clear()
            targetChannels.addAll(dao.getAllChannels())
        }

    private fun startWatching() = watcherJob.start()

    private suspend fun checkChannels() {
        this.coroutineContext {
            // Wait while old notifications be sends
            notificationJob.children.forEach { it.join() }
            mutex.withLock {
                targetChannels
                    .windowed(100)
                    .forEach { youTubeChannels: List<YouTubeChannel> ->
                        youTubeChannels.map { channel: YouTubeChannel ->
                            launch {
                                youtubeClient.searchLastVideoOnChannel(
                                    channelId = channel.channelId,
                                    publishedAfter = channel.lastVideoPublishedAt,
                                ).takeIf { lastVideo: SearchResult.VideoSearchResult -> lastVideo.items.isNotEmpty() }
                                    ?.let { lastVideo: SearchResult.VideoSearchResult ->
                                        if (channel.lastVideoPublishedAt == null || channel.lastVideoPublishedAt != lastVideo.items[0].snippet.publishedAt) {
                                            dao.updateLastVideoPublishDate(
                                                publishDate = lastVideo.items[0].snippet.publishedAt,
                                                channelId = channel._id,
                                            )
                                            dao.getUsersSubscribedToChannel(channel._id).forEach { user: User ->
                                                CoroutineScope(Dispatchers.IO + notificationJob).launch {
                                                    telegramBotClient.sendNotification(
                                                        telegramId = user.telegramId,
                                                        video = lastVideo.items[0],
                                                    )
                                                }
                                            }
                                        }

                                    }
                            }
                        }.joinAll()
                    }
            }
        }
    }
}
