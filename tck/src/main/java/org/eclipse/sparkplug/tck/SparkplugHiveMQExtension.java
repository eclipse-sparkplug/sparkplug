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

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import org.eclipse.sparkplug.tck.test.TCK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian Craggs
 * @author Lukas Brand
 */
public class SparkplugHiveMQExtension implements ExtensionMain {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    @Override
    public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput,
                               final @NotNull ExtensionStartOutput extensionStartOutput) {

        try {
            logger.info("Starting Sparkplug Extension");

            final TCK aTCK = new TCK();

            final ConnectInterceptor connectInterceptor = new ConnectInterceptor(aTCK);
            Services.interceptorRegistry().setConnectInboundInterceptorProvider(input -> connectInterceptor);

            final SubscribeInterceptor subscribeInterceptor = new SubscribeInterceptor(aTCK);
            final PublishInterceptor publishInterceptor = new PublishInterceptor(aTCK);
            final DisconnectInterceptor disconnectInterceptor = new DisconnectInterceptor(aTCK);
            // create a new client initializer
            final ClientInitializer clientInitializer = (initializerInput, clientContext) -> {
                // add the interceptors to the context of the connecting client
                clientContext.addSubscribeInboundInterceptor(subscribeInterceptor);
                clientContext.addPublishInboundInterceptor(publishInterceptor);
                clientContext.addDisconnectInboundInterceptor(disconnectInterceptor);
            };

            // register the client initializer
            Services.initializerRegistry().setClientInitializer(clientInitializer);

        } catch (final Exception e) {
            logger.error("Exception thrown at extension start: ", e);
        }
    }

    @Override
    public void extensionStop(final @NotNull ExtensionStopInput extensionStopInput,
                              final @NotNull ExtensionStopOutput extensionStopOutput) {
        logger.info("Stopping Sparkplug Extension");
    }
}