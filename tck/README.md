# Sparkplug TCK

This is the Sparkplug Test Compatibility Kit (TCK) or test suite.

# Building

Build the TCK with the command 

	gradlew build

in this directory. This will result in an extension for the HiveMQ MQTT Broker in the tck/build/hivemq-extension directory.


## Further Gradle build tasks
Hivemq provides its own gradle task set, with some further useful tasks

    hivemq extension
        hivemqExtensionJar 
        ...
        hivemqExtensionZip 
        runHivemqWithExtension

* Using `./gradlew runHivemqWithExtension` allows running (and debug) HiveMQ directly in your IDE. With this the HiveMQ community edition is automatically downloaded and fully configured for the TCK extension by the tasks from the gradle build file.

* Using `./gradlew hivemqExtensionZip` builds a zip file, that contains all necessary files for extension usage and move this into a HiveMQ broker installation.

## Running HiveMQ with Sparkplug-TCK Extension

  * You'll need to have a HiveMQ broker installed. The [HiveMQ Community edition](https://www.hivemq.com/developers/community/) is open source and freely available.
    * Download complete, including the hivemq-allow-all-extension

  * Copy the extension distribution file (tck/build/hivemq-extension/sparkplug-tck-3.0.0-SNAPSHOT.zip) to the HiveMQ extension directory (extensions).

  * Unzip the sparkplug-tck-3.0.0-SNAPSHOT.zip in the extension folder.
    
The target folder should have the following content. 

```
|-| extensions/
  |-| sparkplug-tck/
    |--  hivemq-extension.xml
    |--  sparkplug-tck-3.0.0-SNAPSHOT.jar
```

Add a websocket listener to the HiveMQ broker.
(Example files for configuration config.xml and logging logback.xml can be found in the hivemq-configuration folder)

	<hivemq>
		<listeners>
			<tcp-listener>
				<port>1883</port>
				<bind-address>0.0.0.0</bind-address>
			</tcp-listener>
			<websocket-listener>
				<port>8000</port>
				<bind-address>0.0.0.0</bind-address>
				<path>/mqtt</path>
				<name>my-websocket-listener</name>
				<subprotocols>
					<subprotocol>mqttv3.1</subprotocol>
					<subprotocol>mqtt</subprotocol>
				</subprotocols>
				<allow-extensions>true</allow-extensions>
			</websocket-listener>
		</listeners>
		<anonymous-usage-statistics>
			<enabled>true</enabled>
		</anonymous-usage-statistics>
	</hivemq>

Finally, when starting the HiveMQ broker you should see the messages:

	INFO  - Starting Sparkplug Extension
	INFO  - Extension "Sparkplug TCK Tests" version 3.0.0-SNAPSHOT started successfully.

Check that connecting to the broker is allowed. (Per default setup, the hivemq-allow-all extension is enabled.)

# Web console

The TCK is controlled by a web console. To build and run the console, see its [README](webconsole/README.md).

Open "localhost:3000" in a web browser.

# Tests

The tests are grouped into two sections, Host and Edge.

Host:
- session establishment
- session termination
- send command
- receive data

Edge:
- session establishment
- session termination
- receive command
- send data
- complex payloads

## Usage

1. Fill in the MQTT server configuration. Use the "Default Values" button and then press connect. If you successfully connect, then no further configuration is necessary here.

2. In the "Sparkplug Client Configuration" box, enter your Host Application id for the system you want to test.

3. Now you can run the Host tests, filling in the parameter boxes for each.

4. To run the Edge tests, select "Edge of Network Node" in the "Sparkplug Client Configuration" box. Fill in your Group and Edge Node ids.

5. Now you can run the Edge tests, filling in the parameter boxes for each.

If any test does not finish automatically, you can press the "Abort Test" button to stop it and report on the results so far. 

The "Reset Test" button will clear the results for that test.

The results of running the tests are collected in a file named "SparkplugTCKresults.txt" in the HiveMQ execution directory.

# To-do list

* Ensure all code adheres to the Sparkplug coding format (as implemented in the Eclipse editor plugin)

* Complete assertion coverage in host and edge node profile tests
  ** Host receive data test: there are no assertions for this at the moment. Should there be some to define what the
  reaction of the Host Application should be?
  ** The host EdgeNodeDeathTest also currently contains no assertions. Should the host be setting the state of each
  device attached to the disconnecting node proactively, or taking any other action?
  ** Review previously experienced Sparkplug issues as gathered by Inductive Automation/Cirrus link. Add any extra tests
  needed to ensure these scenarios are tested in the TCK.

* Finish off web console
  ** Ensure all tests can be run from it and results reported
  ** Get feedback from trial users to improve its usability

* Ensure all tests can be run on a system with other MQTT/Sparkplug traffic. This means identifying the Sparkplug
  components of concern and only checking those. The principle problem is matching Sparkplug ids to MQTT clients - in
  many cases (all?) this can be done by looking at the id used in the death message (the will messge) in the MQTT
  connect packet.

* Add all possible assertion tests to the MQTT client listener. This can then be run on a live Sparkplug system with any
  MQTT broker, not just HiveMQ. As it's communicating over MQTT, it can't inspect any packets other than publications.

* Documentation - I imagine this could be mostly incorporated into the web console, so that the separate "Getting started" could be quite short.  We'll see.
