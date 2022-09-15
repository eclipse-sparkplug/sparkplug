/*
 * Copyright © 2021, 2022 Ian Craggs and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_TEST_CONTROL_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;

/**
 * @author Ian Craggs
 * @author Lukas Brand
 */
public class PublishInterceptor implements PublishInboundInterceptor, PublishOutboundInterceptor {

	private final static @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	private final TCK theTCK;

	public PublishInterceptor(final @NotNull TCK theTCK) {
		this.theTCK = theTCK;
	}

	@Override
	public void onInboundPublish(final @NotNull PublishInboundInput publishInboundInput,
			final @NotNull PublishInboundOutput publishInboundOutput) {
		try {
			final String clientId = publishInboundInput.getClientInformation().getClientId();
			final PublishPacket packet = publishInboundInput.getPublishPacket();
			final String topic = packet.getTopic();
			logger.debug("Inbound publish from '{}' at {} ", clientId, topic);
			if (packet.getPayload().isPresent()) {
				final ByteBuffer payloadByteBuffer = packet.getPayload().get();
				final String payload = StandardCharsets.UTF_8.decode(payloadByteBuffer).toString();
				logger.trace("\tPayload {}", payload);

				if (topic.equals(TCK_LOG_TOPIC)) {
					logger.debug(clientId + ": " + payload); // display log message
				}

				if (topic.equals(TCK_CONSOLE_TEST_CONTROL_TOPIC)) {
					String cmd = "NEW_TEST";
					if (payload.toUpperCase().startsWith(cmd)) {
						// find all tokens which are either plain tokens or
						// containing whitespaces and therefore surrounded with double quotes
						final Pattern tokenPattern = Pattern.compile("(\"[^\"]+\")|\\S+");
						final Matcher matcher = tokenPattern.matcher(payload.trim());
						final List<String> tokens = new ArrayList<>();
						while (matcher.find()) {
							tokens.add(matcher.group());
						}
						final String[] strings = tokens.stream().map(token -> {
								if (token.startsWith("\"") && token.endsWith("\"")) {
									return token.substring(1, token.length() - 1);
								} else {
									return token;
								}
							}).toArray(String[]::new);

						if (strings.length < 3) {
							throw new RuntimeException("New test syntax is: NEW_TEST profile testname <parameters>");
						}

						final int no_parms = strings.length - 3;
						final String[] parms = new String[no_parms];
						if (no_parms > 0) {
							System.arraycopy(strings, 3, parms, 0, no_parms);
						}
						theTCK.newTest(strings[1], strings[2], parms);
					} else {
						cmd = "END_TEST";
						if (payload.toUpperCase().trim().equals(cmd)) {
							theTCK.endTest();
						}
					}
				} else
					theTCK.publish(clientId, packet);
			}
		} catch (final Exception e) {
			logger.error("Publish Exception", e);
		}
	}

	@Override
	public void onOutboundPublish(@NotNull PublishOutboundInput publishOutboundInput, @NotNull PublishOutboundOutput publishOutboundOutput) {
		final String clientId = publishOutboundInput.getClientInformation().getClientId();
		final PublishPacket packet = publishOutboundInput.getPublishPacket();
		final String topic = packet.getTopic();
		logger.debug("Outbound publish from '{}' at {} ", clientId, topic);
		theTCK.publish(clientId, packet);
	}
}