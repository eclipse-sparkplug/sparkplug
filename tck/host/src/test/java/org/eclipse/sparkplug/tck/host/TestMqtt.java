package org.eclipse.sparkplug.tck.host;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.validation.constraints.NotNull;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.testcontainer.core.HiveMQExtension;
import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;

public class TestMqtt {

	private static Logger logger = LoggerFactory.getLogger(TestMqtt.class.getName());

	@RegisterExtension
	public final @NotNull HiveMQTestContainerExtension extension =
			new HiveMQTestContainerExtension().withExtension(HiveMQExtension.builder().id("extension-1")
					.name("sparkplug-extension").version("1.0.0-SNAPSHOT").mainClass(SparkplugExtension.class).build());

	@Test
	public void mqttTest() throws InterruptedException {
		try {
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			MqttClient client = new MqttClient("tcp://localhost:" + extension.getMqttPort(), "Sparkplug Client");
			client.setTimeToWait(2000);
			client.connect(options);

			logger.info("Client connected? {}", client.isConnected());
			assertTrue(client.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
