/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck.host;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundOutput;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

public class LifecycleInterceptor implements ConnectInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger(LifecycleInterceptor.class.getName());

	@Override
	public void onConnect(@NotNull ConnectInboundInput connectInboundInput,
			@NotNull ConnectInboundOutput connectInboundOutput) {
		try {
			logger.info("Inbound connect from '{}'", connectInboundInput.getClientInformation().getClientId());
			logger.info("\tInet Address {}", connectInboundInput.getConnectionInformation().getInetAddress());
			logger.info("\tMQTT Version {}", connectInboundInput.getConnectionInformation().getMqttVersion());
			logger.info("\tClean Start {}", connectInboundInput.getConnectPacket().getCleanStart());
			logger.info("\tKeep Alive {}", connectInboundInput.getConnectPacket().getKeepAlive());

			Optional<WillPublishPacket> willPublishPacketOptional =
					connectInboundInput.getConnectPacket().getWillPublish();
			if (willPublishPacketOptional.isPresent()) {
				WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
				final PublishService publishService = Services.publishService();

				ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
				if (payload != null && "OFFLINE".equals(StandardCharsets.UTF_8.decode(payload).toString())) {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-payload\": \"PASS\"}".getBytes()))
							.build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				} else {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-payload\": \"FAIL\"}".getBytes()))
							.build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				}

				if (willPublishPacket.getQos() == Qos.AT_LEAST_ONCE) {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-qos\": \"PASS\"}".getBytes())).build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				} else {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-qos\": \"FAIL\"}".getBytes())).build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				}

				if (willPublishPacket.getRetain()) {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-retain\": \"PASS\"}".getBytes()))
							.build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				} else {
					Publish message = Builders.publish().topic("SPARKPLUG_TCK_RESULT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap("{\"host-topic-phid-death-retain\": \"FAIL\"}".getBytes()))
							.build();
					publishService.publishToClient(message, "Sparkplug TCK Client");
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
}