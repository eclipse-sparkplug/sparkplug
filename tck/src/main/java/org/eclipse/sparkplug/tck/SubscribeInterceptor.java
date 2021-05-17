/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

public class SubscribeInterceptor implements SubscribeInboundInterceptor {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private TCK theTCK = null;
	
	public SubscribeInterceptor(TCK aTCK) {
		theTCK = aTCK;
	}

	@Override
	public void onInboundSubscribe(@NotNull SubscribeInboundInput subscribeInboundInput,
			@NotNull SubscribeInboundOutput subscribeInboundOutput) {
		try {
			String clientId = subscribeInboundInput.getClientInformation().getClientId();
			logger.info("Inbound subscribe from '{}'", clientId);
			
			SubscribePacket packet = subscribeInboundInput.getSubscribePacket();
			
			logger.info("\tTopic {}", packet.getSubscriptions().get(0).getTopicFilter());
			
			theTCK.subscribe(clientId, packet);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
}