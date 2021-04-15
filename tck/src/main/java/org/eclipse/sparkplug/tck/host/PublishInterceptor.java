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
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.eclipse.sparkplug.tck.host.test.TCK;

public class PublishInterceptor implements PublishInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private TCK theTCK = null;
	
	public PublishInterceptor(TCK aTCK) {
		theTCK = aTCK;
	}

	@Override
	public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput,
			@NotNull PublishInboundOutput publishInboundOutput) {
		try {
			String clientId = publishInboundInput.getClientInformation().getClientId();
			logger.info("Inbound publish from '{}'", clientId);
					
			PublishPacket packet = publishInboundInput.getPublishPacket();
			
			String topic = packet.getTopic();
			logger.info("\tTopic {}", topic);
			
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			logger.info("\tPayload {}", payload);
			

			if (topic.equals("SPARKPLUG_TCK/TEST_CONTROL")) {
				String cmd = "NEW ";
				if (payload.startsWith(cmd)) {
					theTCK.newTest(payload.substring(cmd.length()).trim());
				} else {	
					cmd = "END TEST";
					if (payload.trim().equals(cmd)) {
						theTCK.endTest();
					}	
				}
			}
			else 
				theTCK.publish(clientId, packet);
			
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
}