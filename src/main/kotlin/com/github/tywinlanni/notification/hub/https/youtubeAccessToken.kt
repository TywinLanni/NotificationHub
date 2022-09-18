package com.github.tywinlanni.notification.hub.https

import com.github.tywinlanni.notification.hub.dao.DAO
import com.github.tywinlanni.notification.hub.databaseEntity.YouTubeChannel
import com.github.tywinlanni.notification.hub.https.resources.YouTubeResource
import com.github.tywinlanni.notification.hub.watcher.YoutubeWatcher
import com.github.tywinlanni.notification.hub.youtubeClient.Snippet
import com.github.tywinlanni.notification.hub.youtubeClient.YoutubeClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

fun Route.registerYoutubeRoutes(youtubeClient: YoutubeClient, dao: DAO, youtubeWatcher: YoutubeWatcher) {
    get<YouTubeResource.FindChannelByNAmeResource> { youtubeChannel ->
        call.respond(
            message = youtubeClient.searchChannelByName(youtubeChannel.channelName)
        )
    }

    get<YouTubeResource.NextPageResource> { nextPage ->
        call.respond(
            message = youtubeClient.searchChannelByNameNextPage(nextPage.nextPageToken)
        )
    }

    post<YouTubeResource.AddMonitoredChannelResource> { telegramUser ->
        val channel: Snippet.Channel = call.receive()

        dao.addMonitoredChannel(
            userTelegramId = telegramUser.userTelegramId,
            youTubeChannel = YouTubeChannel(
                channelId = channel.channelId,
                channelName = channel.channelTitle,
                previewURL = channel.thumbnails.default.url,
                description = channel.description,
            ),
        )

        call.respond(
            message = HttpStatusCodeContent(HttpStatusCode.Accepted)
        )
    }
}
