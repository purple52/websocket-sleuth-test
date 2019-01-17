package com.example

import com.fasterxml.jackson.databind.ObjectMapper
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [
            Application::class
        ]
)
class WebsocketTest {

    @Autowired
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @LocalServerPort
    private lateinit var port: String

    @Value("\${server.servlet.contextPath}")
    private lateinit var contextPath: String

    @Test
    fun `Can pass message via websocket`() {
        val session = createStompSession()

        var messagesReceived = 0

        session.subscribe(
                "/topic/foo",
                object : StompFrameHandler {
                    override fun handleFrame(headers: StompHeaders, payload: Any) {
                        messagesReceived += 1
                    }

                    override fun getPayloadType(headers: StompHeaders): Type {
                        return MyEvent::class.java
                    }
                }
        )

        simpMessagingTemplate.convertAndSend("/topic/foo", MyEvent("test"))

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted {
                    MatcherAssert.assertThat(
                            "Didn't receive any messages",
                            messagesReceived,
                            CoreMatchers.`is`(1)
                    )
                }
    }


    private fun createStompSession(): StompSession {
        return WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient()))))
                .apply {
                    messageConverter = MappingJackson2MessageConverter().apply {
                        objectMapper = this@WebsocketTest.objectMapper
                    }
                }.connect(
                        "ws://localhost:$port$contextPath/websocket",
                        null as WebSocketHttpHeaders?,
                        StompHeaders(),
                        object : StompSessionHandlerAdapter() {}
                ).get(1, TimeUnit.SECONDS)
    }
}

data class MyEvent(
        val dummy: String
)
