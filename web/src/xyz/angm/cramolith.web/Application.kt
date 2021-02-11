/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 7:18 PM.
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.sessions.*
import xyz.angm.cramolith.server.database.DB
import xyz.angm.cramolith.server.database.Player
import xyz.angm.cramolith.server.database.Players

fun main(args: Array<String>) = EngineMain.main(args)
fun startWeb() = main(emptyArray())

data class LoginSession(val id: Int)

@Suppress("unused")
fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
    }
    install(Sessions) {
        cookie<LoginSession>(
            "LOGIN_COOKIE",
            storage = SessionStorageMemory()
        ) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        static("/static") {
            resources("static")
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("message" to "")))
        }
        get("/register") {
            call.respond(FreeMarkerContent("register.ftl", mapOf("error" to "")))
        }
        get("/changelog") {
            call.respond(FreeMarkerContent("changelog.ftl", mapOf("" to "")))
        }
        get("/about_us") {
            call.respond(FreeMarkerContent("about_us.ftl", mapOf("" to "")))
        }

        get("/settings") {
            val session = call.sessions.get<LoginSession>()
            if (session != null) {
                call.respond(FreeMarkerContent("settings.ftl", mapOf("user" to DB.transaction {
                    Player.findById(session.id)!!
                })))
            } else {
                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "")))
            }
        }

        post("/register/submit") {
            val input = call.receiveParameters()
            val username = input["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val pw = input["pw"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val pwConfirm = input["pw-confirm"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val playerExists = DB.transaction { !Player.find { Players.name eq username }.empty() }

            val error = when {
                pw != pwConfirm -> "Passwords are not the same"
                playerExists -> "Username already exists"
                else -> null
            }

            return@post if (error == null) {
                val id = DB.transaction {
                    Player.new {
                        name = username
                        password = pw
                        posX = 100
                        posY = 100
                        posMap = 0
                        triggeredActors = HashMap()
                    }.id
                }
                call.sessions.set(LoginSession(id = id.value))
                call.respond(FreeMarkerContent("index.ftl", mapOf("message" to "Successfully registered. Welcome, $username!")))
            } else {
                call.respond(FreeMarkerContent("register.ftl", mapOf("error" to error)))
            }
        }

        post("/login/submit") {
            val input = call.receiveParameters()
            val username = input["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val pw = input["pw"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val player = DB.transaction { Player.find { Players.name eq username }.firstOrNull() }

            val error = when {
                player == null -> "Unknown user"
                player.password != pw -> "Wrong password"
                else -> null
            }

            return@post if (error == null) {
                call.sessions.set(LoginSession(id = player!!.id.value))
                call.respond(FreeMarkerContent("index.ftl", mapOf("message" to "Successfully logged in. Welcome back, $username!")))
            } else {
                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to error)))
            }
        }

        post("/settings/submit") {
            val session = call.sessions.get<LoginSession>() ?: return@post call.respondRedirect("/login")
            val input = call.receiveParameters()
            val username = input["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val pw = input["pw"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            DB.transaction {
                val player = Player.findById(session.id) ?: return@transaction null
                player.name = username
                player.password = pw
                Unit
            } ?: return@post call.respond(HttpStatusCode.BadRequest)

            call.respond(FreeMarkerContent("index.ftl", mapOf("message" to "Successfully changed settings.")))
        }
    }
}