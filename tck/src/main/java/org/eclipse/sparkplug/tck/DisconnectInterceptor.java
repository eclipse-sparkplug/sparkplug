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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundOutput;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian Craggs
 * @author Lukas Brand
 */
public class DisconnectInterceptor implements DisconnectInboundInterceptor {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    private final @NotNull TCK theTCK;

    public DisconnectInterceptor(final @NotNull TCK theTCK) {
        this.theTCK = theTCK;
    }

    @Override
    public void onInboundDisconnect(final @NotNull DisconnectInboundInput disconnectInboundInput,
                                    final @NotNull DisconnectInboundOutput disconnectInboundOutput) {
        try {
            final String clientId = disconnectInboundInput.getClientInformation().getClientId();

            logger.info("Inbound disconnect from '{}'", clientId);

            final DisconnectPacket packet = disconnectInboundInput.getDisconnectPacket();
            theTCK.disconnect(clientId, packet);
        } catch (final Exception e) {
            logger.error("Disconnect Exception", e);
        }
    }
}