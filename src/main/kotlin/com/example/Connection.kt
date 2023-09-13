package com.example

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object{
        val id = AtomicInteger(0)
    }
    val name = "user${id.getAndIncrement()}"
}