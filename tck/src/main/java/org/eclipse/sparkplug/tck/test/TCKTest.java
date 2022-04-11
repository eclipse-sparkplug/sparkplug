/* ******************************************************************************
 * Copyright (c) 2021, 2022 Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ian Craggs - initial implementation and documentation
 ****************************************************************************** */

package org.eclipse.sparkplug.tck.test;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.Monitor;

/**
 * @author Ian Craggs
 * @author Lukas Brand
 */
public abstract class TCKTest {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	public abstract void connect(String clientId, ConnectPacket packet);

	public abstract void disconnect(String clientId, DisconnectPacket packet);

	public abstract void subscribe(String clientId, SubscribePacket packet);

	public abstract void publish(String clientId, PublishPacket packet);

	public abstract String getName();

	public abstract Map<String, String> getResults();

	public abstract String[] getTestIds();

	public abstract void endTest(Map<String, String> results);

	public void reportResults(final @NotNull Map<String, String> results) {
		logger.info("Summary Test Results for {} ", getName());

		final StringBuilder summary = Utils.getSummary(results);

		final PublishService publishService = Services.publishService();
		final Publish message = Builders.publish().topic("SPARKPLUG_TCK/RESULT").qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(summary.toString().getBytes())).build();
		publishService.publish(message);
	}
}