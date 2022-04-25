package org.eclipse.sparkplug.tck.test.broker;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.broker.test.BrokerConformanceFeatureTester;
import org.eclipse.sparkplug.tck.test.broker.test.results.ComplianceTestResult;
import org.eclipse.sparkplug.tck.test.broker.test.results.QosTestResult;
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.NOT_YET_IMPLEMENTED;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

public class AwareBrokerTest extends TCKTest {
    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private final @NotNull String[] testId = {
            ID_CONFORMANCE_MQTT_AWARE_BASIC, ID_CONFORMANCE_MQTT_AWARE_STORE,
            ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC, ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN,
            ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC, ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN};
    private HashMap testResults;
    private final @NotNull ArrayList<String> testIds = new ArrayList<>();
    private TCK theTCK = null;
    private static int TIME_OUT = 60;
    private @NotNull String host;
    private @NotNull String port;

    public AwareBrokerTest(TCK aTCK, String[] params) {
        logger.info("Broker: {} Parameters: {} ", getName(), Arrays.asList(params));
        theTCK = aTCK;
        testResults = new HashMap<String, String>();
        testIds.addAll(Arrays.asList(testId));
        if (params.length < 2) {
            logger.error("Parameters must be: host and port, (already set during mqtt connection establishment)");
            return;
        }
        host = params[0];
        port = params[1];
        logger.info("Parameters are Broker host: {}, port: {}, ", host, port);

        Services.extensionExecutorService().schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("Broker - Sparkplug Broker compliant Test - execute test");
                try {
                    checkAware(host, Integer.parseInt(port));
                } finally {
                    theTCK.endTest();
                }
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void endTest(Map<String, String> results) {
        testResults.putAll(results);
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "Sparkplug Broker compliant Test";
    }

    public String[] getTestIds() {
        return testIds.toArray(new String[0]);
    }

    public HashMap<String, String> getResults() {
        return testResults;
    }

    @Override
    public void connect(String clientId, ConnectPacket packet) {
        logger.info("Broker - {} - connect - clientId: {}", getName(), clientId);
    }

    @Override
    public void disconnect(String clientId, DisconnectPacket packet) {
        logger.info("Broker - {} - disconnect - clientId: {}", getName(), clientId);
    }

    @Override
    public void subscribe(String clientId, SubscribePacket packet) {
        logger.info("Broker - {} - subscribe - " +
                "clientId: {} topic: {}", getName(), clientId, packet.getSubscriptions().get(0));
    }


    @Override
    public void publish(String clientId, PublishPacket packet) {
        final String topic = packet.getTopic();
        logger.info("Broker - {} - publish - topic: {}", getName(), topic);
    }


    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_BASIC)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_STORE)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN)
    @SpecAssertion(
            section = Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER,
            id = ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP)

    public void checkAware(final String host, final int port) {
        logger.info("{} - Start", Sections.CONFORMANCE_SPARKPLUG_AWARE_MQTT_SERVER);

        logger.debug("Check Req: {} ", CONFORMANCE_MQTT_AWARE_BASIC);

        CompliantBrokerTest.checkCompliance(host, port, testResults);
        boolean isBasicAware = testResults.get(ID_CONFORMANCE_MQTT_QOS0) == TopicConstants.PASS
                && testResults.get(ID_CONFORMANCE_MQTT_QOS1) == TopicConstants.PASS
                && testResults.get(ID_CONFORMANCE_MQTT_WILL_MESSAGES) == TopicConstants.PASS
                && testResults.get(ID_CONFORMANCE_MQTT_RETAINED) == TopicConstants.PASS;
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_BASIC, setResult(isBasicAware, CONFORMANCE_MQTT_AWARE_BASIC));

        BrokerConformanceFeatureTester brokerConformanceFeatureTester =
                new BrokerConformanceFeatureTester(host, port, null, null, null, TIME_OUT);


        logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_STORE);
        //A Sparkplug Aware MQTT Server MUST store NBIRTH and DBIRTH messages as they pass through the MQTT Server
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_STORE, NOT_YET_IMPLEMENTED);

        logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC);
        //A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on a topic of the form:
        // $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC, NOT_YET_IMPLEMENTED);

        logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN);
        //A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on the topic:
        // $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id
        // with the MQTT retain flag set to true
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN, NOT_YET_IMPLEMENTED);

        logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC);
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC, NOT_YET_IMPLEMENTED);

        logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN);
        testResults.put(ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN, NOT_YET_IMPLEMENTED);
    }

}
