package com.github.tywinlanni.notification.hub.https.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource(path = "/youtube")
class YouTubeResource {
    @Serializable
    @Resource(path = "findChannelByName")
    data class FindChannelByNAmeResource(
        val parent: YouTubeResource = YouTubeResource(),
        val channelName: String,
    )

    @Serializable
    @Resource(path = "nextPage")
    data class NextPageResource(
        val parent: YouTubeResource = YouTubeResource(),
        val nextPageToken: String,
        val channelName: String,
    )

    @Serializable
    @Resource(path = "addMonitoredChannel")
    data class AddMonitoredChannelResource(
        val parent: YouTubeResource = YouTubeResource(),
        val userTelegramId: Long,
    )
}
