package fr.icam

import io.vertx.core.Vertx
import io.vertx.mqtt.MqttServer
import io.vertx.mqtt.MqttServerOptions
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import kotlin.concurrent.thread


class MainTest {

    private var baos : ByteArrayOutputStream? = null
    private var oldOut : PrintStream? = null
    private var oldIn : InputStream? = null

    @Before
    fun setUp() {
        //Catching the standard output
        baos = ByteArrayOutputStream()
        val ps = PrintStream(baos)
        oldOut = System.out
        oldIn = System.`in`
        System.setOut(ps)
    }

    @After
    fun tearDown() {

        //Resetting the standard output
        System.setOut(oldOut)
        System.setIn(oldIn)
    }

    @Test
    fun checkOnlyServer() {

        var options = MqttServerOptions().setPort(8883)
        var server = MqttServer.create(Vertx.vertx(), options)
        var receivedConnectionRequest = false

        server.endpointHandler { endpoint ->
            receivedConnectionRequest = true
            endpoint.accept(false)
            server.close() //Closes the server on connection
        }.listen()

        Thread.sleep(500)

        val t1 = thread {
            main(arrayOf("-s", "tcp://localhost"))
        }

        Thread.sleep(500)
        t1.interrupt()

        Assert.assertTrue(receivedConnectionRequest)
    }

    @Test
    fun checkServerId() {
        var options = MqttServerOptions().setPort(8883)
        var server = MqttServer.create(Vertx.vertx(), options)
        var receivedConnectionRequest = false
        var clientId = ""

        server.endpointHandler { endpoint ->
            receivedConnectionRequest = true
            clientId = endpoint.clientIdentifier()
            endpoint.accept(false)
            server.close() //Closes the server on connection
        }.listen()
        Thread.sleep(500)

        Assert.assertEquals("", clientId)

        val t1 = thread {
            main(arrayOf("-s", "tcp://localhost", "-id", "testClientId"))
        }

        Thread.sleep(500)
        t1.interrupt()

        Assert.assertTrue(receivedConnectionRequest)
        Assert.assertEquals("testClientId", clientId)
    }

    @Test
    fun checkServerPort() {
        val port = 8814
        var options = MqttServerOptions().setPort(port)
        var server = MqttServer.create(Vertx.vertx(), options)
        var receivedConnectionRequest = false

        server.endpointHandler { endpoint ->
            receivedConnectionRequest = true
            endpoint.accept(false)
            server.close() //Closes the server on connection
        }.listen()
        Thread.sleep(500)

        val t1 = thread {
            main(arrayOf("-s", "tcp://localhost", "-p", port.toString()))
        }

        Thread.sleep(500)
        t1.interrupt()

        Assert.assertTrue(receivedConnectionRequest)
    }

    @Test
    fun checkServerTopic() {

        var options = MqttServerOptions().setPort(8883)
        var server = MqttServer.create(Vertx.vertx(), options)
        var receivedConnectionRequest = false
        var receivedTopicRequest = false

        server.endpointHandler { endpoint ->
            endpoint.subscribeHandler{
                receivedTopicRequest = true
                server.close()
            }

            receivedConnectionRequest = true
            endpoint.accept(false)
        }.listen()

        Thread.sleep(500)

        val t1 = thread {
            main(arrayOf("-s", "tcp://localhost", "-t", "topic/my_topic", "0"))
        }
        Thread.sleep(500)

        t1.interrupt()

        Assert.assertTrue(receivedConnectionRequest)
        Assert.assertTrue(receivedTopicRequest)
    }



}
