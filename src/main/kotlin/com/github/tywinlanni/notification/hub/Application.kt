package com.github.tywinlanni.notification.hub

import com.github.tywinlanni.notification.hub.configuration.loadConfiguration
import com.github.tywinlanni.notification.hub.dao.MongoDb
import com.github.tywinlanni.notification.hub.https.registerYoutubeRoutes
import com.github.tywinlanni.notification.hub.telegramBotClient.TelegramBotClient
import com.github.tywinlanni.notification.hub.watcher.YoutubeWatcher
import com.github.tywinlanni.notification.hub.youtubeClient.YoutubeClient
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {

    val configuration = loadConfiguration()

    install(Resources)
    install(Authentication) {
        basic {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == configuration.auth.base.userName && credentials.password == configuration.auth.base.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    install(StatusPages) {
        customExceptions()
    }
    install(ContentNegotiation) {
        json()
    }

    val dao = MongoDb(KMongo.createClient().coroutine, configuration.databaseConfiguration)
    val telegramBotClient = TelegramBotClient(configuration.telegramConfiguration)
    val youtubeClient = YoutubeClient(configuration.youTubeConfiguration)
    val youtubeWatcher = runBlocking {
        YoutubeWatcher
            .Builder(dao, youtubeClient, telegramBotClient)
            .loadChannels()
            .startWatching()
            .build()
    }

    routing {
        //authenticate("auth-basic") {
            registerYoutubeRoutes(youtubeClient, dao, youtubeWatcher)
        //}
    }
}
