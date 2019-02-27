package fr.icam

import org.apache.commons.cli.*
import org.eclipse.paho.client.mqttv3.*
import java.util.*
import kotlin.concurrent.thread


private var port: Int = 0
private var qos : Int = 1
private lateinit var id: String
private lateinit var server: String
private lateinit var topic: String
private lateinit var san: SAN

fun main(args: Array<String>) {
    val options = generateOptions()
    val parser = DefaultParser()
    var cmd : CommandLine? = null

    try {
        cmd = parser.parse(options, args)
    } catch (e: ParseException) {
        HelpFormatter().printHelp("./client.jar", options, true)
        System.exit(1)
    }

    server = cmd!!.getOptionValue("server")
    port    = if (cmd.hasOption("port")) Integer.valueOf(cmd.getOptionValue("port")) else 8883
    id      = if (cmd.hasOption("id")) cmd.getOptionValue("id") else UUID.randomUUID().toString()
    san     = if (cmd.hasOption("sensor")) SAN.SENSOR else SAN.ACTUATOR
    topic   = if (cmd.hasOption("topic")) cmd.getOptionValues("topic")[0] else ""
    qos     = if (cmd.hasOption("topic")) Integer.valueOf(cmd.getOptionValues("topic")[1]) else 1

    val client = MqttClient("$server:$port", id)

    //Set callback to subscribe to topic once the client is connected
    client.setCallback(object: MqttCallbackExtended {
        override fun connectComplete(p0: Boolean, p1: String?) {
            println("> Successfully connected to broker")

            if (cmd.hasOption("topic")) {
                client.subscribe(cmd.getOptionValues("topic")[0], Integer.valueOf(cmd.getOptionValues("topic")[1]))
                println("> Subscribed to $topic")
            }
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            if (san == SAN.ACTUATOR)
                println("> $topic: $message")
        }

        override fun connectionLost(p0: Throwable?) {
            println("> Lost connection with the broker")
        }

        override fun deliveryComplete(p0: IMqttDeliveryToken?) {

        }
    })

    client.connect()
    mainLoop(client)
}

private fun mainLoop(client: MqttClient?) {

    if (san == SAN.SENSOR) {
        var input = ""
        println("> Enter 'exit' to leave.")

        while(input != "exit") {
            input = readLine()!!
            thread {
                println("> publishing $input on topic $topic")
                client!!.publish(topic, input.toByteArray(), qos, true)
            }
        }
    } else {
        println("> Now listening to topic $topic.")
        println("> CTRL-C to leave.")
    }

}

private fun generateOptions() : Options {
    val options = Options()

    options.addOption(Option
            .builder("s")
            .longOpt("server")
            .argName("server")
            .desc("URI of the MQTT broker. Default port: 8883")
            .hasArg(true)
            .optionalArg(false)
            .required(true).build())

    options.addOption(Option
            .builder("p")
            .longOpt("port")
            .argName("port")
            .desc("Specific port, other than 8883.")
            .hasArg(true)
            .optionalArg(false).build())

    options.addOption(Option
            .builder("t")
            .longOpt("topic")
            .desc("Topic to subscribe or publish to, and its QoS.")
            .argName("topic> <QoS")
            .valueSeparator(' ')
            .numberOfArgs(2)
            .build())

    options.addOption(Option
            .builder("id")
            .longOpt("identifier")
            .argName("id")
            .desc("The client identified.")
            .hasArg(true)
            .optionalArg(false).build())

    options.addOption(Option
            .builder("a")
            .longOpt("actuator")
            .hasArg(false)
            .desc("The client is an actuator, once started, it is going to idle, waiting for broker's messages.")
            .build())

    options.addOption(Option
            .builder("S")
            .longOpt("sensor")
            .hasArg(false)
            .desc("The client is a sensor, once started, the prompt will ask for messages to send to the broker.")
            .build())

    return options
}


