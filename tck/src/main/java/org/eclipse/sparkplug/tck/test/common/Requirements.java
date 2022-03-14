
/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar, Ian Craggs
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Anja Helmbrecht-Schaar - initial implementation and documentation
 */
package org.eclipse.sparkplug.tck.test.common;

/**
 * Class that contains all Requirement IDs and Description, that have to check.
 */
public class Requirements {

    // @SpecAssertions works only with constants like string but not enum or arrays

    // 1 Introduction
    // 1.1 Rationale and Use Case
    // 1.1.1 Define an MQTT Topic Namespace
    // 1.1.2 Define MQTT State Management
    // 1.1.3 Define the MQTT Payload
    // 1.1.4 Background
    // 1.2 Intellectual Property Rights
    // 1.2.1 Eclipse Foundation Specification License
    // 1.2.2 Disclaimers
    // 1.3 Organization of the Sparkplug Specification
    // 1.4 Terminology
    // 1.4.1 Infrastructure Components
    // 1.4.1.1 MQTT Server(s)
    // 1.4.1.2 Sparkplug Group
    // 1.4.1.3 Sparkplug Edge Node
    // 1.4.1.4 Sparkplug Device
    // 1.4.1.5 MQTT/Sparkplug Enabled Device
    // 1.4.1.6 Host Applications
    public final static String ID_INTRO_SPARKPLUG_HOST_STATE = "intro-sparkplug-host-state";
    public final static String INTRO_SPARKPLUG_HOST_STATE = "Sparkplug Host Applications MUST publish STATE messages denoting their online and offline status.";

    // 1.4.1.7 Primary Host Application
    // 1.4.1.8 Sparkplug Identifiers
    public final static String ID_INTRO_GROUP_ID_STRING = "intro-group-id-string";
    public final static String INTRO_GROUP_ID_STRING = "The Group ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_GROUP_ID_CHARS = "intro-group-id-chars";
    public final static String INTRO_GROUP_ID_CHARS = "Because the Group ID is used in MQTT topic strings the Group ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    public final static String ID_INTRO_EDGE_NODE_ID_STRING = "intro-edge-node-id-string";
    public final static String INTRO_EDGE_NODE_ID_STRING = "The Edge Node ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_EDGE_NODE_ID_CHARS = "intro-edge-node-id-chars";
    public final static String INTRO_EDGE_NODE_ID_CHARS = "Because the Edge Node ID is used in MQTT topic strings the Edge Node ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    public final static String ID_INTRO_DEVICE_ID_STRING = "intro-device-id-string";
    public final static String INTRO_DEVICE_ID_STRING = "The Device ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_DEVICE_ID_CHARS = "intro-device-id-chars";
    public final static String INTRO_DEVICE_ID_CHARS = "Because the Device ID is used in MQTT topic strings the Device ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    // 1.4.1.9 Sparkplug Metric
    // 1.5 Normative References
    // 1.6 Security
    // 1.6.1 Authentication
    // 1.6.2 Authorization
    // 1.6.3 Encryption
    // 1.7 Editing Convention
    // 1.8 Leveraging Standards and Open Source
    // 2 Principles
    // 2.1 Pub/Sub
    // 2.2 Report by Exception
    public final static String ID_PRINCIPLES_RBE_RECOMMENDED = "principles-rbe-recommended";
    public final static String PRINCIPLES_RBE_RECOMMENDED = "Because of the stateful nature of Sparkplug sessions, data SHOULD NOT be published from Edge Nodes on a periodic basis and instead SHOULD be published using a RBE based approach.";

    // 2.3 Continuous Session Awareness
    // 2.4 Birth and Death Certificates
    public final static String ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "principles-birth-certificates-order";
    public final static String PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "Birth Certificates MUST be the first MQTT messages published by any Edge Node or any Host Application.";

    // 2.5 Persistent vs Non-Persistent Connections for Edge Nodes
    public final static String ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION = "principles-persistence-clean-session";
    public final static String PRINCIPLES_PERSISTENCE_CLEAN_SESSION = "Edge Node MQTT CONNECT packets MUST set the 'Clean Session' flag to true.";

    // 3 Sparkplug Architecture and Infrastructure Components
    // 3.1 MQTT Server(s)
    // 3.2 MQTT Edge Node
    // 3.3 Device / Sensor
    // 3.4 MQTT Enabled Device (Sparkplug)
    // 3.5 Primary Host Application
    // 3.6 Sparkplug Host Application
    public final static String ID_COMPONENTS_PH_STATE = "components-ph-state";
    public final static String COMPONENTS_PH_STATE = "A Sparkplug Host Application MUST utilize the STATE messages to denote whether it is online or offline at any given point in time.";

    // 4 Topics and Messages
    // 4.1 Topic Namespace Elements
    public final static String ID_TOPIC_STRUCTURE = "topic-structure";
    public final static String TOPIC_STRUCTURE = "All MQTT clients using the Sparkplug specification MUST use the following topic namespace structure";

    // 4.1.1 namespace Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_A = "topic-structure-namespace-a";
    public final static String TOPIC_STRUCTURE_NAMESPACE_A = "For the Sparkplug B version of the payload definition, the UTF-8 string constant for the namespace element MUST be";

    // 4.1.2 group_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID = "topic-structure-namespace-valid-group-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID = "The format of the Group ID MUST be a valid UTF-8 string with the exception of the reserved characters of + (plus), / (forward slash), and # (number sign).";

    // 4.1.3 message_type Element
    // 4.1.4 edge_node_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR = "topic-structure-namespace-unique-edge-node-descriptor";
    public final static String TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR = "The group_id combined with the edge_node_id element MUST be unique from any other group_id/edge_node_id assigned in the MQTT infrastructure.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID = "topic-structure-namespace-valid-edge-node-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID = "The format of the edge_node_id MUST be a valid UTF-8 string with the exception of the reserved characters of + (plus), / (forward slash), and # (number sign).";

    // 4.1.5 device_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID = "topic-structure-namespace-valid-device-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID = "The format of the device_id MUST be a valid UTF-8 string except for the reserved characters of + (plus), / (forward slash), and # (number sign).";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID = "topic-structure-namespace-unique-device-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID = "The device_id MUST be unique from other devices being reported on by the same Edge Node.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE = "topic-structure-namespace-duplicate-device-id-across-edge-node";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE = "The device_id MAY be duplicated from Edge Node to other Edge Nodes.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES = "topic-structure-namespace-device-id-associated-message-types";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES = "The device_id MUST be included with message_type elements DBIRTH, DDEATH, DDATA, and DCMD based topics.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES = "topic-structure-namespace-device-id-non-associated-message-types";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES = "The device_id MUST NOT be included with message_type elements NBIRTH, NDEATH, NDATA, NCMD, and STATE based topics";

    // 4.2 Message Types and Contents
    // 4.2.1 Edge Node
    // 4.2.1.1 Birth Message (NBIRTH)
    // 4.2.1.1.1 Topic (NBIRTH)
    // 4.2.1.1.2 Payload (NBIRTH)
    public final static String ID_TOPICS_NBIRTH_MQTT = "topics-nbirth-mqtt";
    public final static String TOPICS_NBIRTH_MQTT = "NBIRTH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NBIRTH_SEQ_NUM = "topics-nbirth-seq-num";
    public final static String TOPICS_NBIRTH_SEQ_NUM = "The NBIRTH MUST include a sequence number in the payload and it MUST have a value of 0.";

    public final static String ID_TOPICS_NBIRTH_TIMESTAMP = "topics-nbirth-timestamp";
    public final static String TOPICS_NBIRTH_TIMESTAMP = "The NBIRTH MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_NBIRTH_METRIC_REQS = "topics-nbirth-metric-reqs";
    public final static String TOPICS_NBIRTH_METRIC_REQS = "The NBIRTH MUST include every metric the Edge Node will ever report on.";

    public final static String ID_TOPICS_NBIRTH_METRICS = "topics-nbirth-metrics";
    public final static String TOPICS_NBIRTH_METRICS = "At a minimum each metric MUST include the following";

    public final static String ID_TOPICS_NBIRTH_TEMPLATES = "topics-nbirth-templates";
    public final static String TOPICS_NBIRTH_TEMPLATES = "If Template instances will be published by this Edge Node or any devices, all Template definitions MUST be published in the NBIRTH.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_INCLUDED = "topics-nbirth-bdseq-included";
    public final static String TOPICS_NBIRTH_BDSEQ_INCLUDED = "A bdSeq number as a metric MUST be included in the payload.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_MATCHING = "topics-nbirth-bdseq-matching";
    public final static String TOPICS_NBIRTH_BDSEQ_MATCHING = "This MUST match the bdSeq number provided in the MQTT CONNECT packet's Will Message payload.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_INCREMENT = "topics-nbirth-bdseq-increment";
    public final static String TOPICS_NBIRTH_BDSEQ_INCREMENT = "The bdSeq number MUST start at zero and increment by one on every new MQTT CONNECT packet.";

    public final static String ID_TOPICS_NBIRTH_REBIRTH_METRIC = "topics-nbirth-rebirth-metric";
    public final static String TOPICS_NBIRTH_REBIRTH_METRIC = "The NBIRTH message MUST include the following metric";

    // 4.2.1.2 Data Message (NDATA)
    // 4.2.1.2.1 Topic (NDATA)
    // 4.2.1.2.2 Payload (NDATA)
    public final static String ID_TOPICS_NDATA_MQTT = "topics-ndata-mqtt";
    public final static String TOPICS_NDATA_MQTT = "NDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NDATA_SEQ_NUM = "topics-ndata-seq-num";
    public final static String TOPICS_NDATA_SEQ_NUM = "The NDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_NDATA_TIMESTAMP = "topics-ndata-timestamp";
    public final static String TOPICS_NDATA_TIMESTAMP = "The NDATA MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_NDATA_PAYLOAD = "topics-ndata-payload";
    public final static String TOPICS_NDATA_PAYLOAD = "The NDATA MUST include the Edge Node's metrics that have changed since the last NBIRTH or NDATA message.";

    // 4.2.1.3 Death Message (NDEATH)
    // 4.2.1.3.1 Topic (NDEATH)
    // 4.2.1.3.2 Payload (NDEATH)
    public final static String ID_TOPICS_NDEATH_PAYLOAD = "topics-ndeath-payload";
    public final static String TOPICS_NDEATH_PAYLOAD = "The NDEATH message contains a very simple payload that MUST only include a single metric, the bdSeq number, so that the NDEATH event can be associated with the NBIRTH.";

    public final static String ID_TOPICS_NDEATH_SEQ = "topics-ndeath-seq";
    public final static String TOPICS_NDEATH_SEQ = "The NDEATH message MUST NOT include a sequence number.";

    // 4.2.1.4 Command (NCMD)
    // 4.2.1.4.1 Topic (NCMD)
    // 4.2.1.4.2 Payload (NCMD)
    public final static String ID_TOPICS_NCMD_MQTT = "topics-ncmd-mqtt";
    public final static String TOPICS_NCMD_MQTT = "NCMD messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NCMD_TIMESTAMP = "topics-ncmd-timestamp";
    public final static String TOPICS_NCMD_TIMESTAMP = "The NCMD MUST include a timestamp denoting the Date/Time the message was sent from the Host Application's MQTT client.";

    public final static String ID_TOPICS_NCMD_PAYLOAD = "topics-ncmd-payload";
    public final static String TOPICS_NCMD_PAYLOAD = "The NCMD MUST include the metrics that need to be written to on the Edge Node.";

    // 4.2.2 Device / Sensor
    // 4.2.2.1 Birth Message (DBIRTH)
    // 4.2.2.1.1 Topic (DBIRTH)
    // 4.2.2.1.2 Payload (DBIRTH)
    public final static String ID_TOPICS_DBIRTH_MQTT = "topics-dbirth-mqtt";
    public final static String TOPICS_DBIRTH_MQTT = "DBIRTH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DBIRTH_SEQ = "topics-dbirth-seq";
    public final static String TOPICS_DBIRTH_SEQ = "The DBIRTH MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_DBIRTH_TIMESTAMP = "topics-dbirth-timestamp";
    public final static String TOPICS_DBIRTH_TIMESTAMP = "The DBIRTH MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_DBIRTH_METRIC_REQS = "topics-dbirth-metric-reqs";
    public final static String TOPICS_DBIRTH_METRIC_REQS = "The DBIRTH MUST include every metric the Edge Node will ever report on.";

    public final static String ID_TOPICS_DBIRTH_METRICS = "topics-dbirth-metrics";
    public final static String TOPICS_DBIRTH_METRICS = "At a minimum each metric MUST include the following";

    // 4.2.2.2 Data Message (DDATA)
    // 4.2.2.2.1 Topic (DDATA)
    // 4.2.2.2.2 Payload (DDATA)
    public final static String ID_TOPICS_DDATA_MQTT = "topics-ddata-mqtt";
    public final static String TOPICS_DDATA_MQTT = "DDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DDATA_SEQ_NUM = "topics-ddata-seq-num";
    public final static String TOPICS_DDATA_SEQ_NUM = "The DDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_DDATA_TIMESTAMP = "topics-ddata-timestamp";
    public final static String TOPICS_DDATA_TIMESTAMP = "The DDATA MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_DDATA_PAYLOAD = "topics-ddata-payload";
    public final static String TOPICS_DDATA_PAYLOAD = "The DDATA MUST include the Device's metrics that have changed since the last DBIRTH or DDATA message.";

    // 4.2.2.3 Death Message (DDEATH)
    // 4.2.2.3.1 Topic (DDEATH)
    // 4.2.2.3.2 Payload (DDEATH)
    public final static String ID_TOPICS_DDEATH_MQTT = "topics-ddeath-mqtt";
    public final static String TOPICS_DDEATH_MQTT = "DDEATH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DDEATH_SEQ_NUM = "topics-ddeath-seq-num";
    public final static String TOPICS_DDEATH_SEQ_NUM = "The DDEATH MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    // 4.2.2.4 Command (DCMD)
    // 4.2.2.4.1 Topic DCMD)
    // 4.2.2.4.2 Payload (DCMD)
    public final static String ID_TOPICS_DCMD_MQTT = "topics-dcmd-mqtt";
    public final static String TOPICS_DCMD_MQTT = "DCMD messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DCMD_TIMESTAMP = "topics-dcmd-timestamp";
    public final static String TOPICS_DCMD_TIMESTAMP = "The DCMD MUST include a timestamp denoting the Date/Time the message was sent from the Host Application's MQTT client.";

    public final static String ID_TOPICS_DCMD_PAYLOAD = "topics-dcmd-payload";
    public final static String TOPICS_DCMD_PAYLOAD = "The DCMD MUST include the metrics that need to be written to on the Device.";

    // 4.2.3 Sparkplug Host Application
    // 4.2.3.1 Birth Certificate Message (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_MESSAGE = "host-topic-phid-birth-message";
    public final static String HOST_TOPIC_PHID_BIRTH_MESSAGE = "The first MQTT message a Host Application MUST publish is a Birth Certificate.";

    // 4.2.3.1.1 Birth Certificate Topic (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_TOPIC = "host-topic-phid-birth-topic";
    public final static String HOST_TOPIC_PHID_BIRTH_TOPIC = "STATE/sparkplug_host_application_id";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD = "host-topic-phid-birth-payload";
    public final static String HOST_TOPIC_PHID_BIRTH_PAYLOAD = "The Birth Certificate Payload MUST be the UTF-8 string “ONLINE”";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_QOS = "host-topic-phid-birth-qos";
    public final static String HOST_TOPIC_PHID_BIRTH_QOS = "The MQTT Quality of Service (QoS) MUST be set to 1";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_RETAIN = "host-topic-phid-birth-retain";
    public final static String HOST_TOPIC_PHID_BIRTH_RETAIN = "The MQTT retain flag for the Birth Certificate MUST be set to TRUE";

    // 4.2.3.1.2 Birth Certificate Payload (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF = "host-topic-phid-birth-payload-on-off";
    public final static String HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF = "The STATE message from the Sparkplug Host Application Birth Certificate message MUST include a payload that is a UTF-8 string that is the following";

    // 4.2.3.2 Death Certificate Message (STATE)
    // 4.2.3.2.1 Death Certificate Topic (STATE)
    public final static String ID_HOST_TOPIC_PHID_DEATH_TOPIC = "host-topic-phid-death-topic";
    public final static String HOST_TOPIC_PHID_DEATH_TOPIC = "STATE/sparkplug_host_application_id";

    public final static String ID_HOST_TOPIC_PHID_REQUIRED = "host-topic-phid-required";
    public final static String HOST_TOPIC_PHID_REQUIRED = "The Sparkplug Host Application MUST provide a Will message in the MQTT CONNECT packet";

    public final static String ID_HOST_TOPIC_PHID_DEATH_PAYLOAD = "host-topic-phid-death-payload";
    public final static String HOST_TOPIC_PHID_DEATH_PAYLOAD = "The MQTT Will Payload MUST be the UTF-8 string “OFFLINE”";

    public final static String ID_HOST_TOPIC_PHID_DEATH_QOS = "host-topic-phid-death-qos";
    public final static String HOST_TOPIC_PHID_DEATH_QOS = "The MQTT Will QoS MUST be set to 1";

    public final static String ID_HOST_TOPIC_PHID_DEATH_RETAIN = "host-topic-phid-death-retain";
    public final static String HOST_TOPIC_PHID_DEATH_RETAIN = "The MQTT Will retain flag MUST be set to TRUE";

    // 4.2.3.2.2 Death Certificate Payload (STATE)
    public final static String ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF = "host-topic-phid-death-payload-off";
    public final static String HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF = "The STATE messages from the Sparkplug Host Application Death Certificate message MUST include a payload that is a UTF-8 string that is the following";

    // 5 Operational Behavior
    // 5.1 Host Application Session Establishment
    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION = "message-flow-phid-sparkplug-clean-session";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION = "The CONNECT Control Packet for all Sparkplug Host Applications MUST set the MQTT 'Clean Session' flag to true.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION = "message-flow-phid-sparkplug-subscription";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION = "The subscription on the Sparkplug Topic Namespace and the STATE topic MUST be done immediately after successfully establishing the MQTT session and before publishing its own STATE message.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH = "message-flow-phid-sparkplug-state-publish";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH = "Once an MQTT Session has been established, the Sparkplug Host Application subscriptions on the Sparkplug Topic Namespace have been established, and the STATE topic subscription has been been established, the Sparkplug Host Application MUST publish a new STATE message with a Payload of a UTF-8 string of 'ONLINE'.";

    public final static String ID_D = "d";
    public final static String D = "All metric data associated with any Sparkplug Edge Node that was connected to that MQTT Server and known by the Host Application MUST be updated to a STALE data quality if the Host Application loses connection to the MQTT Server.";

    // 5.2 Edge Node Session Establishment
    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "message-flow-edge-node-birth-publish-connect";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "Any Edge Node in the MQTT infrastructure MUST establish an MQTT Session prior to publishing NBIRTH and DBIRTH messages.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_SUBSCRIBE = "message-flow-edge-node-birth-publish-subscribe";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_SUBSCRIBE = "Any Edge Node in the MQTT infrastructure MUST verify the Primary Host Application is ONLINE via the STATE topic if a Primary Host Application is configured for the Edge Node before publishing NBIRTH and DBIRTH messages.";

    // 5.3 Edge Node Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH = "operational-behavior-edge-node-intentional-disconnect-ndeath";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH = "An Edge Node MUST publish an NDEATH before terminating the connection.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET = "operational-behavior-edge-node-intentional-disconnect-packet";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET = "Immediately following the NDEATH publish, a DISCONNECT packet MUST be sent to the MQTT Server.";

    // 5.4 Device Session Establishment
    // 5.5 Device Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH = "operational-behavior-device-ddeath";
    public final static String OPERATIONAL_BEHAVIOR_DEVICE_DDEATH = "If a Sparkplug Edge Node loses connection with an attached Sparkplug Device, it MUST publish a DDEATH message on behalf of the device.";

    // 5.6 Sparkplug Host Applications
    // 5.7 Sparkplug Host Application Message Ordering
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM = "operational-behavior-host-reordering-param";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM = "Sparkplug Host Applications SHOULD provide a configurable 'Reorder Timeout' parameter";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START = "operational-behavior-host-reordering-start";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_START = "If a message arrives with an out of order sequence number, the Host Application SHOULD start a timer denoting the start of the Reorder Timeout window";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH = "operational-behavior-host-reordering-rebirth";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH = "If the Reorder Timeout elapses and the missing message(s) have not been received, the Sparkplug Host Application SHOULD send an NCMD to the Edge Node with a 'Node Control/Rebirth' request";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS = "operational-behavior-host-reordering-success";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS = "If the missing messages that triggered the start of the Reorder Timeout timer arrive before the reordering timer elapses, the timer can be terminated and normal operation in the Host Application can continue";

    // 5.8 Primary Host Application STATE in Multiple MQTT Server Topologies
    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE = "operational-behavior-primary-application-state-with-multiple-servers-state";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE = "Regardless of the number of MQTT Servers in a Sparkplug Infrastructure, every time a Primary Host Application establishes a new MQTT Session with an MQTT Server, the STATE Birth Certificate defined in the STATE description section MUST be the first message that is published after a successful MQTT Session is established with each MQTT Server.";

    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER = "operational-behavior-primary-application-state-with-multiple-servers-single-server";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER = "The Edge Nodes MUST not connected to more than one server at any point in time.";

    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK = "operational-behavior-primary-application-state-with-multiple-servers-walk";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK = "If the Primary Host Application is OFFLINE as denoted via the STATE MQTT Message, the Edge Node MUST terminate its session with this MQTT Server and move to the next available MQTT Server that is available.";

    // 5.9 Edge Node NDATA and NCMD Messages
    // 5.10 MQTT Enabled Device Session Establishment
    // 5.11 Sparkplug Host Application Session Establishment
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID = "operational-behavior-host-application-host-id";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID = "The host_id MUST be unique to all other Sparkplug Host IDs in the infrastructure.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL = "operational-behavior-host-application-connect-will";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL = "When a Sparkplug Host Application sends its MQTT CONNECT packet, it MUST include a Will Message.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC = "operational-behavior-host-application-connect-will-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC = "The MQTT Will Message's topic MUST be of the form 'STATE/host_id' where host_id is the unique identifier of the Sparkplug Host Application";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD = "operational-behavior-host-application-connect-will-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD = "The MQTT Will Message's payload MUST be the UTF-8 String of 'OFFLINE'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS = "operational-behavior-host-application-connect-will-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS = "The MQTT Will Message's MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED = "operational-behavior-host-application-connect-will-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED = "The MQTT Will Message's retained flag MUST be set to true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH = "operational-behavior-host-application-connect-birth";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH = "The MQTT Client associated with the Sparkplug Host Application MUST send a birth message immediately after successfully connecting to the MQTT Server.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC = "operational-behavior-host-application-connect-birth-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC = "The Host Application's Birth topic MUST be of the form 'STATE/host_id' where host_id is the unique identifier of the Sparkplug Host Application";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD = "operational-behavior-host-application-connect-birth-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD = "The Host Application's Birth payload MUST be the UTF-8 String of 'ONLINE'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS = "operational-behavior-host-application-connect-birth-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS = "The Host Application's Birth MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED = "operational-behavior-host-application-connect-birth-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED = "The Host Application's Birth retained flag MUST be set to true.";

    // 5.12 Sparkplug Host Application Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC = "operational-behavior-host-application-death-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC = "The Sparkplug Host Application's Death topic MUST be of the form 'STATE/host_id' where host_id is the unique identifier of the Sparkplug Host Application.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD = "operational-behavior-host-application-death-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD = "The Sparkplug Host Application's Death payload MUST be the UTF-8 String of 'OFFLINE'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS = "operational-behavior-host-application-death-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS = "The Sparkplug Host Application's Death MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED = "operational-behavior-host-application-death-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED = "The Sparkplug Host Application's Death retained flag MUST be set to true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL = "operational-behavior-host-application-disconnect-intentional";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL = "In the case of intentionally disconnecting, an MQTT DISCONNECT packet MUST be sent immediately after the Death message is sent.";

    // 5.13 Data Publish
    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH = "operational-behavior-data-publish-nbirth";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH = "NBIRTH messages MUST include all metrics for the specified Edge Node that will ever be published for that Edge Node within the established Sparkplug session.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "operational-behavior-data-publish-nbirth-values";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "NBIRTH messages MUST include current values for all metrics.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE = "operational-behavior-data-publish-nbirth-change";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE = "NDATA messages MUST only be published when Edge Node level metrics change.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER = "operational-behavior-data-publish-nbirth-order";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER = "For all metrics where is_historical=false, NBIRTH and NDATA messages MUST keep metric values in chronological order in the list of metrics in the payload.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH = "operational-behavior-data-publish-dbirth";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH = "DBIRTH messages MUST include all metrics for the specified Device that will ever be published for that Device within the established Sparkplug session.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "operational-behavior-data-publish-dbirth-values";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "DBIRTH messages MUST include current values for all metrics.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE = "operational-behavior-data-publish-dbirth-change";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE = "DDATA messages MUST only be published when Device level metrics change.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER = "operational-behavior-data-publish-dbirth-order";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER = "For all metrics where is_historical=false, DBIRTH and DDATA messages MUST keep metric values in chronological order in the list of metrics in the payload.";

    // 5.14 Commands
    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "operational-behavior-data-commands-rebirth-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "An NBIRTH message MUST include a metric with a name of 'Node Control/Rebirth'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "operational-behavior-data-commands-rebirth-datatype";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "The 'Node Control/Rebirth' metric in the NBIRTH message MUST have a datatype of 'Boolean'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "operational-behavior-data-commands-rebirth-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "The 'Node Control/Rebirth' metric value in the NBIRTH message MUST have a value of false.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB = "operational-behavior-data-commands-ncmd-rebirth-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB = "A Rebirth Request MUST use the NCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME = "operational-behavior-data-commands-ncmd-rebirth-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME = "A Rebirth Request MUST include a metric with a name of 'Node Control/Rebirth'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE = "operational-behavior-data-commands-ncmd-rebirth-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE = "A Rebirth Request MUST include a metric value of true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "operational-behavior-data-commands-rebirth-action-1";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "When an Edge Node receives a Rebirth Request, it MUST immediately stop sending DATA messages.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "operational-behavior-data-commands-rebirth-action-2";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "After an Edge Node stops sending DATA messages, it MUST send a complete BIRTH sequence including the NBIRTH and DBIRTH(s) if applicable.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "operational-behavior-data-commands-rebirth-action-3";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "The NBIRTH MUST include the same bdSeq metric with the same value it had included in the Will Message of the previous MQTT CONNECT packet.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB = "operational-behavior-data-commands-ncmd-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB = "An Edge Node level command MUST use the NCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME = "operational-behavior-data-commands-ncmd-metric-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME = "An NCMD message MUST include a metric name that was included in the associated NBIRTH message for the Edge Node.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE = "operational-behavior-data-commands-ncmd-metric-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE = "An NCMD message MUST include a compatible metric value for the metric name that it is writing to.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB = "operational-behavior-data-commands-dcmd-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB = "A Device level command MUST use the DCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_NAME = "operational-behavior-data-commands-dcmd-metric-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_NAME = "A DCMD message MUST include a metric name that was included in the associated DBIRTH message for the Device.";

    // 6 Payloads
    // 6.1 Overview
    // 6.2 Google Protocol Buffers
    // 6.3 Sparkplug A MQTT Payload Definition
    // 6.4 Sparkplug B MQTT Payload Definition
    // 6.4.1 Google Protocol Buffer Schema
    // 6.4.2 Payload Metric Naming Convention
    // 6.4.3 Sparkplug B v1.0 Payload Components
    // 6.4.4 Payload Component Definitions
    // 6.4.5 Payload
    public final static String ID_PAYLOADS_TIMESTAMP_IN_UTC = "payloads_timestamp_in_UTC";
    public final static String PAYLOADS_TIMESTAMP_IN_UTC = "This timestamp MUST be in UTC.";

    public final static String ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED = "payloads-sequence-num-always-included";
    public final static String PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED = "A sequence number MUST be included in the payload of every Sparkplug MQTT message except NDEATH messages.";

    public final static String ID_PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH = "payloads-sequence-num-zero-nbirth";
    public final static String PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH = "A NBIRTH message MUST always contain a sequence number of zero.";

    public final static String ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING = "payloads-sequence-num-incrementing";
    public final static String PAYLOADS_SEQUENCE_NUM_INCREMENTING = "All subsequent messages MUST contain a sequence number that is continually increasing by one in each message until a value of 255 is reached. At that point, the sequence number of the following message MUST be zero.";

    // 6.4.6 Metric
    public final static String ID_PAYLOADS_NAME_REQUIREMENT = "payloads-name-requirement";
    public final static String PAYLOADS_NAME_REQUIREMENT = "The name MUST be included with every metric unless aliases are being used.";

    public final static String ID_PAYLOADS_ALIAS_UNIQUENESS = "payloads-alias-uniqueness";
    public final static String PAYLOADS_ALIAS_UNIQUENESS = "If supplied in an NBIRTH or DBIRTH it MUST be a unique number across this Edge Node's entire set of metrics.";

    public final static String ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "payloads-alias-birth-requirement";
    public final static String PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "NBIRTH and DBIRTH messages MUST include both a metric name and alias.";

    public final static String ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "payloads-alias-data-cmd-requirement";
    public final static String PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "NDATA, DDATA, NCMD, and DCMD messages MUST only include an alias and the metric name MUST be excluded.";

    public final static String ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "payloads-name-birth-data-requirement";
    public final static String PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.";

    public final static String ID_PAYLOADS_NAME_CMD_REQUIREMENT = "payloads-name-cmd-requirement";
    public final static String PAYLOADS_NAME_CMD_REQUIREMENT = "The timestamp MAY be included with metrics in NCMD and DCMD messages.";

    public final static String ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC = "payloads_metric_timestamp_in_UTC";
    public final static String PAYLOADS_METRIC_TIMESTAMP_IN_UTC = "The timestamp MUST be in UTC.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "payloads-metric-datatype-value-type";
    public final static String PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "The datatype MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_VALUE = "payloads-metric-datatype-value";
    public final static String PAYLOADS_METRIC_DATATYPE_VALUE = "The datatype MUST be one of the enumerated values as shown in the valid Sparkplug Data Types.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_REQ = "payloads-metric-datatype-req";
    public final static String PAYLOADS_METRIC_DATATYPE_REQ = "The datatype MUST be included with each metric definition in NBIRTH and DBIRTH messages.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ = "payloads-metric-datatype-not-req";
    public final static String PAYLOADS_METRIC_DATATYPE_NOT_REQ = "The datatype SHOULD NOT be included with metric definitions in NDATA, NCMD, DDATA, and DCMD messages.";

    // 6.4.7 MetaData
    // 6.4.8 PropertySet
    public final static String ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "payloads-propertyset-keys-array-size";
    public final static String PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "The array of keys in a PropertySet MUST contain the same number of values included in the array of PropertyValue objects.";

    public final static String ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "payloads-propertyset-values-array-size";
    public final static String PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "The array of values in a PropertySet MUST contain the same number of items that are in the keys array.";

    // 6.4.9 PropertyValue
    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "payloads-metric-propertyvalue-type-type";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "This MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "payloads-metric-propertyvalue-type-value";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types or the Sparkplug Property Value Data Types.";

    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "payloads-metric-propertyvalue-type-req";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "This MUST be included in Property Value Definitions in NBIRTH and DBIRTH messages.";

    // 6.4.9.1 Quality Codes
    public final static String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "payloads-propertyset-quality-value-type";
    public final static String PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "The 'type' of the Property Value MUST be a value of 3 which represents a Signed 32-bit Integer.";

    public final static String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "payloads-propertyset-quality-value-value";
    public final static String PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "The 'value' of the Property Value MUST be an int_value and be one of the valid quality codes of 0, 192, or 500.";

    // 6.4.10 PropertySetList
    // 6.4.11 DataSet
    public final static String ID_PAYLOADS_DATASET_COLUMN_SIZE = "payloads-dataset-column-size";
    public final static String PAYLOADS_DATASET_COLUMN_SIZE = "This MUST be an unsigned 64-bit integer representing the number of columns in this DataSet.";

    public final static String ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS = "payloads-dataset-column-num-headers";
    public final static String PAYLOADS_DATASET_COLUMN_NUM_HEADERS = "The size of the array MUST have the same number of elements that the types array contains.";

    public final static String ID_PAYLOADS_DATASET_TYPES_DEF = "payloads-dataset-types-def";
    public final static String PAYLOADS_DATASET_TYPES_DEF = "This MUST be an array of unsigned 32 bit integers representing the datatypes of the columns.";

    public final static String ID_PAYLOADS_DATASET_TYPES_NUM = "payloads-dataset-types-num";
    public final static String PAYLOADS_DATASET_TYPES_NUM = "The array of types MUST have the same number of elements that the columns array contains.";

    public final static String ID_PAYLOADS_DATASET_TYPES_TYPE = "payloads-dataset-types-type";
    public final static String PAYLOADS_DATASET_TYPES_TYPE = "The values in the types array MUST be a unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_DATASET_TYPES_VALUE = "payloads-dataset-types-value";
    public final static String PAYLOADS_DATASET_TYPES_VALUE = "This values in the types array MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ = "payloads-template-parameter-type-req";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ = "This MUST be included in DataSet Definitions in NBIRTH and DBIRTH messages.";

    // 6.4.12 DataSet.Row
    // 6.4.13 DataSet.DataSetValue
    public final static String ID_PAYLOADS_TEMPLATE_DATASET_VALUE = "payloads-template-dataset-value";
    public final static String PAYLOADS_TEMPLATE_DATASET_VALUE = "The value supplied MUST be one of the following types: uint32, uint64, float, double, bool, or string.";

    // 6.4.14 Template
    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION = "payloads-template-definition-is-definition";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION = "A Template Definition MUST have is_definition set to true.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_REF = "payloads-template-definition-ref";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_REF = "A Template Definition MUST omit the template_ref field.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION = "payloads-template-instance-is-definition";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION = "A Template Instance MUST have is_definition set to false.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_REF = "payloads-template-instance-ref";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_REF = "A Template Instance MUST have template_ref set to the type of template definition it is.";

    public final static String ID_PAYLOADS_TEMPLATE_VERSION = "payloads-template-version";
    public final static String PAYLOADS_TEMPLATE_VERSION = "If included, the version MUST be a UTF-8 string representing the version of the Template.";

    public final static String ID_PAYLOADS_TEMPLATE_REF_DEFINITION = "payloads-template-ref-definition";
    public final static String PAYLOADS_TEMPLATE_REF_DEFINITION = "This MUST be omitted if this is a Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_REF_INSTANCE = "payloads-template-ref-instance";
    public final static String PAYLOADS_TEMPLATE_REF_INSTANCE = "This MUST be a UTF-8 string representing a reference to a Template Definition name if this is a Template Instance.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION = "payloads-template-is-definition";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION = "This MUST be included in every Template Definition and Template Instance.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION = "payloads-template-is-definition-definition";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION = "This MUST be set to true if this is a Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE = "payloads-template-is-definition-instance";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE = "This MUST be set to false if this is a Template Instance.";

    // 6.4.15 Template.Parameter
    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED = "payloads-template-parameter-name-required";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED = "This MUST be included in every Template Parameter definition.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE = "payloads-template-parameter-name-type";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE = "This MUST be a UTF-8 string representing the name of the Template parameter.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE = "payloads-template-parameter-value-type";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE = "This MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE = "payloads-template-parameter-type-value";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE = "This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE = "payloads-template-parameter-value";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_VALUE = "The value supplied MUST be one of the following types: uint32, uint64, float, double, bool, or string.";

    // 6.4.16 Data Types
    // 6.4.17 Datatype Details
    // 6.4.18 Payload Representation on Host Applications
    // 6.4.19 NBIRTH
    public final static String ID_PAYLOADS_NBIRTH_TIMESTAMP = "payloads-nbirth-timestamp";
    public final static String PAYLOADS_NBIRTH_TIMESTAMP = "NBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR = "payloads-nbirth-edge-node-descriptor";
    public final static String PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR = "Every Edge Node Descriptor in any Sparkplug infrastructure MUST be unique in the system.";

    public final static String ID_PAYLOADS_NBIRTH_SEQ = "payloads-nbirth-seq";
    public final static String PAYLOADS_NBIRTH_SEQ = "Every NBIRTH message MUST include a sequence number and it MUST have a value of 0.";

    public final static String ID_PAYLOADS_NBIRTH_BDSEQ = "payloads-nbirth-bdseq";
    public final static String PAYLOADS_NBIRTH_BDSEQ = "Every NBIRTH message MUST include a bdSeq number metric.";

    public final static String ID_PAYLOADS_NBIRTH_BDSEQ_INC = "payloads-nbirth-bdseq-inc";
    public final static String PAYLOADS_NBIRTH_BDSEQ_INC = "Every NBIRTH message in a new Sparkplug MQTT session SHOULD include a bdSeq number value that is one greater than the previous NBIRTH's bdSeq number. This value MUST never exceed 255. If in the previous NBIRTH a value of 255 was sent, the next NBIRTH MUST have a value of 0.";

    public final static String ID_PAYLOADS_NBIRTH_REBIRTH_REQ = "payloads-nbirth-rebirth-req";
    public final static String PAYLOADS_NBIRTH_REBIRTH_REQ = "Every NBIRTH MUST include a metric with the name 'Node Control/Rebirth' and have a boolean value of false.";

    public final static String ID_PAYLOADS_NBIRTH_QOS = "payloads-nbirth-qos";
    public final static String PAYLOADS_NBIRTH_QOS = "NBIRTH messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NBIRTH_RETAIN = "payloads-nbirth-retain";
    public final static String PAYLOADS_NBIRTH_RETAIN = "NBIRTH messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.20 DBIRTH
    public final static String ID_PAYLOADS_DBIRTH_TIMESTAMP = "payloads-dbirth-timestamp";
    public final static String PAYLOADS_DBIRTH_TIMESTAMP = "DBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DBIRTH_SEQ = "payloads-dbirth-seq";
    public final static String PAYLOADS_DBIRTH_SEQ = "Every DBIRTH message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DBIRTH_SEQ_INC = "payloads-dbirth-seq-inc";
    public final static String PAYLOADS_DBIRTH_SEQ_INC = "Every DBIRTH message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DBIRTH_ORDER = "payloads-dbirth-order";
    public final static String PAYLOADS_DBIRTH_ORDER = "All DBIRTH messages sent by an Edge Node MUST be sent immediately after the NBIRTH and before any NDATA or DDATA messages are published by the Edge Node.";

    public final static String ID_PAYLOADS_DBIRTH_QOS = "payloads-dbirth-qos";
    public final static String PAYLOADS_DBIRTH_QOS = "DBIRTH messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DBIRTH_RETAIN = "payloads-dbirth-retain";
    public final static String PAYLOADS_DBIRTH_RETAIN = "DBIRTH messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.21 NDATA
    public final static String ID_PAYLOADS_NDATA_TIMESTAMP = "payloads-ndata-timestamp";
    public final static String PAYLOADS_NDATA_TIMESTAMP = "NDATA messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NDATA_SEQ = "payloads-ndata-seq";
    public final static String PAYLOADS_NDATA_SEQ = "Every NDATA message MUST include a sequence number.";

    public final static String ID_PAYLOADS_NDATA_SEQ_INC = "payloads-ndata-seq-inc";
    public final static String PAYLOADS_NDATA_SEQ_INC = "Every NDATA message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_NDATA_ORDER = "payloads-ndata-order";
    public final static String PAYLOADS_NDATA_ORDER = "All NDATA messages sent by an Edge Node MUST NOT be sent until all the NBIRTH and all DBIRTH messages have been published by the Edge Node.";

    public final static String ID_PAYLOADS_NDATA_QOS = "payloads-ndata-qos";
    public final static String PAYLOADS_NDATA_QOS = "NDATA messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NDATA_RETAIN = "payloads-ndata-retain";
    public final static String PAYLOADS_NDATA_RETAIN = "NDATA messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.22 DDATA
    public final static String ID_PAYLOADS_DDATA_TIMESTAMP = "payloads-ddata-timestamp";
    public final static String PAYLOADS_DDATA_TIMESTAMP = "DDATA messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DDATA_SEQ = "payloads-ddata-seq";
    public final static String PAYLOADS_DDATA_SEQ = "Every DDATA message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DDATA_SEQ_INC = "payloads-ddata-seq-inc";
    public final static String PAYLOADS_DDATA_SEQ_INC = "Every DDATA message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DDATA_ORDER = "payloads-ddata-order";
    public final static String PAYLOADS_DDATA_ORDER = "All DDATA messages sent by an Edge Node MUST NOT be sent until all the NBIRTH and all DBIRTH messages have been published by the Edge Node.";

    public final static String ID_PAYLOADS_DDATA_QOS = "payloads-ddata-qos";
    public final static String PAYLOADS_DDATA_QOS = "DDATA messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DDATA_RETAIN = "payloads-ddata-retain";
    public final static String PAYLOADS_DDATA_RETAIN = "DDATA messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.23 NCMD
    public final static String ID_PAYLOADS_NCMD_TIMESTAMP = "payloads-ncmd-timestamp";
    public final static String PAYLOADS_NCMD_TIMESTAMP = "NCMD messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NCMD_SEQ = "payloads-ncmd-seq";
    public final static String PAYLOADS_NCMD_SEQ = "Every NCMD message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_NCMD_QOS = "payloads-ncmd-qos";
    public final static String PAYLOADS_NCMD_QOS = "NCMD messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NCMD_RETAIN = "payloads-ncmd-retain";
    public final static String PAYLOADS_NCMD_RETAIN = "NCMD messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.24 DCMD
    public final static String ID_PAYLOADS_DCMD_TIMESTAMP = "payloads-dcmd-timestamp";
    public final static String PAYLOADS_DCMD_TIMESTAMP = "DCMD messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DCMD_SEQ = "payloads-dcmd-seq";
    public final static String PAYLOADS_DCMD_SEQ = "Every DCMD message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_DCMD_QOS = "payloads-dcmd-qos";
    public final static String PAYLOADS_DCMD_QOS = "DCMD messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DCMD_RETAIN = "payloads-dcmd-retain";
    public final static String PAYLOADS_DCMD_RETAIN = "DCMD messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.25 NDEATH
    public final static String ID_PAYLOADS_NDEATH_SEQ = "payloads-ndeath-seq";
    public final static String PAYLOADS_NDEATH_SEQ = "Every NDEATH message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE = "payloads-ndeath-will-message";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE = "An NDEATH message MUST be registered as a Will Message in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "payloads-ndeath-will-message-qos";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "The NDEATH message MUST set the MQTT Will QoS to 1 in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "payloads-ndeath-will-message-retain";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "The NDEATH message MUST set the MQTT Will Retained flag to false in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_BDSEQ = "payloads-ndeath-bdseq";
    public final static String PAYLOADS_NDEATH_BDSEQ = "The NDEATH message MUST include the same bdSeq number value that will be used in the associated NBIRTH message.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER = "payloads-ndeath-will-message-publisher";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER = "An NDEATH message SHOULD be published by the Edge Node before it intentionally disconnects from the MQTT Server.";

    // 6.4.26 DDEATH
    public final static String ID_PAYLOADS_DDEATH_TIMESTAMP = "payloads-ddeath-timestamp";
    public final static String PAYLOADS_DDEATH_TIMESTAMP = "DDEATH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DDEATH_SEQ = "payloads-ddeath-seq";
    public final static String PAYLOADS_DDEATH_SEQ = "Every DDEATH message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DDEATH_SEQ_INC = "payloads-ddeath-seq-inc";
    public final static String PAYLOADS_DDEATH_SEQ_INC = "Every DDEATH message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DDEATH_SEQ_NUMBER = "payloads-ddeath-seq-number";
    public final static String PAYLOADS_DDEATH_SEQ_NUMBER = "A sequence number MUST be included with the DDEATH messages so the Host Application can ensure order of messages and maintain the state of the data.";

    // 6.4.27 STATE
    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE = "payloads-state-will-message";
    public final static String PAYLOADS_STATE_WILL_MESSAGE = "Sparkplug Host Applications MUST register a Will Message in the MQTT CONNECT packet on the topic 'STATE/[sparkplug_host_id]'.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_QOS = "payloads-state-will-message-qos";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_QOS = "The Sparkplug Host Application MUST set the the MQTT Will QoS to 1 in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN = "payloads-state-will-message-retain";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_RETAIN = "The Sparkplug Host Application MUST set the Will Retained flag to true in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD = "payloads-state-will-message-payload";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD = "The Sparkplug Host Application MUST set the Will Payload to the UTF-8 string of 'OFFLINE' in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_STATE_SUBSCRIBE = "payloads-state-subscribe";
    public final static String PAYLOADS_STATE_SUBSCRIBE = "After establishing an MQTT connection, the Sparkplug Host Application MUST subscribe on it's own 'STATE/[sparkplug_host_id]' topic.";

    public final static String ID_PAYLOADS_STATE_BIRTH = "payloads-state-birth";
    public final static String PAYLOADS_STATE_BIRTH = "After subscribing on it's own STATE/[sparkplug_host_id] topic, the Sparkplug Host Application MUST publish an MQTT message on the topic 'STATE/[sparkplug_host_id]' with a payload of the UTF-8 string of 'ONLINE', a QoS of 1, and the retain flag set to true.";

    // 7 Security
    // 7.1 TLS
    // 7.2 Authentication
    // 7.3 Authorization
    // 7.4 Implementation Notes
    // 7.4.1 Underlying MQTT Security
    // 7.4.2 Encrypted Sockets
    // 7.4.3 Access Control Lists
    // 8 High Availability
    // 8.1 High Availability for MQTT Servers
    // 8.1.1 MQTT Server HA Clustering (non-normative)
    // 8.1.2 High Availability Cluster
    // 8.1.3 High Availability Cluster with Load Balancer
    // 8.2 Multiple Isolated MQTT Servers (non-normative)
    // 9 Acknowledgements
    // 10 Conformance
    // 10.1 Conformance Profiles
    // 10.1.1 Sparkplug Edge Node
    // 10.1.2 Sparkplug Host Application
    public final static String ID_CONFORMANCE_PRIMARY_HOST = "conformance-primary-host";
    public final static String CONFORMANCE_PRIMARY_HOST = "Sparkplug Host Applications MUST publish 'STATE' messages that represent its Birth and Death Certificates.";

    // 10.1.3 Sparkplug Compliant MQTT Server
    public final static String ID_CONFORMANCE_MQTT_QOS0 = "conformance-mqtt-qos0";
    public final static String CONFORMANCE_MQTT_QOS0 = "A Sparkplug conformant MQTT Server MUST support publish and subscribe on QoS 0";

    public final static String ID_CONFORMANCE_MQTT_QOS1 = "conformance-mqtt-qos1";
    public final static String CONFORMANCE_MQTT_QOS1 = "A Sparkplug conformant MQTT Server MUST support publish and subscribe on QoS 1";

    public final static String ID_CONFORMANCE_MQTT_WILL_MESSAGES = "conformance-mqtt-will-messages";
    public final static String CONFORMANCE_MQTT_WILL_MESSAGES = "A Sparkplug conformant MQTT Server MUST support all aspects of Will Messages including use of the 'retain flag' and QoS 1";

    public final static String ID_CONFORMANCE_MQTT_RETAINED = "conformance-mqtt-retained";
    public final static String CONFORMANCE_MQTT_RETAINED = "A Sparkplug conformant MQTT Server MUST support all aspects of the 'retain flag'";

    // 10.1.4 Sparkplug Aware MQTT Server
    public final static String ID_CONFORMANCE_MQTT_AWARE_BASIC = "conformance-mqtt-aware-basic";
    public final static String CONFORMANCE_MQTT_AWARE_BASIC = "A Sparkplug Aware MQTT Server MUST support all aspects of a Sparkplug Compliant MQTT Server";

    public final static String ID_CONFORMANCE_MQTT_AWARE_STORE = "conformance-mqtt-aware-store";
    public final static String CONFORMANCE_MQTT_AWARE_STORE = "A Sparkplug Aware MQTT Server MUST store NBIRTH and DBIRTH messages as they pass through the MQTT Server";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC = "conformance-mqtt-aware-nbirth-mqtt-topic";
    public final static String CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC = "A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on a topic of the form: $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN = "conformance-mqtt-aware-nbirth-mqtt-retain";
    public final static String CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN = "A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on the topic: $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id with the MQTT retain flag set to true";

    public final static String ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC = "conformance-mqtt-aware-dbirth-mqtt-topic";
    public final static String CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC = "A Sparkplug Aware MQTT Server MUST make DBIRTH messages available on a topic of the form: $sparkplug/certificates/namespace/group_id/DBIRTH/edge_node_id/device_id";

    public final static String ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN = "conformance-mqtt-aware-dbirth-mqtt-retain";
    public final static String CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN = "A Sparkplug Aware MQTT Server MUST make DBIRTH messages available on the topic: $sparkplug/certificates/namespace/group_id/DBIRTH/edge_node_id/device_id with the MQTT retain flag set to true";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP = "conformance-mqtt-aware-ndeath-timestamp";
    public final static String CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP = "A Sparkplug Aware MQTT Server MAY replace the timestmap of NDEATH messages. If it does, it MUST set the timestamp to the UTC time at which it attempts to deliver the NDEATH to subscribed clients";


    // 11 Appendix A: Open Source Software (non-normative)
    // 11.1 OASIS MQTT Specifications
    // 11.2 Eclipse Foundation IoT Resources
    // 11.3 Eclipse Paho
    // 11.4 Google Protocol Buffers
    // 11.5 Eclipse Kura Google Protocol Buffer Schema
    // 11.6 Raspberry Pi Hardware
    // 12 Appendix B: List of Normative Statements (non-normative)
}
// no of assertions 231
