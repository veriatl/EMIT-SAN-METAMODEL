# Sensor & Actuator Network MQTT Clients

## Installation

Clone this repository and run `mvn package -DskipTests=true` at root.

## Usage

Run the jar containing the dependencies using `java -jar client-$version-jar-with-dependencies.jar`, with the right arguments.

```
usage: ./client.jar [-a] [-id <id>] [-p <port>] -s <server> [-S] [-t <topic> <QoS>]
 -a,--actuator              The client is an actuator, once started, it is going to idle, waiting for broker's messages.
 -id,--identifier <id>      The client identified.
 -p,--port <port>           Specific port, other than 8883.
 -s,--server <server>       URI of the MQTT broker. Default port: 8883
 -S,--sensor                The client is a sensor, once started, the prompt will ask for messages to send to the broker.
 -t,--topic <topic> <QoS>   Topic to subscribe or publish to, and its QoS.

```

## Examples

### Creating a sensor publishing on the given topic

`java -jar client.jar -s tcp://localhost -p 1883 -id sensor01 -t topic01 0 --sensor`

This creates a sensor. The messages have to be provided by hand in the console, and will be sent to the provided topic with the given QoS.

### Creating an actuator publishing on the given topic

`java -jar client.jar -s tcp://localhost -p 1883 -id actuator01 -t topic01 0 --actuator`

This creates an actuator, that listens to the given topic.

`client.jar` refers to the jar with dependencies produced in the target folder, after a maven build. 
