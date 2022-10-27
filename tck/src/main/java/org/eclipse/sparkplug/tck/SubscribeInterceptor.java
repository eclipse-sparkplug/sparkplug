/********************************************************************************
 * Copyright (c) 2021-2022 Cirrus Link Solutions and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Cirrus Link Solutions - initial implementation
 ********************************************************************************/

package org.eclipse.sparkplug.tck;

import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

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

			SubscribePacket packet = subscribeInboundInput.getSubscribePacket();

			logger.debug("Inbound subscribe from '{}' topic {}", clientId,
					packet.getSubscriptions().get(0).getTopicFilter());

			theTCK.subscribe(clientId, packet);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
}