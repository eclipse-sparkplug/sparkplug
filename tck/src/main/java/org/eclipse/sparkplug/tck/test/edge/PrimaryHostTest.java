
/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.sparkplug.tck.test.edge;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.Constants.*;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

/**
 * This is the Sparkplug edge node test when dependent on a primary host application
 *
 * @author Ian Craggs
 */
@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class PrimaryHostTest extends TCKTest {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
    public static final String PROPERTY_KEY_QUALITY = "Quality";

	private final @NotNull String testIds[] = { ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_BDSEQ,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_BDSEQ
            };
	
	private final @NotNull TCK theTCK;

    private @NotNull String deviceId;
    private @NotNull String groupId;
    private @NotNull String edgeNodeId;
    private @NotNull String hostApplicationId;
    private @NotNull long seqUnassigned = -1;

	public PrimaryHostTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
		logger.info("Edge Node payload validation test. Parameters: {} ", Arrays.asList(parms));
		theTCK = aTCK;

		if (parms.length < 4) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log("Parameters to edge payload test must be: hostId groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];
		deviceId = parms[3];
		logger.info("Parameters are HostId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId,
				edgeNodeId, deviceId);
	}

    @Override
    public void endTest(Map<String, String> results) {
    	  testResults.putAll(results);	
        Utils.setEndTest(getName(), new ArrayList<String>(Arrays.asList(testIds)), testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "Sparkplug Edge Payload Test";
    }

    public String[] getTestIds() {
        return testIds;
    }

    public Map<String, String> getResults() {
        return testResults;
    }

    @Override
    public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
        // TODO Auto-generated method stub
    }

    @Override
    public void disconnect(String clientId, DisconnectPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
        // TODO Auto-generated method stub
    }

    
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE)
	
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_BDSEQ)
    @Override
    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        final String topic = packet.getTopic();
        logger.info("Edge - Payload validation test - publish - topic: {}", topic);

        boolean isSparkplugTopic = topic.startsWith(TOPIC_ROOT_SP_BV_1_0);
        if (!isSparkplugTopic) {
            logger.error("Skip Edge payload validation - no sparkplug payload.");
            return;
        }
        boolean isDataTopic = isSparkplugTopic
                && (topic.contains(TOPIC_PATH_DDATA) || topic.contains(TOPIC_PATH_NDATA));
        boolean isCommandTopic = isSparkplugTopic
                && (topic.contains(TOPIC_PATH_NCMD) || topic.contains(TOPIC_PATH_DCMD));
    }

}
