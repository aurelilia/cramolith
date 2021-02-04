/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.web

import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.*
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import xyz.angm.cramolith.server.database.DB
import xyz.angm.cramolith.server.database.Player
import xyz.angm.cramolith.server.database.Players
import java.time.LocalDateTime

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
    }

    routing {
        static("/static") {
            resources("static")
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf<String, String>()))
        }
        post("/submit"){
            val input = call.receiveParameters()
            if (input["pw"] == input["pw-confirm"]){
                call.respond(FreeMarkerContent("index.ftl", mapOf<String, String>()))
            }else{
                call.respond(FreeMarkerContent("index.ftl", mapOf("error" to "Passwords are not the same")))
            }
            if(Player.find { Players.name eq "hmm." }.empty()){
                val username = input["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val pw = input["pw"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                DB.transaction {
                    Player.new{
                        name = username
                        password = pw
                    }
                }
                call.respond(FreeMarkerContent("index.ftl", mapOf<String, String>()))
            }else{
                call.respond(FreeMarkerContent("index.ftl", mapOf("error" to "Username already exists")))
            }
        }
    }
}