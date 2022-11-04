/*******************************************************************************
 * Copyright (c) 2022 Anja Helmbrecht-Schaar HiveMQ
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Anja Helmbrecht-Schaar HiveMQ - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.broker.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.eclipse.sparkplug.tck.test.broker.test.results.AsciiCharsInClientIdTestResults;
import org.eclipse.sparkplug.tck.test.broker.test.results.ClientIdLengthTestResults;
import org.eclipse.sparkplug.tck.test.broker.test.results.ComplianceTestResult;
import org.eclipse.sparkplug.tck.test.broker.test.results.PayloadTestResults;
import org.eclipse.sparkplug.tck.test.broker.test.results.QosTestResult;
import org.eclipse.sparkplug.tck.test.broker.test.results.SharedSubscriptionTestResult;
import org.eclipse.sparkplug.tck.test.broker.test.results.TopicLengthTestResults;
import org.eclipse.sparkplug.tck.test.broker.test.results.TopicUtils;
import org.eclipse.sparkplug.tck.test.broker.test.results.Tuple;
import org.eclipse.sparkplug.tck.test.broker.test.results.WildcardSubscriptionsTestResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient.Mqtt3Publishes;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3ConnAckException;
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3SubAckException;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;

public class BrokerConformanceFeatureTester {

	private static final String ONE_BYTE = "a";
	private static final int MAX_TOPIC_LENGTH = 65535;
	private static final int MAX_CLIENT_ID_LENGTH = 65535;
	private static final Logger Logger = LoggerFactory.getLogger("Sparkplug");
	private final String host;
	private final int port;
	private final String username;
	private final ByteBuffer password;
	private final MqttClientSslConfig sslConfig;
	private final int timeOut;
	private int maxTopicLength = -1;
	private int maxClientIdLength = -1;
	private MqttQos maxQos = MqttQos.EXACTLY_ONCE;

	public BrokerConformanceFeatureTester(final @NotNull String host, final @NotNull Integer port,
			final @Nullable String username, final @Nullable ByteBuffer password,
			final @Nullable MqttClientSslConfig sslConfig, final int timeOut) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.sslConfig = sslConfig;
		this.timeOut = timeOut;
	}

	// Test methods

	public @Nullable Mqtt3ConnAck testConnectWithWill() {
		final String topic =
				(maxTopicLength == -1 ? TopicUtils.generateTopicUUID() : TopicUtils.generateTopicUUID(maxTopicLength));
		Logger.debug(" .        COMPLIANCE CHECK: Testing Connect with Will");
		final Mqtt3Client client = buildClient("ConformanceTestPublisher");
		final Mqtt3Publish will = Mqtt3Publish.builder().topic(topic).qos(MqttQos.AT_LEAST_ONCE)
				.payload("payload".getBytes()).retain(true).build();

		try {
			final Mqtt3ConnAck connAck = client.toBlocking().connectWith().willPublish(will).send();
			Logger.debug(" .        COMPLIANCE CHECK: Received {}", connAck);
			disconnectIfConnected(client);
			return connAck;
		} catch (final Mqtt3ConnAckException ex) {
			Logger.debug(" .        COMPLIANCE CHECK: Failed to connect MQTT 3 client", ex);
			return ex.getMqttMessage();
		}
	}

	public @NotNull WildcardSubscriptionsTestResult testWildcardSubscriptions() {
		final ComplianceTestResult plusWildcardResult = testWildcard("+", "test");
		final ComplianceTestResult hashWildcardResult = testWildcard("#", "test/subtopic");

		return new WildcardSubscriptionsTestResult(plusWildcardResult, hashWildcardResult);
	}

	public @NotNull SharedSubscriptionTestResult testSharedSubscription() {
		Logger.debug(" .        COMPLIANCE CHECK: Testing shared subscriptions");

		final String topic =
				(maxTopicLength == -1 ? TopicUtils.generateTopicUUID() : TopicUtils.generateTopicUUID(maxTopicLength));
		final String sharedTopic = "$share/" + UUID.randomUUID().toString().replace("-", "") + "/" + topic;

		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client sharedSubscriber1 = buildClient("sharedSubscriber1");
		final Mqtt3Client sharedSubscriber2 = buildClient("sharedSubscriber2");

		final Mqtt3Subscribe sharedSubscribe = Mqtt3Subscribe.builder().topicFilter(sharedTopic).qos(maxQos).build();
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

		publisher.toBlocking().connect();
		sharedSubscriber1.toBlocking().connect();
		sharedSubscriber2.toBlocking().connect();

		try {
			sharedSubscriber1.toBlocking().subscribe(sharedSubscribe);
			sharedSubscriber2.toBlocking().subscribe(sharedSubscribe);
		} catch (final Mqtt3SubAckException ex) {
			Logger.error("Could not subscribe to topic {} with qos {}", sharedTopic, maxQos, ex);
			disconnectIfConnected(sharedSubscriber1, sharedSubscriber2);
			return SharedSubscriptionTestResult.SUBSCRIBE_FAILED;
		}

		long startTime = 0;

		Logger.debug(" .        COMPLIANCE CHECK: Subscribing first subscriber to shared topic {} with qos {}",
				sharedTopic, maxQos);
		sharedSubscriber1.toAsync().subscribeWith().topicFilter(sharedTopic).qos(maxQos).callback(publish -> {
			Logger.debug(" .        COMPLIANCE CHECK: Subscriber 1 received {}", publish);
			if (countDownLatch.getCount() != 0) {
				countDownLatch.countDown();
			} else {
				atomicBoolean.set(true);
			}
		}).send().join();

		Logger.debug(" .        COMPLIANCE CHECK: Subscribing second subscriber to shared topic {} with qos {}",
				sharedTopic, maxQos);
		sharedSubscriber2.toAsync().subscribeWith().topicFilter(sharedTopic).qos(maxQos).callback(publish -> {
			Logger.debug(" .        COMPLIANCE CHECK: Subscriber 2 received {}", publish);
			if (countDownLatch.getCount() != 0) {
				countDownLatch.countDown();
			} else {
				atomicBoolean.set(true);
			}
		}).send().join();

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Publishing to shared topic {} with qos {}", sharedTopic, maxQos);
			publisher.toBlocking().publishWith().topic(topic).payload("test".getBytes()).qos(maxQos).send();
			startTime = System.currentTimeMillis();
		} catch (Exception e) {
			Logger.error("Could not publish to topic " + sharedTopic, e);
			disconnectIfConnected(sharedSubscriber1, sharedSubscriber2);
			return SharedSubscriptionTestResult.PUBLISH_FAILED;
		}

		boolean timedOut;
		long timeToReceive = 0;

		try {
			timedOut = !countDownLatch.await(timeOut, TimeUnit.SECONDS);
			timeToReceive = System.currentTimeMillis() - startTime;
		} catch (InterruptedException e) {
			Logger.error("Waiting for subscribers to receive shared publishes interrupted", e);
			disconnectIfConnected(sharedSubscriber1, sharedSubscriber2);
			return SharedSubscriptionTestResult.INTERRUPTED;
		}

		if (timedOut) {
			Logger.debug(" .        COMPLIANCE CHECK: Timed out while waiting for shared subscription publish");
			return SharedSubscriptionTestResult.TIME_OUT;
		}

		try {
			Thread.sleep(100 + timeToReceive);
		} catch (InterruptedException e) {
			Logger.error("Waiting additional time for second subscriber interrupted", e);
			disconnectIfConnected(sharedSubscriber1, sharedSubscriber2);
			return SharedSubscriptionTestResult.INTERRUPTED;
		}

		disconnectIfConnected(sharedSubscriber1, sharedSubscriber2);

		final boolean result = atomicBoolean.get();

		final SharedSubscriptionTestResult testResult =
				result ? SharedSubscriptionTestResult.NOT_SHARED : SharedSubscriptionTestResult.OK;

		Logger.debug(" .        COMPLIANCE CHECK: Result of testing shared subscriptions: {}", testResult);

		return testResult;
	}

	public @NotNull ComplianceTestResult testRetain() {
		Logger.debug(" .        COMPLIANCE CHECK: Testing retained messages");

		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client subscriber = buildClient("ConformanceTestSubscriber");

		final String topic =
				(maxTopicLength == -1 ? TopicUtils.generateTopicUUID() : TopicUtils.generateTopicUUID(maxTopicLength));
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		publisher.toBlocking().connect();

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Publishing retained message '{}' to topic {} with qos {}",
					"RETAIN", topic, maxQos);
			publisher.toBlocking().publishWith().topic(topic).qos(maxQos).retain(true).payload("RETAIN".getBytes())
					.send();
		} catch (final Exception ex) {
			Logger.error("Retained publish failed", ex);
			disconnectIfConnected(publisher);
			return ComplianceTestResult.PUBLISH_FAILED;
		} finally {
			disconnectIfConnected(publisher);
		}

		subscriber.toBlocking().connect();

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Subscribing to topic {} with qos {}", topic, maxQos);
			subscriber.toAsync().subscribeWith().topicFilter(topic).qos(maxQos).callback(publish -> {
				Logger.debug(" .        COMPLIANCE CHECK: Subscriber received {}", publish);
				if (publish.isRetain()) {
					countDownLatch.countDown();
				}
			}).send().join();
		} catch (final Exception ex) {
			Logger.error("Retained subscribe failed", ex);
			disconnectIfConnected(subscriber);
			return ComplianceTestResult.SUBSCRIBE_FAILED;
		}

		try {
			countDownLatch.await(timeOut, TimeUnit.SECONDS);
		} catch (final InterruptedException ex) {
			Logger.error("Interrupted while waiting for retained publish to arrive at subscriber", ex);
		}

		disconnectIfConnected(publisher, subscriber);

		final ComplianceTestResult complianceTestResult =
				countDownLatch.getCount() == 0 ? ComplianceTestResult.OK : ComplianceTestResult.TIME_OUT;

		Logger.debug(" .        COMPLIANCE CHECK: Result of testing retained messages: {}", complianceTestResult);

		return complianceTestResult;
	}

	public @NotNull QosTestResult testQos(final @NotNull MqttQos qos, final int tries) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing qos {} with {} tries", qos, tries);

		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client subscriber = buildClient("ConformanceTestSubscriber");

		final String topic = TopicUtils.generateTopicUUID(maxTopicLength);
		final byte[] payload = qos.toString().getBytes();

		subscriber.toBlocking().connect();
		publisher.toBlocking().connect();

		final CountDownLatch countDownLatch = new CountDownLatch(tries);
		final AtomicInteger totalReceived = new AtomicInteger(0);

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Subscribing to topic {} with qos {}", topic, qos);
			subscriber.toAsync().subscribeWith().topicFilter(topic).qos(qos).callback(publish -> {
				Logger.debug(" .        COMPLIANCE CHECK: Subscriber received {}", publish);
				if (publish.getQos() == qos && Arrays.equals(publish.getPayloadAsBytes(), payload)) {
					totalReceived.incrementAndGet();
					countDownLatch.countDown();
				}
			}).send().join();
		} catch (final Exception ex) {
			Logger.error("Could not subscribe with QoS {}", qos.ordinal(), ex);
		}

		final long before = System.nanoTime();

		for (int i = 0; i < tries; i++) {
			try {
				Logger.debug(" .        COMPLIANCE CHECK: Publishing message {} to topic {} with qos {}",
						new String(payload, StandardCharsets.UTF_8), topic, qos);
				publisher.toBlocking().publishWith().topic(topic).qos(qos).payload(payload).send();
			} catch (final Exception ex) {
				countDownLatch.countDown();
				Logger.error("Could not publish with QoS {}", qos.ordinal());
			}
		}

		try {
			countDownLatch.await(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.error("Interrupted while waiting for QoS {} publish to arrive at subscriber", qos.ordinal());
		}

		final long after = System.nanoTime();
		final long timeToComplete = after - before;

		disconnectIfConnected(publisher, subscriber);

		if (totalReceived.get() > 0 && qos.ordinal() > maxQos.ordinal()) {
			Logger.debug(" .        COMPLIANCE CHECK: Setting maxQos from {} to {} for the next tests", maxQos, qos);
			maxQos = qos;
		}

		Logger.debug(" .        COMPLIANCE CHECK: Result of testing qos {}: Received {} / {} publishes", qos,
				totalReceived, tries);

		return new QosTestResult(totalReceived.get(), timeToComplete);
	}

	public @NotNull PayloadTestResults testPayloadSize(final int maxSize) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing payload size until max. payload size of {} bytes", maxSize);

		final List<Tuple<Integer, ComplianceTestResult>> testResults = new LinkedList<>();
		final String topic =
				(maxTopicLength == -1 ? TopicUtils.generateTopicUUID() : TopicUtils.generateTopicUUID(maxTopicLength));

		final boolean maxTestSuccess = testPayload(topic, testResults, maxSize);
		if (maxTestSuccess) {
			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. payload size: {} bytes", maxSize);
			return new PayloadTestResults(maxSize, testResults);
		} else { // Binary search the payload size
			int top = maxSize;
			int bottom = 0;
			int mid = -1;
			while (bottom <= top) {
				mid = (bottom + top) / 2;
				final boolean success = testPayload(topic, testResults, mid);
				if (success) {
					bottom = mid + 1;
				} else {
					top = mid - 1;
				}
			}

			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. payload size: {} bytes", mid);
			return new PayloadTestResults(mid, testResults);
		}
	}

	private boolean testPayload(final @NotNull String topic,
			final @NotNull List<Tuple<Integer, ComplianceTestResult>> testResults, final int payloadSize) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing payload with {} bytes", payloadSize);
		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client subscriber = buildClient("ConformanceTestSubscriber");
		final String currentPayload = Strings.repeat(ONE_BYTE, payloadSize);
		final Mqtt3Publish publish =
				Mqtt3Publish.builder().topic(topic).qos(maxQos).payload(currentPayload.getBytes()).build();

		subscriber.toBlocking().connect();

		Logger.debug(" .        COMPLIANCE CHECK: Subscribing to topic {} with qos {}", topic, maxQos);
		subscriber.toBlocking().subscribeWith().topicFilter(topic).qos(maxQos).send();
		final Mqtt3Publishes publishes = subscriber.toBlocking().publishes(MqttGlobalPublishFilter.SUBSCRIBED);

		try {
			publisher.toBlocking().connect();
			Logger.debug(" .        COMPLIANCE CHECK: Publishing payload with {} bytes to topic {} with qos {}",
					payloadSize, topic, maxQos);
			publisher.toBlocking().publish(publish);
		} catch (final Exception ex) {
			Logger.error("Failed to publish with payload of {} bytes", currentPayload.getBytes().length, ex);
			testResults.add(new Tuple<>(payloadSize, ComplianceTestResult.PUBLISH_FAILED));
			disconnectIfConnected(publisher, subscriber);
			return false;
		}

		try {
			final Optional<Mqtt3Publish> receive = publishes.receive(timeOut, TimeUnit.SECONDS);
			if (!receive.isPresent()) {
				disconnectIfConnected(publisher, subscriber);
				Logger.debug(" .        COMPLIANCE CHECK: Timed out while waiting for publish with {} bytes",
						currentPayload.getBytes().length);
				testResults.add(new Tuple<>(payloadSize, ComplianceTestResult.TIME_OUT));
				disconnectIfConnected(publisher, subscriber);
				return false;
			} else if (!Arrays.equals(receive.get().getPayloadAsBytes(), currentPayload.getBytes())) {
				disconnectIfConnected(publisher, subscriber);
				Logger.debug(" .        COMPLIANCE CHECK: Received wrong payload for publish with {} bytes",
						currentPayload.getBytes().length);
				testResults.add(new Tuple<>(payloadSize, ComplianceTestResult.WRONG_PAYLOAD));
				disconnectIfConnected(publisher, subscriber);
				return false;
			}
		} catch (InterruptedException e) {
			Logger.error("Interrupted while waiting for subscriber to receive payload with length {} bytes",
					currentPayload.getBytes().length, e);
			testResults.add(new Tuple<>(payloadSize, ComplianceTestResult.INTERRUPTED));
			disconnectIfConnected(publisher, subscriber);
			return false;
		}

		disconnectIfConnected(publisher, subscriber);

		testResults.add(new Tuple<>(payloadSize, ComplianceTestResult.OK));
		return true;
	}

	public @NotNull TopicLengthTestResults testTopicLength() {
		Logger.debug(" .        COMPLIANCE CHECK: Testing topic length");

		final List<Tuple<Integer, ComplianceTestResult>> testResults = new LinkedList<>();
		final boolean maxTopicLengthSuccess = testTopic(testResults, MAX_TOPIC_LENGTH);
		if (maxTopicLengthSuccess) {
			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. topic length: {} bytes", MAX_TOPIC_LENGTH);
			return new TopicLengthTestResults(MAX_TOPIC_LENGTH, testResults);
		} else { // Binary search the right topic length
			int top = MAX_TOPIC_LENGTH;
			int bottom = 0;
			int mid = -1;

			while (bottom <= top) {
				mid = (bottom + top) / 2;
				if (mid == 0)
					return new TopicLengthTestResults(0, testResults);
				final boolean success = testTopic(testResults, mid);
				if (success) {
					bottom = mid + 1;
				} else {
					top = mid - 1;
				}
			}

			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. topic length: {} bytes", mid);
			Logger.debug(" .        COMPLIANCE CHECK: Setting max. topic length to {} for the next tests", mid);
			setMaxTopicLength(mid);
			return new TopicLengthTestResults(mid, testResults);
		}
	}

	private boolean testTopic(final @NotNull List<Tuple<Integer, ComplianceTestResult>> testResults,
			final int topicSize) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing topic with length of {} bytes", topicSize);

		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client subscriber = buildClient("ConformanceTestSubscriber");

		final String currentTopicName = Strings.repeat(ONE_BYTE, topicSize);
		final Mqtt3Publish publish =
				Mqtt3Publish.builder().topic(currentTopicName).qos(maxQos).payload(currentTopicName.getBytes()).build();
		final Mqtt3Subscribe subscribe = Mqtt3Subscribe.builder().topicFilter(currentTopicName).qos(maxQos).build();

		subscriber.toBlocking().connect();
		final Mqtt3Publishes publishes = subscriber.toBlocking().publishes(MqttGlobalPublishFilter.SUBSCRIBED);

		// Test subscribe to topic
		try {
			Logger.debug(" .        COMPLIANCE CHECK: Subscribing to topic with {} bytes with qos {}", topicSize,
					maxQos);
			subscriber.toBlocking().subscribe(subscribe);
		} catch (final Exception ex) {
			Logger.error("Failed to subscribe to topic with a length of {} bytes", currentTopicName.getBytes().length,
					ex);
			testResults.add(new Tuple<>(topicSize, ComplianceTestResult.SUBSCRIBE_FAILED));
			return false;
		}

		// Test publish to topic
		try {
			publisher.toBlocking().connect();
			Logger.debug(" .        COMPLIANCE CHECK: Publishing to topic with {} bytes with qos {}", topicSize,
					maxQos);
			publisher.toBlocking().publish(publish);
		} catch (final Exception ex) {
			Logger.error("Failed to publish to topic with {} bytes", currentTopicName.getBytes().length, ex);
			testResults.add(new Tuple<>(topicSize, ComplianceTestResult.PUBLISH_FAILED));
			return false;
		} finally {
			disconnectIfConnected(publisher);
		}

		// Subscriber retrieves payload
		try {
			final Optional<Mqtt3Publish> receive = publishes.receive(timeOut, TimeUnit.SECONDS);
			if (!receive.isPresent()) {
				Logger.debug(" .        COMPLIANCE CHECK: Timed out while waiting to receive a publish from topic {}",
						currentTopicName);
				testResults.add(new Tuple<>(topicSize, ComplianceTestResult.TIME_OUT));
				disconnectIfConnected(subscriber, publisher);
				return false;
			} else if (!Arrays.equals(receive.get().getPayloadAsBytes(), currentTopicName.getBytes())) {
				Logger.debug(" .        COMPLIANCE CHECK: Received wrong payload for publish to topic {}",
						currentTopicName);
				testResults.add(new Tuple<>(topicSize, ComplianceTestResult.WRONG_PAYLOAD));
				disconnectIfConnected(subscriber, publisher);
				return false;
			}
		} catch (InterruptedException e) {
			Logger.error("Interrupted while waiting to receive publish to topic with {} bytes",
					currentTopicName.getBytes().length, e);
			testResults.add(new Tuple<>(topicSize, ComplianceTestResult.INTERRUPTED));
			return false;
		} finally {
			disconnectIfConnected(subscriber);
		}

		// Everything successful
		testResults.add(new Tuple<>(topicSize, ComplianceTestResult.OK));
		return true;
	}

	public @NotNull ClientIdLengthTestResults testClientIdLength() {
		Logger.debug(" .        COMPLIANCE CHECK: Testing max. client identifier length");

		final List<Tuple<Integer, String>> connectResults = new LinkedList<>();

		final boolean maxClientIdSuccess = testClientIdLength(connectResults, MAX_CLIENT_ID_LENGTH);
		if (maxClientIdSuccess) {
			maxClientIdLength = MAX_CLIENT_ID_LENGTH;
			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. client identifier length: {} bytes",
					MAX_CLIENT_ID_LENGTH);
			return new ClientIdLengthTestResults(MAX_CLIENT_ID_LENGTH, connectResults);
		} else { // Binary search the right client id length
			int top = MAX_CLIENT_ID_LENGTH;
			int bottom = 0;
			int mid = -1;
			while (bottom <= top) {
				mid = (bottom + top) / 2;
				final boolean success = testClientIdLength(connectResults, mid);
				if (success) {
					bottom = mid + 1;
				} else {
					top = mid - 1;
				}
			}

			Logger.debug(" .        COMPLIANCE CHECK: Result of testing max. client identifier length: {} bytes", mid);
			Logger.debug(
					" .        COMPLIANCE CHECK: Setting max. client identifier length to {} bytes for further tests",
					mid);
			maxClientIdLength = mid;
			return new ClientIdLengthTestResults(mid, connectResults);
		}
	}

	private boolean testClientIdLength(final @NotNull List<Tuple<Integer, String>> connectResults,
			final int clientIdLength) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing client identifier with a length of {} bytes", clientIdLength);

		final String currentIdentifier = Strings.repeat(ONE_BYTE, clientIdLength);
		final Mqtt3Client currClient = getClientBuilder("ConformanceTestClient").identifier(currentIdentifier).build();

		try {
			final Mqtt3ConnAck connAck = currClient.toBlocking().connect();
			connectResults.add(new Tuple<>(clientIdLength, connAck.getReturnCode().toString()));
			if (connAck.getReturnCode() != Mqtt3ConnAckReturnCode.SUCCESS) {
				Logger.debug(" .        COMPLIANCE CHECK: Received non-successful return code {}",
						connAck.getReturnCode());
				return false;
			}
		} catch (final Mqtt3ConnAckException connAckEx) {
			connectResults.add(new Tuple<>(clientIdLength, connAckEx.getMqttMessage().getReturnCode().toString()));
			return false;
		} catch (final Exception ex) {
			Logger.error("Connect with client id length {} bytes",
					currClient.getConfig().getClientIdentifier().map(id -> id.toString().getBytes().length).orElse(0),
					ex);
			connectResults.add(new Tuple<>(clientIdLength, "UNDEFINED_FAILURE"));
			return false;
		} finally {
			disconnectIfConnected(currClient);
		}

		return true;

	}

	private @NotNull ComplianceTestResult testWildcard(final String subscribeWildcardTopic, final String publishTopic) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing wildcard {} on topic {}", subscribeWildcardTopic,
				publishTopic);

		final Mqtt3Client publisher = buildClient("ConformanceTestPublisher");
		final Mqtt3Client subscriber = buildClient("ConformanceTestSubscriber");

		final String topic =
				(maxTopicLength == -1 ? TopicUtils.generateTopicUUID() : TopicUtils.generateTopicUUID(maxTopicLength));
		final String subscribeToTopic = topic + "/" + subscribeWildcardTopic;
		final String publishToTopic = topic + "/" + publishTopic;
		final byte[] payload = "WILDCARD_TEST".getBytes();

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final Consumer<Mqtt3Publish> publishCallback = publish -> {
			if (Arrays.equals(publish.getPayloadAsBytes(), payload)) {
				countDownLatch.countDown();
			}
		};

		subscriber.toBlocking().connect();
		publisher.toBlocking().connect();

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Subscribing to wildcard topic {} with qos {}", subscribeToTopic,
					maxQos);
			subscriber.toAsync().subscribeWith().topicFilter(subscribeToTopic).qos(maxQos).callback(publishCallback)
					.send().join();
		} catch (final Exception ex) {
			disconnectIfConnected(subscriber, publisher);
			Logger.error("Failed to subscribe to wildcard topic {}", subscribeToTopic, ex);
			return ComplianceTestResult.SUBSCRIBE_FAILED;
		}

		try {
			Logger.debug(" .        COMPLIANCE CHECK: Publishing to wildcard topic {} with qos {}", publishTopic,
					maxQos);
			publisher.toBlocking().publishWith().topic(publishToTopic).qos(maxQos).payload(payload).send();
		} catch (final Exception ex) {
			disconnectIfConnected(subscriber, publisher);
			Logger.error("Failed to publish to wildcard topic {}", publishToTopic, ex);
			return ComplianceTestResult.PUBLISH_FAILED;
		}

		try {
			countDownLatch.await(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.error("Interrupted while subscription to {} receives publish to {}", subscribeToTopic,
					publishToTopic, e);
		}

		disconnectIfConnected(publisher, subscriber);
		final ComplianceTestResult complianceTestResult =
				countDownLatch.getCount() == 0 ? ComplianceTestResult.OK : ComplianceTestResult.TIME_OUT;
		Logger.debug(" .        COMPLIANCE CHECK: Result of testing wildcard topic {}: {}", subscribeWildcardTopic,
				complianceTestResult);

		return complianceTestResult;
	}

	public @NotNull AsciiCharsInClientIdTestResults testAsciiCharsInClientId() {
		Logger.debug(" .        COMPLIANCE CHECK: Testing ascii characters in client identifier");

		final String ASCII = " !\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~";
		final List<Tuple<Character, String>> connectResults = new LinkedList<>();

		boolean allSuccess = false;
		final Mqtt3Client client = getClientBuilder("TestClient").identifier(ASCII).build();

		if (ASCII.length() <= maxClientIdLength) {
			try {
				Logger.debug(" .        COMPLIANCE CHECK: Testing client identifier '{}'", ASCII);
				client.toBlocking().connect();
				allSuccess = true;
			} catch (Exception ex) {
				Logger.error("Could not connect with Client ID '" + ASCII + "'", ex);
			} finally {
				disconnectIfConnected(client);
			}
		}

		if (allSuccess) {
			Logger.debug(" .        COMPLIANCE CHECK: Result of testing ascii characters: All supported");
			return new AsciiCharsInClientIdTestResults(connectResults);
		} else {
			for (int i = 0; i < ASCII.length(); i++) {
				testAsciiChar(connectResults, ASCII.charAt(i));
			}
			Logger.debug(
					" .        COMPLIANCE CHECK: Result of testing ascii character in client identifier: Unsupported characters {}",
					connectResults.toString());
			return new AsciiCharsInClientIdTestResults(connectResults);
		}
	}

	private void testAsciiChar(final @NotNull List<Tuple<Character, String>> connectResults, final char asciiChar) {
		Logger.debug(" .        COMPLIANCE CHECK: Testing ascii character '{}'", asciiChar);
		final Mqtt3Client client = getClientBuilder("TestClient").identifier(String.valueOf(asciiChar)).build();
		try {
			client.toBlocking().connect();
		} catch (final Mqtt3ConnAckException ex) {
			Logger.debug(" .        COMPLIANCE CHECK: Could not connect client identifier with ascii char '{}'",
					asciiChar, ex);
			connectResults.add(new Tuple<>(asciiChar, ex.getMqttMessage().getReturnCode().toString()));
		} catch (final Exception ex) {
			Logger.error("Could not connect client identifier with ascii char '{}'", asciiChar, ex);
			connectResults.add(new Tuple<>(asciiChar, null));
		}
		disconnectIfConnected(client);
	}

	// Getter / Setter

	public void setMaxTopicLength(final int topicLength) {
		maxTopicLength = topicLength;
	}

	public void setMaxQos(final @NotNull MqttQos qos) {
		maxQos = qos;
	}

	// Helpers
	private @NotNull Mqtt3Client buildClient(String identifier) {
		return getClientBuilder(identifier).build();
	}

	private @NotNull Mqtt3ClientBuilder getClientBuilder(String identifier) {
		return Mqtt3Client.builder().identifier(identifier).serverHost(host).serverPort(port).simpleAuth(buildAuth())
				.sslConfig(sslConfig);
	}

	private @Nullable Mqtt3SimpleAuth buildAuth() {
		if (username != null && password != null) {
			return Mqtt3SimpleAuth.builder().username(username).password(password).build();
		} else if (username != null) {
			Mqtt3SimpleAuth.builder().username(username).build();
		} else if (password != null) {
			throw new IllegalArgumentException("Password-Only Authentication is not allowed in MQTT 3");
		}
		return null;
	}

	private void disconnectIfConnected(final @NotNull Mqtt3Client... clients) {
		for (Mqtt3Client client : clients) {
			if (client.getState().isConnected()) {
				client.toBlocking().disconnect();
			}
		}
	}
}
