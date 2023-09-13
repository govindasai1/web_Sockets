package com.example.plugins

import com.example.Connection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
    }
    routing {
        webSocket("/single_session") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                            outgoing.send(Frame.Text("CONNECTION DISCONNECTING...."))
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
        val  connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/multi_sessions") {
            val connection = Connection(this)
            connections += connection
                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if (receivedText == "bye") {
                        connections.forEach {
                            it.session.send("${connection.name} IS DISCONNECTED")
                            connections -= connection
                        }
                    } else {
                        val textWithUsername = "[${connection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                        }
                    }

                }

        }
    }
}
