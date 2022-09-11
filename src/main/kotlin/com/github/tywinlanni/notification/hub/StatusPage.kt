package com.github.tywinlanni.notification.hub

import com.github.tywinlanni.notification.hub.dao.MongoDb
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun StatusPagesConfig.customExceptions() {

    exception { call: ApplicationCall, cause: MongoDb.MongoException ->
        //logger.warn(cause.message)

        call.respondText(text = cause.message, status = HttpStatusCode.Conflict)
    }
}
