/*******************************************************************************
 * Copyright (c) 2021, 2023 Anja Helmbrecht-Schaar
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Anja Helmbrecht-Schaar - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.broker;

import static org.eclipse.sparkplug.tck.test.broker.CompliantBrokerTest.checkCompliance;
import static org.eclipse.sparkplug.tck.test.common.Constants.PASS;
import static org.eclipse.sparkplug.tck.test.common.Constants.SPARKPLUG_AWARE_ROOT;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_BASIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_AWARE_STORE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_BASIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_AWARE_STORE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_QOS0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_QOS1;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_WILL_MESSAGES;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.broker.test.BrokerAwareFeatureTester;
import org.eclipse.sparkplug.tck.test.broker.test.results.AwareTestResult;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscription;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class AwareBrokerTest extends TCKTest {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	public final static @NotNull List<String> testIds = List.of(ID_CONFORMANCE_MQTT_AWARE_BASIC,
			ID_CONFORMANCE_MQTT_AWARE_STORE, ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC,
			ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN, ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC,
			ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN, ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP);
	private TCK theTCK = null;
	private @NotNull String host;
	private @NotNull String port;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private boolean bNBirthChecked = false;
	private boolean bDBirthChecked = false;
	private boolean bNDeathChecked = false;
	private boolean bBasicAwareChecked = false;
	private boolean bStoreAwareChecked = false;
	private AwareTestResult createSubscriptionResult = AwareTestResult.NOT_SUBSCRIBED;
	private Mqtt3Client subscriber;

	private long willTimestamp;
	private BrokerAwareFeatureTester brokerAwareFeatureTester;

	public AwareBrokerTest(TCK aTCK, String[] params) {
		logger.info("Broker: {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		if (params.length < 4) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters must be: host and port, groupId and egdeNodeId ");
			throw new IllegalArgumentException();
		}
		host = params[0];
		port = params[1];
		groupId = params[2];
		edgeNodeId = params[3];

		Services.extensionExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				// create subscriber client
				brokerAwareFeatureTester =
						new BrokerAwareFeatureTester(host, Integer.parseInt(port), null, null, null, 60);
				subscriber = brokerAwareFeatureTester.getClientBuilder("AwareTestSubscriber").build();
				try {
					subscriber.toAsync().connect().get();
				} catch (InterruptedException | ExecutionException e) {
					logger.error(e.getMessage());
				}
			}
		});
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		brokerAwareFeatureTester.finish(subscriber);
		reportResults(testResults);
	}

	public String getName() {
		return "Broker SparkplugAware";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		if (packet.getClientId() == edgeNodeId && packet.getWillPublish().isPresent()) {
			logger.info("AWARE:: Broker - {} - connect - clientId: {}", getName(), clientId);
			// catch the will message and store the timestamp for later comparison
			willTimestamp = System.currentTimeMillis();
		}

		if (clientId.equals("AwareTestSubscriber")) {
			logger.info("AWARE:: Broker - {} - connect - clientId: {}", getName(), clientId);
			createSubscriptionToSysTopic(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/#");
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		//
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		//
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		final String packetTopic = packet.getTopic();
		logger.info("AWARE:: Broker - {} - publish - topic: {}", getName(), packetTopic);

		if (!bBasicAwareChecked) {
			// async to continue the publish flow!
			Services.extensionExecutorService().submit(new Runnable() {
				@Override
				public void run() {
					checkConformanceAware(host, Integer.parseInt(port));
				}
			});
		}

		if (isUsedTopic(packetTopic) && packetTopic.contains(TOPIC_PATH_NDEATH)) {
			checkNDEATHAware(packet);
		}

		if (bBasicAwareChecked && bNBirthChecked && bDBirthChecked && bNDeathChecked) {
			logger.debug("AWARE:: Broker {}, publish - end test", getName());
			theTCK.endTest();
		}
	}

	private boolean isUsedTopic(String topic) {
		final String[] topicLevels = topic.split("/");
		boolean isSparkplugTopic = topicLevels[0].equals(TOPIC_ROOT_SP_BV_1_0);
		if (isSparkplugTopic && topicLevels[1].equals(groupId) && topicLevels[3].equals(edgeNodeId)) {
			logger.debug("AWARE:: Broker - Found a topic for used group and edge nodes {}", topic);
			return true;
		}

		logger.error("AWARE:: Broker - Skip not the used group and edge nodes {}", topic);
		return false;
	}

	private void checkPublishOnSysTopic(Boolean isRetain, String packetTopic) {
		final String sparkplugTopic = packetTopic.substring(SPARKPLUG_AWARE_ROOT.length());
		final String[] topicLevels = sparkplugTopic.split("/");

		final boolean ok = isUsedTopic(sparkplugTopic);
		boolean isNBIRTHTopic = ok && topicLevels[2].equals(TOPIC_PATH_NBIRTH);
		boolean isDBIRTHTopic = ok && topicLevels[2].equals(TOPIC_PATH_DBIRTH);

		if (!isNBIRTHTopic && !isDBIRTHTopic) {
			logger.error("AWARE:: Broker - Skip no (N|D)BIRTH sys-topic {}", packetTopic);
			return;
		}

		if (isNBIRTHTopic) {
			checkNBIRTHAware(isRetain);
		}

		if (isDBIRTHTopic) {
			checkDBIRTHAware(isRetain);
		}

		if (bNBirthChecked && bDBirthChecked) {
			checkStoreAware();
		}

		if (bBasicAwareChecked && bNBirthChecked && bDBirthChecked && bNDeathChecked) {
			logger.debug("AWARE:: Broker {}, checkPublishOnSysTopic - end test", getName());
			theTCK.endTest();
		}
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC)
	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN)
	public void checkNBIRTHAware(Boolean isRetain) {
		logger.debug("AWARE:: Broker - Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC);
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC,
				setResult(true, CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC));

		logger.debug("AWARE:: Broker - Check Req: {} {}", ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN, isRetain);
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN,
				setResult(isRetain, CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN));
		bNBirthChecked = true;
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC)
	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN)
	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP)

	public void checkDBIRTHAware(Boolean isRetain) {

		logger.debug("AWARE:: Broker - Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC);
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC,
				setResult(true, CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC));

		logger.debug("AWARE:: Broker - Check Req: {} {}", ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN, isRetain);
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN,
				setResult(isRetain, CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN));

		bDBirthChecked = true;
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP)

	public void checkNDEATHAware(PublishPacket packet) {
		logger.debug("AWARE:: Broker - Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP);
		SparkplugBProto.PayloadOrBuilder result = Utils.getSparkplugPayload(packet);
		boolean bValid = result.getTimestamp() > willTimestamp;
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP,
				setResult(bValid, CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP));
		bNDeathChecked = true;
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_BASIC)
	public void checkConformanceAware(final String host, final int port) {
		logger.info("AWARE:: Broker - {} - Start", Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER);
		logger.debug("AWARE:: Broker - Check Req: {} ", CONFORMANCE_MQTT_AWARE_BASIC);

		checkCompliance(host, port, testResults);
		boolean isBasicAware = testResults.get(ID_CONFORMANCE_MQTT_QOS0).equals(PASS)
				&& testResults.get(ID_CONFORMANCE_MQTT_QOS1).equals(PASS)
				&& testResults.get(ID_CONFORMANCE_MQTT_WILL_MESSAGES).equals(PASS)
				&& testResults.get(ID_CONFORMANCE_MQTT_RETAINED).equals(PASS);
		testResults.put(ID_CONFORMANCE_MQTT_AWARE_BASIC, setResult(isBasicAware, CONFORMANCE_MQTT_AWARE_BASIC));
		bBasicAwareChecked = true;
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_AWARE_STORE)
	public void checkStoreAware() {
		logger.info("AWARE:: Broker - {} - Start", Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER);
		logger.debug("AWARE:: Broker - Check Req: {} ", CONFORMANCE_MQTT_AWARE_STORE);
		final String pass1 = testResults.get(ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC);
		final String pass2 = testResults.get(ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC);

		testResults.put(ID_CONFORMANCE_MQTT_AWARE_STORE,
				setResult(pass1.equals(PASS) && pass2.equals(PASS), CONFORMANCE_MQTT_AWARE_STORE));
	}

	public void createSubscriptionToSysTopic(String origin) {
		logger.info("AWARE:: Broker - {} - create subscription to sys topic for {} {}", getName(), groupId, edgeNodeId);
		final String topic = SPARKPLUG_AWARE_ROOT + origin;
		final Mqtt3Subscription subscription =
				Mqtt3Subscription.builder().topicFilter(topic).qos(MqttQos.AT_LEAST_ONCE).build();
		try {
			logger.debug("AWARE:: Broker - subscribing to sys topic {} with qos {}", topic, MqttQos.AT_LEAST_ONCE);
			subscriber.toAsync().subscribeWith().addSubscription(subscription).callback(publish -> {
				logger.info("AWARE:: Broker - receive message on sys topic: {} {}", publish.getTopic(),
						publish.isRetain());
				checkPublishOnSysTopic(publish.isRetain(), publish.getTopic().toString());
			}).send().whenComplete((subAck, throwable) -> {
				if (throwable != null) {
					logger.error("AWARE:: Broker - Subscribe failed: {}", throwable.getMessage());
				} else {
					logger.info("AWARE:: Broker - Successful subscribed to topic: " + subscription.getTopicFilter());
				}
			});

		} catch (final Exception ex) {
			logger.error("Failed to subscribe to topic ", ex);
			createSubscriptionResult = AwareTestResult.SUBSCRIBE_FAILED;
		}
	}
}
