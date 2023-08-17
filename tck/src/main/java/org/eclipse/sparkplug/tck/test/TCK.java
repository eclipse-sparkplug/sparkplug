/*******************************************************************************
 * Copyright (c) 2021, 2023 Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ian Craggs - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.eclipse.sparkplug.tck.test.common.Constants.Profile;
import org.eclipse.sparkplug.tck.utility.EdgeNode;
import org.eclipse.sparkplug.tck.utility.HostApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.TreeMap;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_RESULTS_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;

/**
 * @author Ian Craggs
 */
public class TCK {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger("Sparkplug");
    private final HostApplication hostApps = new HostApplication();
    private final EdgeNode edgeNode = new EdgeNode();
    private @Nullable TCKTest current = null;
    /**
     * The hasMonitor variable indicates whether the Monitor class should be run for a test. This is switched off for
     * the Broker profile, and on for the Host and Edge profiles. The Monitor class holds tests for assertions that
     * don't neatly fit into a single test scenario, or apply all the time, so it runs alongside all Host and Edge
     * tests.
     */
    private @NotNull Boolean hasMonitor = true;
    final Results results = new Results(Results.SPARKPLUG_TCKRESULTS_LOG, null, null);
    private final Monitor monitor = new Monitor(results);
    final Utilities utilities = new Utilities(monitor, hostApps, edgeNode);
    public TCK() {
        results.initialize(new String[0]);
    }

    final PublishService publishService = Services.publishService();
    
    public void MQTTResults(String message) {
        final Publish payload = Builders.publish().topic(TCK_RESULTS_TOPIC).qos(Qos.AT_LEAST_ONCE)
                .payload(ByteBuffer.wrap(message.getBytes())).build();
        publishService.publish(payload);
    }
    
    public void MQTTLog(String message) {
        final Publish payload = Builders.publish().topic(TCK_LOG_TOPIC).qos(Qos.AT_LEAST_ONCE)
                .payload(ByteBuffer.wrap(message.getBytes())).build();
        publishService.publish(payload);
    }
    
    public void newTest(final @NotNull Profile profile, final @NotNull String test, final @NotNull String[] parms) {

        LOGGER.info("Test requested " + profile.name().toLowerCase() + " " + test);

        try {
            final Class testClass =
                    Class.forName("org.eclipse.sparkplug.tck.test." + profile.name().toLowerCase() + "." + test);

            try {
                final Class[] types = {this.getClass(), String[].class};
                final Constructor constructor = testClass.getConstructor(types);
                final Object[] parameters = {this, parms};
                current = (TCKTest) constructor.newInstance(parameters);
            } catch (NoSuchMethodException e) {
                try {
                    final Class[] types = {this.getClass(), String[].class, Results.Config.class};
                    final Constructor constructor = testClass.getConstructor(types);
                    final Object[] parameters = {this, parms, results.getConfig()};
                    current = (TCKTest) constructor.newInstance(parameters);
                } catch (NoSuchMethodException f) {
                    final Class[] types = {this.getClass(), Utilities.class, String[].class, Results.Config.class};
                    final Constructor constructor = testClass.getConstructor(types);
                    final Object[] parameters = {this, utilities, parms, results.getConfig()};
                    current = (TCKTest) constructor.newInstance(parameters);
                }
            }

            hasMonitor = !profile.equals(Profile.BROKER);

            if (hasMonitor) {
                monitor.startTest();
            }
            
            LOGGER.info("Test started successfully: " + profile.name().toLowerCase() + " " + test);
            
            // tell the webconsole the test has started
            MQTTLog("Test started successfully: " + profile.name().toLowerCase() + " " + test);
            
        } catch (java.lang.reflect.InvocationTargetException e) {
            LOGGER.error("Error starting test " + profile.name().toLowerCase() + "." + test);
            if (e.getMessage() != null) {
                LOGGER.error(e.getMessage());
            }
            MQTTResults("OVERALL: NOT EXECUTED"); // Ensure the test ends
        } catch (final Exception e) {
            LOGGER.error("Could not find or set test class " + profile.name().toLowerCase() + "." + test, e);
        }
    }

    public void endTest() {
        endTest("");
    }

    public void endTest(String info) {
        if (current != null) {
            LOGGER.info("Test end requested for " + current.getName() + " " + info);
            final TreeMap<String, String> testResults = new TreeMap<>();

            if (!hasMonitor) {
                current.endTest(testResults);
            } else {
                testResults.putAll(monitor.getResults());
                current.endTest(testResults);
                monitor.endTest(null);
            }
            current = null;
        } else {
            LOGGER.info("Test end requested but no test active");
        }
    }

    public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
        if (current != null) {
            current.onMqttConnectionStart(connectionStartInput);
        }
        if (hasMonitor) {
            monitor.onMqttConnectionStart(connectionStartInput);
        }
    }

    public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        if (current != null) {
            current.onAuthenticationSuccessful(authenticationSuccessfulInput);
        }
        if (hasMonitor) {
            monitor.onAuthenticationSuccessful(authenticationSuccessfulInput);
        }
    }

    public void onDisconnect(DisconnectEventInput disconnectEventInput) {
        if (current != null) {
            current.onDisconnect(disconnectEventInput);
        }
        if (hasMonitor) {
            monitor.onDisconnect(disconnectEventInput);
        }
    }

    public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
        if (current != null) {
            current.connect(clientId, packet);
        }
        if (hasMonitor) {
            monitor.connect(clientId, packet);
        }
    }

    public void disconnect(final @NotNull String clientId, final @NotNull DisconnectPacket packet) {
        if (current != null) {
            current.disconnect(clientId, packet);
        }
        if (hasMonitor) {
            monitor.disconnect(clientId, packet);
        }
    }

    public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
        if (current != null) {
            current.subscribe(clientId, packet);
        }
        if (hasMonitor) {
            monitor.subscribe(clientId, packet);
        }
    }

    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        LOGGER.debug("CLIENT_ID={} :: TOPIC: {} :: current={}", clientId, packet.getTopic(),
                current != null ? current.getName() : "null");
        if (current != null) {
            current.publish(clientId, packet);
        }
        if (hasMonitor) {
            monitor.publish(clientId, packet);
        }
    }

    public static class Utilities {
        private final Monitor monitor;
        private final HostApplication hostApps;
        private final EdgeNode edgeNode;

        public Utilities(Monitor monitor, HostApplication hostApps, EdgeNode edgeNode) {
            this.monitor = monitor;
            this.hostApps = hostApps;
            this.edgeNode = edgeNode;
        }

        public Monitor getMonitor() {
            return monitor;
        }

        public HostApplication getHostApps() {
            return hostApps;
        }

        public EdgeNode getEdgeNode() {
            return edgeNode;
        }
    }
}
