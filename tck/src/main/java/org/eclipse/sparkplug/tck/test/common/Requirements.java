
/*
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
    public final static String INTRO_SPARKPLUG_HOST_STATE = "[tck-id-intro-sparkplug-host-state] Sparkplug Host Applications MUST publish STATE messages denoting their online and offline status.";

    // 1.4.1.7 Primary Host Application
    // 1.4.1.8 Sparkplug Identifiers
    public final static String ID_INTRO_GROUP_ID_STRING = "intro-group-id-string";
    public final static String INTRO_GROUP_ID_STRING = "[tck-id-intro-group-id-string] The Group ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_GROUP_ID_CHARS = "intro-group-id-chars";
    public final static String INTRO_GROUP_ID_CHARS = "[tck-id-intro-group-id-chars] Because the Group ID is used in MQTT topic strings the Group ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    public final static String ID_INTRO_EDGE_NODE_ID_STRING = "intro-edge-node-id-string";
    public final static String INTRO_EDGE_NODE_ID_STRING = "[tck-id-intro-edge-node-id-string] The Edge Node ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_EDGE_NODE_ID_CHARS = "intro-edge-node-id-chars";
    public final static String INTRO_EDGE_NODE_ID_CHARS = "[tck-id-intro-edge-node-id-chars] Because the Edge Node ID is used in MQTT topic strings the Edge Node ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    public final static String ID_INTRO_DEVICE_ID_STRING = "intro-device-id-string";
    public final static String INTRO_DEVICE_ID_STRING = "[tck-id-intro-device-id-string] The Device ID MUST be a UTF-8 string and used as part of the Sparkplug topics as defined in the Topics Section.";

    public final static String ID_INTRO_DEVICE_ID_CHARS = "intro-device-id-chars";
    public final static String INTRO_DEVICE_ID_CHARS = "[tck-id-intro-device-id-chars] Because the Device ID is used in MQTT topic strings the Device ID MUST only contain characters allowed for MQTT topics per the MQTT Specification.";

    public final static String ID_INTRO_EDGE_NODE_ID_UNIQUENESS = "intro-edge-node-id-uniqueness";
    public final static String INTRO_EDGE_NODE_ID_UNIQUENESS = "[tck-id-intro-edge-node-id-uniqueness] The Edge Node Descriptor MUST be unique within the context of all of other Edge Nodes within the Sparkplug infrastructure.";

    // 1.4.1.9 Sparkplug Metric
    // 1.4.1.10 Data Types
    // 1.5 Normative References
    // 1.6 Consolidated List of Normative Statements
    // 1.7 Security
    // 1.7.1 Authentication
    // 1.7.2 Authorization
    // 1.7.3 Encryption
    // 1.8 Editing Convention
    // 1.9 Leveraging Standards and Open Source
    // 2 Principles
    // 2.1 Pub/Sub
    // 2.2 Report by Exception
    public final static String ID_PRINCIPLES_RBE_RECOMMENDED = "principles-rbe-recommended";
    public final static String PRINCIPLES_RBE_RECOMMENDED = "[tck-id-principles-rbe-recommended] Because of the stateful nature of Sparkplug sessions, data SHOULD NOT be published from Edge Nodes on a periodic basis and instead SHOULD be published using a RBE based approach.";

    // 2.3 Continuous Session Awareness
    // 2.4 Birth and Death Certificates
    public final static String ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "principles-birth-certificates-order";
    public final static String PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "[tck-id-principles-birth-certificates-order] Birth Certificates MUST be the first MQTT messages published by any Edge Node or any Host Application.";

    // 2.5 Persistent vs Non-Persistent Connections for Edge Nodes
    public final static String ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION_311 = "principles-persistence-clean-session-311";
    public final static String PRINCIPLES_PERSISTENCE_CLEAN_SESSION_311 = "[tck-id-principles-persistence-clean-session-311] If the MQTT client is using MQTT v3.1.1, the Edge Node's MQTT CONNECT packet MUST set the 'Clean Session' flag to true.";

    public final static String ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION_50 = "principles-persistence-clean-session-50";
    public final static String PRINCIPLES_PERSISTENCE_CLEAN_SESSION_50 = "[tck-id-principles-persistence-clean-session-50] If the MQTT client is using MQTT v5.0, the Edge Node's MQTT CONNECT packet MUST set the 'Clean Start' flag to true and the 'Session Expiry Interval' to 0.";

    // 3 Sparkplug Architecture and Infrastructure Components
    // 3.1 MQTT Server(s)
    // 3.2 MQTT Edge Node
    // 3.3 Device / Sensor
    // 3.4 MQTT Enabled Device (Sparkplug)
    // 3.5 Primary Host Application
    // 3.6 Sparkplug Host Application
    public final static String ID_COMPONENTS_PH_STATE = "components-ph-state";
    public final static String COMPONENTS_PH_STATE = "[tck-id-components-ph-state] A Sparkplug Host Application MUST utilize the STATE messages to denote whether it is online or offline at any given point in time.";

    // 4 Topics and Messages
    // 4.1 Topic Namespace Elements
    public final static String ID_TOPIC_STRUCTURE = "topic-structure";
    public final static String TOPIC_STRUCTURE = "[tck-id-topic-structure] All MQTT clients using the Sparkplug specification MUST use the following topic namespace structure";

    // 4.1.1 namespace Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_A = "topic-structure-namespace-a";
    public final static String TOPIC_STRUCTURE_NAMESPACE_A = "[tck-id-topic-structure-namespace-a] For the Sparkplug B version of the payload definition, the UTF-8 string constant for the namespace element MUST be";

    // 4.1.2 group_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID = "topic-structure-namespace-valid-group-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID = "[tck-id-topic-structure-namespace-valid-group-id] The format of the Group ID MUST be a valid UTF-8 string with the exception of the reserved characters of + (plus), / (forward slash), and # (number sign).";

    // 4.1.3 message_type Element
    // 4.1.4 edge_node_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR = "topic-structure-namespace-unique-edge-node-descriptor";
    public final static String TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR = "[tck-id-topic-structure-namespace-unique-edge-node-descriptor] The group_id combined with the edge_node_id element MUST be unique from any other group_id/edge_node_id assigned in the MQTT infrastructure.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID = "topic-structure-namespace-valid-edge-node-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID = "[tck-id-topic-structure-namespace-valid-edge-node-id] The format of the edge_node_id MUST be a valid UTF-8 string with the exception of the reserved characters of + (plus), / (forward slash), and # (number sign).";

    // 4.1.5 device_id Element
    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID = "topic-structure-namespace-valid-device-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID = "[tck-id-topic-structure-namespace-valid-device-id] The format of the device_id MUST be a valid UTF-8 string except for the reserved characters of + (plus), / (forward slash), and # (number sign).";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID = "topic-structure-namespace-unique-device-id";
    public final static String TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID = "[tck-id-topic-structure-namespace-unique-device-id] The device_id MUST be unique from other devices being reported on by the same Edge Node.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE = "topic-structure-namespace-duplicate-device-id-across-edge-node";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE = "[tck-id-topic-structure-namespace-duplicate-device-id-across-edge-node] The device_id MAY be duplicated from Edge Node to other Edge Nodes.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES = "topic-structure-namespace-device-id-associated-message-types";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES = "[tck-id-topic-structure-namespace-device-id-associated-message-types] The device_id MUST be included with message_type elements DBIRTH, DDEATH, DDATA, and DCMD based topics.";

    public final static String ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES = "topic-structure-namespace-device-id-non-associated-message-types";
    public final static String TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES = "[tck-id-topic-structure-namespace-device-id-non-associated-message-types] The device_id MUST NOT be included with message_type elements NBIRTH, NDEATH, NDATA, NCMD, and STATE based topics";

    // 4.2 Message Types and Contents
    // 4.2.1 Edge Node
    // 4.2.1.1 Birth Message (NBIRTH)
    // 4.2.1.1.1 Topic (NBIRTH)
    public final static String ID_TOPICS_NBIRTH_TOPIC = "topics-nbirth-topic";
    public final static String TOPICS_NBIRTH_TOPIC = "[tck-id-topics-nbirth-topic] The Birth Certificate topic for a Sparkplug Edge Node MUST be of the form 'namespace/group_id/NBIRTH/edge_node_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id and edge_node_id are replaced with the Group and Edge Node ID for this specific Edge Node.";

    // 4.2.1.1.2 Payload (NBIRTH)
    public final static String ID_TOPICS_NBIRTH_MQTT = "topics-nbirth-mqtt";
    public final static String TOPICS_NBIRTH_MQTT = "[tck-id-topics-nbirth-mqtt] NBIRTH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NBIRTH_SEQ_NUM = "topics-nbirth-seq-num";
    public final static String TOPICS_NBIRTH_SEQ_NUM = "[tck-id-topics-nbirth-seq-num] The NBIRTH MUST include a sequence number in the payload and it MUST have a value of 0.";

    public final static String ID_TOPICS_NBIRTH_TIMESTAMP = "topics-nbirth-timestamp";
    public final static String TOPICS_NBIRTH_TIMESTAMP = "[tck-id-topics-nbirth-timestamp] The NBIRTH MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_NBIRTH_METRIC_REQS = "topics-nbirth-metric-reqs";
    public final static String TOPICS_NBIRTH_METRIC_REQS = "[tck-id-topics-nbirth-metric-reqs] The NBIRTH MUST include every metric the Edge Node will ever report on.";

    public final static String ID_TOPICS_NBIRTH_METRICS = "topics-nbirth-metrics";
    public final static String TOPICS_NBIRTH_METRICS = "[tck-id-topics-nbirth-metrics] At a minimum each metric MUST include the metric name, datatype, and current value.";

    public final static String ID_TOPICS_NBIRTH_TEMPLATES = "topics-nbirth-templates";
    public final static String TOPICS_NBIRTH_TEMPLATES = "[tck-id-topics-nbirth-templates] If Template instances will be published by this Edge Node or any devices, all Template definitions MUST be published in the NBIRTH.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_INCLUDED = "topics-nbirth-bdseq-included";
    public final static String TOPICS_NBIRTH_BDSEQ_INCLUDED = "[tck-id-topics-nbirth-bdseq-included] A bdSeq number as a metric MUST be included in the payload.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_MATCHING = "topics-nbirth-bdseq-matching";
    public final static String TOPICS_NBIRTH_BDSEQ_MATCHING = "[tck-id-topics-nbirth-bdseq-matching] This MUST match the bdSeq number provided in the MQTT CONNECT packet's Will Message payload.";

    public final static String ID_TOPICS_NBIRTH_BDSEQ_INCREMENT = "topics-nbirth-bdseq-increment";
    public final static String TOPICS_NBIRTH_BDSEQ_INCREMENT = "[tck-id-topics-nbirth-bdseq-increment] The bdSeq number MUST start at zero and increment by one on every new MQTT CONNECT packet.";

    public final static String ID_TOPICS_NBIRTH_REBIRTH_METRIC = "topics-nbirth-rebirth-metric";
    public final static String TOPICS_NBIRTH_REBIRTH_METRIC = "[tck-id-topics-nbirth-rebirth-metric] The NBIRTH message MUST include the following metric";

    // 4.2.1.2 Data Message (NDATA)
    // 4.2.1.2.1 Topic (NDATA)
    public final static String ID_TOPICS_NDATA_TOPIC = "topics-ndata-topic";
    public final static String TOPICS_NDATA_TOPIC = "[tck-id-topics-ndata-topic] The Edge Node data topic for a Sparkplug Edge Node MUST be of the form 'namespace/group_id/NDATA/edge_node_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id and edge_node_id are replaced with the Group and Edge Node ID for this specific Edge Node.";

    // 4.2.1.2.2 Payload (NDATA)
    public final static String ID_TOPICS_NDATA_MQTT = "topics-ndata-mqtt";
    public final static String TOPICS_NDATA_MQTT = "[tck-id-topics-ndata-mqtt] NDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NDATA_SEQ_NUM = "topics-ndata-seq-num";
    public final static String TOPICS_NDATA_SEQ_NUM = "[tck-id-topics-ndata-seq-num] The NDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_NDATA_TIMESTAMP = "topics-ndata-timestamp";
    public final static String TOPICS_NDATA_TIMESTAMP = "[tck-id-topics-ndata-timestamp] The NDATA MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_NDATA_PAYLOAD = "topics-ndata-payload";
    public final static String TOPICS_NDATA_PAYLOAD = "[tck-id-topics-ndata-payload] The NDATA MUST include the Edge Node's metrics that have changed since the last NBIRTH or NDATA message.";

    // 4.2.1.3 Death Message (NDEATH)
    // 4.2.1.3.1 Topic (NDEATH)
    public final static String ID_TOPICS_NDEATH_TOPIC = "topics-ndeath-topic";
    public final static String TOPICS_NDEATH_TOPIC = "[tck-id-topics-ndeath-topic] The Edge Node Death Certificate topic for a Sparkplug Edge Node MUST be of the form 'namespace/group_id/NDEATH/edge_node_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id and edge_node_id are replaced with the Group and Edge Node ID for this specific Edge Node.";

    // 4.2.1.3.2 Payload (NDEATH)
    public final static String ID_TOPICS_NDEATH_PAYLOAD = "topics-ndeath-payload";
    public final static String TOPICS_NDEATH_PAYLOAD = "[tck-id-topics-ndeath-payload] The NDEATH message contains a very simple payload that MUST only include a single metric, the bdSeq number, so that the NDEATH event can be associated with the NBIRTH.";

    public final static String ID_TOPICS_NDEATH_SEQ = "topics-ndeath-seq";
    public final static String TOPICS_NDEATH_SEQ = "[tck-id-topics-ndeath-seq] The NDEATH message MUST NOT include a sequence number.";

    // 4.2.1.4 Command (NCMD)
    // 4.2.1.4.1 Topic (NCMD)
    public final static String ID_TOPICS_NCMD_TOPIC = "topics-ncmd-topic";
    public final static String TOPICS_NCMD_TOPIC = "[tck-id-topics-ncmd-topic] The Edge Node command topic for a Sparkplug Edge Node MUST be of the form 'namespace/group_id/NCMD/edge_node_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id and edge_node_id are replaced with the Group and Edge Node ID for this specific Edge Node.";

    // 4.2.1.4.2 Payload (NCMD)
    public final static String ID_TOPICS_NCMD_MQTT = "topics-ncmd-mqtt";
    public final static String TOPICS_NCMD_MQTT = "[tck-id-topics-ncmd-mqtt] NCMD messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_NCMD_TIMESTAMP = "topics-ncmd-timestamp";
    public final static String TOPICS_NCMD_TIMESTAMP = "[tck-id-topics-ncmd-timestamp] The NCMD MUST include a timestamp denoting the Date/Time the message was sent from the Host Application's MQTT client.";

    public final static String ID_TOPICS_NCMD_PAYLOAD = "topics-ncmd-payload";
    public final static String TOPICS_NCMD_PAYLOAD = "[tck-id-topics-ncmd-payload] The NCMD MUST include the metrics that need to be written to on the Edge Node.";

    // 4.2.2 Device / Sensor
    // 4.2.2.1 Birth Message (DBIRTH)
    // 4.2.2.1.1 Topic (DBIRTH)
    public final static String ID_TOPICS_DBIRTH_TOPIC = "topics-dbirth-topic";
    public final static String TOPICS_DBIRTH_TOPIC = "[tck-id-topics-dbirth-topic] The Device Birth topic for a Sparkplug Device MUST be of the form 'namespace/group_id/DBIRTH/edge_node_id/device_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id, edge_node_id, and device_id are replaced with the Group, Edge Node, and Device ID for this specific Device.";

    // 4.2.2.1.2 Payload (DBIRTH)
    public final static String ID_TOPICS_DBIRTH_MQTT = "topics-dbirth-mqtt";
    public final static String TOPICS_DBIRTH_MQTT = "[tck-id-topics-dbirth-mqtt] DBIRTH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DBIRTH_SEQ = "topics-dbirth-seq";
    public final static String TOPICS_DBIRTH_SEQ = "[tck-id-topics-dbirth-seq] The DBIRTH MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_DBIRTH_TIMESTAMP = "topics-dbirth-timestamp";
    public final static String TOPICS_DBIRTH_TIMESTAMP = "[tck-id-topics-dbirth-timestamp] The DBIRTH MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_DBIRTH_METRIC_REQS = "topics-dbirth-metric-reqs";
    public final static String TOPICS_DBIRTH_METRIC_REQS = "[tck-id-topics-dbirth-metric-reqs] The DBIRTH MUST include every metric the Edge Node will ever report on.";

    public final static String ID_TOPICS_DBIRTH_METRICS = "topics-dbirth-metrics";
    public final static String TOPICS_DBIRTH_METRICS = "[tck-id-topics-dbirth-metrics] At a minimum each metric MUST include the metric name, metric datatype, and current value.";

    // 4.2.2.2 Data Message (DDATA)
    // 4.2.2.2.1 Topic (DDATA)
    public final static String ID_TOPICS_DDATA_TOPIC = "topics-ddata-topic";
    public final static String TOPICS_DDATA_TOPIC = "[tck-id-topics-ddata-topic] The Device command topic for a Sparkplug Device MUST be of the form 'namespace/group_id/DDATA/edge_node_id/device_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id, edge_node_id, and device_id are replaced with the Group, Edge Node, and Device ID for this specific Device.";

    // 4.2.2.2.2 Payload (DDATA)
    public final static String ID_TOPICS_DDATA_MQTT = "topics-ddata-mqtt";
    public final static String TOPICS_DDATA_MQTT = "[tck-id-topics-ddata-mqtt] DDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DDATA_SEQ_NUM = "topics-ddata-seq-num";
    public final static String TOPICS_DDATA_SEQ_NUM = "[tck-id-topics-ddata-seq-num] The DDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    public final static String ID_TOPICS_DDATA_TIMESTAMP = "topics-ddata-timestamp";
    public final static String TOPICS_DDATA_TIMESTAMP = "[tck-id-topics-ddata-timestamp] The DDATA MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public final static String ID_TOPICS_DDATA_PAYLOAD = "topics-ddata-payload";
    public final static String TOPICS_DDATA_PAYLOAD = "[tck-id-topics-ddata-payload] The DDATA MUST include the Device's metrics that have changed since the last DBIRTH or DDATA message.";

    // 4.2.2.3 Death Message (DDEATH)
    // 4.2.2.3.1 Topic (DDEATH)
    public final static String ID_TOPICS_DDEATH_TOPIC = "topics-ddeath-topic";
    public final static String TOPICS_DDEATH_TOPIC = "[tck-id-topics-ddeath-topic] The Device Death Certificate topic for a Sparkplug Device MUST be of the form 'namespace/group_id/DDEATH/edge_node_id/device_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id, edge_node_id, and device_id are replaced with the Group, Edge Node, and Device ID for this specific Device.";

    // 4.2.2.3.2 Payload (DDEATH)
    public final static String ID_TOPICS_DDEATH_MQTT = "topics-ddeath-mqtt";
    public final static String TOPICS_DDEATH_MQTT = "[tck-id-topics-ddeath-mqtt] DDEATH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DDEATH_SEQ_NUM = "topics-ddeath-seq-num";
    public final static String TOPICS_DDEATH_SEQ_NUM = "[tck-id-topics-ddeath-seq-num] The DDEATH MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.";

    // 4.2.2.4 Command (DCMD)
    // 4.2.2.4.1 Topic DCMD)
    public final static String ID_TOPICS_DCMD_TOPIC = "topics-dcmd-topic";
    public final static String TOPICS_DCMD_TOPIC = "[tck-id-topics-dcmd-topic] The Device command topic for a Sparkplug Device MUST be of the form 'namespace/group_id/DCMD/edge_node_id/device_id' where the namespace is replaced with the specific namespace for this version of Sparkplug and the group_id, edge_node_id, and device_id are replaced with the Group, Edge Node, and Device ID for this specific Device.";

    // 4.2.2.4.2 Payload (DCMD)
    public final static String ID_TOPICS_DCMD_MQTT = "topics-dcmd-mqtt";
    public final static String TOPICS_DCMD_MQTT = "[tck-id-topics-dcmd-mqtt] DCMD messages MUST be published with MQTT QoS equal to 0 and retain equal to false.";

    public final static String ID_TOPICS_DCMD_TIMESTAMP = "topics-dcmd-timestamp";
    public final static String TOPICS_DCMD_TIMESTAMP = "[tck-id-topics-dcmd-timestamp] The DCMD MUST include a timestamp denoting the Date/Time the message was sent from the Host Application's MQTT client.";

    public final static String ID_TOPICS_DCMD_PAYLOAD = "topics-dcmd-payload";
    public final static String TOPICS_DCMD_PAYLOAD = "[tck-id-topics-dcmd-payload] The DCMD MUST include the metrics that need to be written to on the Device.";

    // 4.2.3 Sparkplug Host Application
    // 4.2.3.1 Birth Certificate Message (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_MESSAGE = "host-topic-phid-birth-message";
    public final static String HOST_TOPIC_PHID_BIRTH_MESSAGE = "[tck-id-host-topic-phid-birth-message] The first MQTT message a Host Application MUST publish is a Birth Certificate.";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_QOS = "host-topic-phid-birth-qos";
    public final static String HOST_TOPIC_PHID_BIRTH_QOS = "[tck-id-host-topic-phid-birth-qos] The MQTT Quality of Service (QoS) MUST be set to 1";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_RETAIN = "host-topic-phid-birth-retain";
    public final static String HOST_TOPIC_PHID_BIRTH_RETAIN = "[tck-id-host-topic-phid-birth-retain] The MQTT retain flag for the Birth Certificate MUST be set to TRUE";

    // 4.2.3.1.1 Birth Certificate Topic (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_TOPIC = "host-topic-phid-birth-topic";
    public final static String HOST_TOPIC_PHID_BIRTH_TOPIC = "[tck-id-host-topic-phid-birth-topic] The Sparkplug Host Application Birth topic MUST be of the form spBv1.0/STATE/sparkplug_host_id where the sparkplug_host_id must be replaced with the specific Spakrplug Host ID of this Sparkplug Host Application.";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED = "host-topic-phid-birth-sub-required";
    public final static String HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED = "[tck-id-host-topic-phid-birth-sub-required] The Sparkplug Host Application MUST subscribe to its own spBv1.0/STATE/sparkplug_host_id and the appropriate spBv1.0 topic(s) immediately after successfully connecting to the MQTT Server.";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_REQUIRED = "host-topic-phid-birth-required";
    public final static String HOST_TOPIC_PHID_BIRTH_REQUIRED = "[tck-id-host-topic-phid-birth-required] The Sparkplug Host Application MUST publish a Sparkplug Host Application BIRTH message to the MQTT Server immediately after successfully subscribing its own spBv1.0/STATE/sparkplug_host_id topic.";

    // 4.2.3.1.2 Birth Certificate Payload (STATE)
    public final static String ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD = "host-topic-phid-birth-payload";
    public final static String HOST_TOPIC_PHID_BIRTH_PAYLOAD = "[tck-id-host-topic-phid-birth-payload] The Birth Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'true'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ = "host-topic-phid-birth-payload-bdseq";
    public final static String HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ = "[tck-id-host-topic-phid-birth-payload-bdseq] The bdSeq metric value MUST be be the same value set in the immediately prior MQTT CONNECT packet's Will Message payload.";

    // 4.2.3.2 Death Certificate Message (STATE)
    public final static String ID_HOST_TOPIC_PHID_DEATH_QOS = "host-topic-phid-death-qos";
    public final static String HOST_TOPIC_PHID_DEATH_QOS = "[tck-id-host-topic-phid-death-qos] The MQTT Quality of Service (QoS) MUST be set to 1";

    public final static String ID_HOST_TOPIC_PHID_DEATH_RETAIN = "host-topic-phid-death-retain";
    public final static String HOST_TOPIC_PHID_DEATH_RETAIN = "[tck-id-host-topic-phid-death-retain] The MQTT retain flag for the Birth Certificate MUST be set to TRUE";

    // 4.2.3.2.1 Death Certificate Topic (STATE)
    public final static String ID_HOST_TOPIC_PHID_DEATH_TOPIC = "host-topic-phid-death-topic";
    public final static String HOST_TOPIC_PHID_DEATH_TOPIC = "[tck-id-host-topic-phid-death-topic] The Sparkplug Host Application Death topic MUST be of the form spBv1.0/STATE/sparkplug_host_id where the sparkplug_host_id must be replaced with the specific Spakrplug Host ID of this Sparkplug Host Application.";

    public final static String ID_HOST_TOPIC_PHID_DEATH_REQUIRED = "host-topic-phid-death-required";
    public final static String HOST_TOPIC_PHID_DEATH_REQUIRED = "[tck-id-host-topic-phid-death-required] The Sparkplug Host Application MUST provide a Will message in the MQTT CONNECT packet";

    // 4.2.3.2.2 Death Certificate Payload (STATE)
    public final static String ID_HOST_TOPIC_PHID_DEATH_PAYLOAD = "host-topic-phid-death-payload";
    public final static String HOST_TOPIC_PHID_DEATH_PAYLOAD = "[tck-id-host-topic-phid-death-payload] The Death Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'false'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ = "host-topic-phid-death-payload-bdseq";
    public final static String HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ = "[tck-id-host-topic-phid-death-payload-bdseq] The Death Certificate's bdSeq number value MUST have a value of one more than the bdSeq number value sent in the prior MQTT CONNECT packet from the Host Application unless the previous value was 255. In this case the new bdSeq number value MUST be 0.";

    // 5 Operational Behavior
    // 5.1 Timestamps in Sparkplug
    // 5.2 Case Sensitivity in Sparkplug
    public final static String ID_CASE_SENSITIVITY_SPARKPLUG_IDS = "case-sensitivity-sparkplug-ids";
    public final static String CASE_SENSITIVITY_SPARKPLUG_IDS = "[tck-id-case-sensitivity-sparkplug-ids] Edge Nodes in a Sparkplug environment SHOULD NOT have Sparkplug IDs (Group, Edge Node, or Device IDs) that when converted to lower case match";

    public final static String ID_CASE_SENSITIVITY_METRIC_NAMES = "case-sensitivity-metric-names";
    public final static String CASE_SENSITIVITY_METRIC_NAMES = "[tck-id-case-sensitivity-metric-names] An Edge Node SHOULD NOT publish metric names that when converted to all lower case match.";

    // 5.3 Host Application Session Establishment
    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311 = "message-flow-phid-sparkplug-clean-session-311";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311 = "[tck-id-message-flow-phid-sparkplug-clean-session-311] The CONNECT Control Packet for all Sparkplug Host Applications when using MQTT 3.1.1 MUST set the MQTT 'Clean Session' flag to true.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50 = "message-flow-phid-sparkplug-clean-session-50";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50 = "[tck-id-message-flow-phid-sparkplug-clean-session-50] The CONNECT Control Packet for all Sparkplug Host Applications when using MQTT 5.0 MUST set the the MQTT 'Clean Start' flag to true and the 'Session Expiry Interval' to 0.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION = "message-flow-phid-sparkplug-subscription";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION = "[tck-id-message-flow-phid-sparkplug-subscription] The subscription on the Sparkplug Topic Namespace and the STATE topic MUST be done immediately after successfully establishing the MQTT session and before publishing its own STATE message.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH = "message-flow-phid-sparkplug-state-publish";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH = "[tck-id-message-flow-phid-sparkplug-state-publish] Once an MQTT Session has been established, the Sparkplug Host Application subscriptions on the Sparkplug Topic Namespace have been established and the STATE topic subscription has been been established, the Sparkplug Host Application MUST publish a new STATE message.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD = "message-flow-phid-sparkplug-state-publish-payload";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD = "[tck-id-message-flow-phid-sparkplug-state-publish-payload] The Host Application Birth Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'true'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ = "message-flow-phid-sparkplug-state-publish-payload-bdseq";
    public final static String MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ = "[tck-id-message-flow-phid-sparkplug-state-publish-payload-bdseq] The bdSeq metric value MUST be be the same value set in the immediately prior MQTT CONNECT packet's Will Message payload.";

    public final static String ID_G = "g";
    public final static String G = "All metric data associated with any Sparkplug Edge Node that was connected to that MQTT Server and known by the Host Application MUST be updated to a STALE data quality if the Host Application loses connection to the MQTT Server.";

    public final static String ID_MESSAGE_FLOW_HID_SPARKPLUG_STATE_MESSAGE_DELIVERED = "message-flow-hid-sparkplug-state-message-delivered";
    public final static String MESSAGE_FLOW_HID_SPARKPLUG_STATE_MESSAGE_DELIVERED = "[tck-id-message-flow-hid-sparkplug-state-message-delivered] After publishing its own Host Application STATE message, if at any point the Host Application is delivered a STATE message on its own Host Application ID with a 'online' value of false, it MUST immediately republish its STATE message to the same MQTT Server with a 'online' value of true, 'bdSeq' number value that matches the value in the prior MQTT CONNECT packet Will Message, and the 'timestamp' set to the current UTC time in milliseconds since Epoch.";

    // 5.4 Edge Node Session Establishment
    public final static String ID_MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE = "message-flow-edge-node-ncmd-subscribe";
    public final static String MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE = "[tck-id-message-flow-edge-node-ncmd-subscribe] The MQTT client associated with the Edge Node MUST subscribe to a topic of the form 'spBv1.0/group_id/NCMD/edge_node_id' where group_id is the Sparkplug Group ID and the edge_node_id is the Sparkplug Edge Node ID for this Edge Node. It MUST subscribe on this topic with a QoS of 1.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "message-flow-edge-node-birth-publish-connect";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "[tck-id-message-flow-edge-node-birth-publish-connect] Any Edge Node in the MQTT infrastructure MUST establish an MQTT Session prior to publishing NBIRTH and DBIRTH messages.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE = "message-flow-edge-node-birth-publish-will-message";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE = "[tck-id-message-flow-edge-node-birth-publish-will-message] When a Sparkplug Edge Node sends its MQTT CONNECT packet, it MUST include a Will Message.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC = "message-flow-edge-node-birth-publish-will-message-topic";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC = "[tck-id-message-flow-edge-node-birth-publish-will-message-topic] The Edge Node's MQTT Will Message's topic MUST be of the form 'spBv1.0/group_id/NDEATH/edge_node_id' where group_id is the Sparkplug Group ID and the edge_node_id is the Sparkplug Edge Node ID for this Edge Node";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD = "message-flow-edge-node-birth-publish-will-message-payload";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD = "[tck-id-message-flow-edge-node-birth-publish-will-message-payload] The Edge Node's MQTT Will Message's payload MUST be a Sparkplug Google Protobuf encoded payload.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ = "message-flow-edge-node-birth-publish-will-message-payload-bdSeq";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ = "[tck-id-message-flow-edge-node-birth-publish-will-message-payload-bdSeq] The Edge Node's MQTT Will Message's payload MUST include a metric with the name of 'bdSeq', the datatype of INT64, and the value MUST be incremented by one from the value in the previous MQTT CONNECT packet unless the value would be greater than 255. If in the previous NBIRTH a value of 255 was sent, the next MQTT Connect packet Will Message payload bdSeq number value MUST have a value of 0.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS = "message-flow-edge-node-birth-publish-will-message-qos";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS = "[tck-id-message-flow-edge-node-birth-publish-will-message-qos] The Edge Node's MQTT Will Message's MQTT QoS MUST be 1.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED = "message-flow-edge-node-birth-publish-will-message-will-retained";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED = "[tck-id-message-flow-edge-node-birth-publish-will-message-will-retained] The Edge Node's MQTT Will Message's retained flag MUST be set to false.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT = "message-flow-edge-node-birth-publish-phid-wait";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT = "[tck-id-message-flow-edge-node-birth-publish-phid-wait] If the Edge Node is configured to wait for a Primary Host Application it MUST verify the Primary Host Application is online via the STATE topic before publishing NBIRTH and DBIRTH messages.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID = "message-flow-edge-node-birth-publish-phid-wait-id";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID = "[tck-id-message-flow-edge-node-birth-publish-phid-wait-id] If the Edge Node is configured to wait for a Primary Host Application it MUST validate the Host Application ID as the last token in the STATE message topic string matches the configured Primary Host Application ID for this Edge Node.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE = "message-flow-edge-node-birth-publish-phid-wait-online";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE = "[tck-id-message-flow-edge-node-birth-publish-phid-wait-online] If the Edge Node is configured to wait for a Primary Host Application it MUST validate the 'online' boolean flag is true in the STATE message payload before considering the Primary Host Application to be online and active.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_BDSEQ = "message-flow-edge-node-birth-publish-phid-wait-bdSeq";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_BDSEQ = "[tck-id-message-flow-edge-node-birth-publish-phid-wait-bdSeq] If the Edge Node is configured to wait for a Primary Host Application it MUST validate the 'bdseq' number is greater than the previous STATE message bdSeq number in the STATE message payload before considering the Primary Host Application to be online and active. If no previous bdSeq number was received by this Edge Node it MUST consider the incoming bdSeq number the latest/valid.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC = "message-flow-edge-node-birth-publish-nbirth-topic";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC = "[tck-id-message-flow-edge-node-birth-publish-nbirth-topic] The Edge Node's NBIRTH MQTT topic MUST be of the form 'spBv1.0/group_id/NBIRTH/edge_node_id' where group_id is the Sparkplug Group ID and the edge_node_id is the Sparkplug Edge Node ID for this Edge Node";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD = "message-flow-edge-node-birth-publish-nbirth-payload";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD = "[tck-id-message-flow-edge-node-birth-publish-nbirth-payload] The Edge Node's NBIRTH payload MUST be a Sparkplug Google Protobuf encoded payload.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ = "message-flow-edge-node-birth-publish-nbirth-payload-bdSeq";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ = "[tck-id-message-flow-edge-node-birth-publish-nbirth-payload-bdSeq] The Edge Node's NBIRTH payload MUST include a metric with the name of 'bdSeq' the datatype of INT64 and the value MUST be the same as the previous MQTT CONNECT packet.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS = "message-flow-edge-node-birth-publish-nbirth-qos";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS = "[tck-id-message-flow-edge-node-birth-publish-nbirth-qos] The Edge Node's NBIRTH MQTT QoS MUST be 0.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED = "message-flow-edge-node-birth-publish-nbirth-retained";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED = "[tck-id-message-flow-edge-node-birth-publish-nbirth-retained] The Edge Node's NBIRTH retained flag MUST be set to false.";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ = "message-flow-edge-node-birth-publish-nbirth-payload-seq";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ = "[tck-id-message-flow-edge-node-birth-publish-nbirth-payload-seq] The Edge Node's NBIRTH payload MUST include a 'seq' number that is between 0 and 255 (inclusive).";

    public final static String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE = "message-flow-edge-node-birth-publish-phid-offline";
    public final static String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE = "[tck-id-message-flow-edge-node-birth-publish-phid-offline] If the Edge Node is configured to wait for a Primary Host Application, it is connected to the MQTT Server, and receives a STATE message on its configured Primary Host, the bdSeq number in the payload is greater than the previous bdSeq number, and the 'online' value is false, it MUST immediately publish an NDEATH message and disconnect from the MQTT Server and start the connection establishment process over.";

    // 5.5 Edge Node Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH = "operational-behavior-edge-node-intentional-disconnect-ndeath";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH = "[tck-id-operational-behavior-edge-node-intentional-disconnect-ndeath] An Edge Node MUST publish an NDEATH before terminating the connection.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET = "operational-behavior-edge-node-intentional-disconnect-packet";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET = "[tck-id-operational-behavior-edge-node-intentional-disconnect-packet] Immediately following the NDEATH publish, a DISCONNECT packet MUST be sent to the MQTT Server.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE = "operational-behavior-edge-node-termination-host-action-ndeath-node-offline";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE = "[tck-id-operational-behavior-edge-node-termination-host-action-ndeath-node-offline] Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark the Edge Node as offline using the current Host Application's system UTC time";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE = "operational-behavior-edge-node-termination-host-action-ndeath-node-tags-stale";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE = "[tck-id-operational-behavior-edge-node-termination-host-action-ndeath-node-tags-stale] Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all metrics that were included in the previous NBIRTH as STALE using the current Host Application's system UTC time";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE = "operational-behavior-edge-node-termination-host-action-ndeath-devices-offline";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE = "[tck-id-operational-behavior-edge-node-termination-host-action-ndeath-devices-offline] Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all Sparkplug Devices associated with the Edge Node as offline using the current Host Application's system UTC time";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE = "operational-behavior-edge-node-termination-host-action-ndeath-devices-tags-stale";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE = "[tck-id-operational-behavior-edge-node-termination-host-action-ndeath-devices-tags-stale] Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all of the metrics that were included with associated Sparkplug Device DBIRTH messages as STALEusing the current Host Application's system UTC time";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE = "operational-behavior-edge-node-termination-host-offline";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE = "[tck-id-operational-behavior-edge-node-termination-host-offline] If the Edge Node is configured to use a Primary Host Application, it MUST disconnect from the current MQTT Server if a the online JSON value is false and if the bdSeq number matches the bdSeq number from the previous 'online STATE message'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_BDSEQ = "operational-behavior-edge-node-termination-host-offline-bdSeq";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_BDSEQ = "[tck-id-operational-behavior-edge-node-termination-host-offline-bdSeq] Consider an Edge Node that is configured to use a Primary Host Application and the Edge Node is connected and publishing. Then it receives an 'offline STATE message'. It MUST NOT disconnect if the bdSeq number does not match the bdSeq number value from the previous 'online STATE message'.";

    // 5.6 Device Session Establishment
    public final static String ID_MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE = "message-flow-device-dcmd-subscribe";
    public final static String MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE = "[tck-id-message-flow-device-dcmd-subscribe] If the Device supports writing to outputs, the MQTT client associated with the Device MUST subscribe to a topic of the form 'spBv1.0/group_id/DCMD/edge_node_id/device_id' where group_id is the Sparkplug Group ID the edge_node_id is the Sparkplug Edge Node ID and the device_id is the Sparkplug Device ID for this Device. It MUST subscribe on this topic with a QoS of 1.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT = "message-flow-device-birth-publish-nbirth-wait";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT = "[tck-id-message-flow-device-birth-publish-nbirth-wait] The NBIRTH message must have been sent within the current MQTT session prior to a DBIRTH being published.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC = "message-flow-device-birth-publish-dbirth-topic";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC = "[tck-id-message-flow-device-birth-publish-dbirth-topic] The Device's DBIRTH MQTT topic MUST be of the form 'spBv1.0/group_id/DBIRTH/edge_node_id/device_id' where group_id is the Sparkplug Group ID the edge_node_id is the Sparkplug Edge Node ID and the device_id is the Sparkplug Device ID for this Device.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC = "message-flow-device-birth-publish-dbirth-match-edge-node-topic";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC = "[tck-id-message-flow-device-birth-publish-dbirth-match-edge-node-topic] The Device's DBIRTH MQTT topic group_id and edge_node_id MUST match the group_id and edge_node_id that were sent in the prior NBIRTH message for the Edge Node this Device is associated with.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD = "message-flow-device-birth-publish-dbirth-payload";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD = "[tck-id-message-flow-device-birth-publish-dbirth-payload] The Device's DBIRTH payload MUST be a Sparkplug Google Protobuf encoded payload.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS = "message-flow-device-birth-publish-dbirth-qos";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS = "[tck-id-message-flow-device-birth-publish-dbirth-qos] The Device's DBIRTH MQTT QoS MUST be 0.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED = "message-flow-device-birth-publish-dbirth-retained";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED = "[tck-id-message-flow-device-birth-publish-dbirth-retained] The Device's DBIRTH retained flag MUST be set to false.";

    public final static String ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ = "message-flow-device-birth-publish-dbirth-payload-seq";
    public final static String MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ = "[tck-id-message-flow-device-birth-publish-dbirth-payload-seq] The Device's DBIRTH payload MUST include a 'seq' number that is between 0 and 255 (inclusive) and be one more than was included in the prior Sparkplug message sent from the Edge Node associated with this Device.";

    // 5.7 Device Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH = "operational-behavior-device-ddeath";
    public final static String OPERATIONAL_BEHAVIOR_DEVICE_DDEATH = "[tck-id-operational-behavior-device-ddeath] If a Sparkplug Edge Node loses connection with an attached Sparkplug Device, it MUST publish a DDEATH message on behalf of the device.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE = "operational-behavior-edge-node-termination-host-action-ddeath-devices-offline";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE = "[tck-id-operational-behavior-edge-node-termination-host-action-ddeath-devices-offline] Immediately after receiving an DDEATH from an Edge Node, Host Applications MUST mark the Sparkplug Device associated with the Edge Node as offline using the timestamp in the DDEATH payload";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE = "operational-behavior-edge-node-termination-host-action-ddeath-devices-tags-stale";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE = "[tck-id-operational-behavior-edge-node-termination-host-action-ddeath-devices-tags-stale] Immediately after receiving an DDEATH from an Edge Node, Host Applications MUST mark all of the metrics that were included with the associated Sparkplug Device DBIRTH messages as STALE using the timestamp in the DDEATH payload";

    // 5.8 Sparkplug Host Applications
    // 5.9 Sparkplug Host Application Message Ordering
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM = "operational-behavior-host-reordering-param";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM = "[tck-id-operational-behavior-host-reordering-param] Sparkplug Host Applications SHOULD provide a configurable 'Reorder Timeout' parameter";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START = "operational-behavior-host-reordering-start";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_START = "[tck-id-operational-behavior-host-reordering-start] If a message arrives with an out of order sequence number, the Host Application SHOULD start a timer denoting the start of the Reorder Timeout window";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH = "operational-behavior-host-reordering-rebirth";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH = "[tck-id-operational-behavior-host-reordering-rebirth] If the Reorder Timeout elapses and the missing message(s) have not been received, the Sparkplug Host Application SHOULD send an NCMD to the Edge Node with a 'Node Control/Rebirth' request";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS = "operational-behavior-host-reordering-success";
    public final static String OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS = "[tck-id-operational-behavior-host-reordering-success] If the missing messages that triggered the start of the Reorder Timeout timer arrive before the reordering timer elapses, the timer can be terminated and normal operation in the Host Application can continue";

    // 5.10 Primary Host Application STATE in Multiple MQTT Server Topologies
    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE = "operational-behavior-primary-application-state-with-multiple-servers-state";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE = "[tck-id-operational-behavior-primary-application-state-with-multiple-servers-state] Regardless of the number of MQTT Servers in a Sparkplug Infrastructure, every time a Primary Host Application establishes a new MQTT Session with an MQTT Server, the STATE Birth Certificate defined in the STATE description section MUST be the first message that is published after a successful MQTT Session is established with each MQTT Server.";

    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER = "operational-behavior-primary-application-state-with-multiple-servers-single-server";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER = "[tck-id-operational-behavior-primary-application-state-with-multiple-servers-single-server] The Edge Nodes MUST not connected to more than one server at any point in time.";

    public final static String ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK = "operational-behavior-primary-application-state-with-multiple-servers-walk";
    public final static String OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK = "[tck-id-operational-behavior-primary-application-state-with-multiple-servers-walk] If the Primary Host Application is offline as denoted via the STATE MQTT Message, the Edge Node MUST terminate its session with this MQTT Server and move to the next available MQTT Server that is available.";

    public final static String ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT = "operational-behavior-edge-node-birth-sequence-wait";
    public final static String OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT = "[tck-id-operational-behavior-edge-node-birth-sequence-wait] The Edge Node MUST also wait to publish its BIRTH sequence until an online=true STATE message is received by the Edge Node.";

    // 5.11 Edge Node NDATA and NCMD Messages
    // 5.12 MQTT Enabled Device Session Establishment
    // 5.13 Sparkplug Host Application Session Establishment
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID = "operational-behavior-host-application-host-id";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID = "[tck-id-operational-behavior-host-application-host-id] The sparkplug_host_id MUST be unique to all other Sparkplug Host IDs in the infrastructure.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL = "operational-behavior-host-application-connect-will";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL = "[tck-id-operational-behavior-host-application-connect-will] When a Sparkplug Host Application sends its MQTT CONNECT packet, it MUST include a Will Message.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC = "operational-behavior-host-application-connect-will-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC = "[tck-id-operational-behavior-host-application-connect-will-topic] The MQTT Will Message's topic MUST be of the form 'spBv1.0/STATE/sparkplug_host_id' where host_id is the unique identifier of the Sparkplug Host Application";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD = "operational-behavior-host-application-connect-will-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD = "[tck-id-operational-behavior-host-application-connect-will-payload] The Death Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'false'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ = "operational-behavior-host-application-connect-will-payload-bdseq";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ = "[tck-id-operational-behavior-host-application-connect-will-payload-bdseq] The Death Certificate's bdSeq number value MUST have a value of one more than the bdSeq number value sent in the prior MQTT CONNECT packet from the Host Application unless the previous value was 255. In this case the new bdSeq number value MUST be 0.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS = "operational-behavior-host-application-connect-will-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS = "[tck-id-operational-behavior-host-application-connect-will-qos] The MQTT Will Message's MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED = "operational-behavior-host-application-connect-will-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED = "[tck-id-operational-behavior-host-application-connect-will-retained] The MQTT Will Message's retained flag MUST be set to true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH = "operational-behavior-host-application-connect-birth";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH = "[tck-id-operational-behavior-host-application-connect-birth] The MQTT Client associated with the Sparkplug Host Application MUST send a birth message immediately after successfully connecting to the MQTT Server.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC = "operational-behavior-host-application-connect-birth-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC = "[tck-id-operational-behavior-host-application-connect-birth-topic] The Host Application's Birth topic MUST be of the form 'spBv1.0/STATE/sparkplug_host_id' where host_id is the unique identifier of the Sparkplug Host Application";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD = "operational-behavior-host-application-connect-birth-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD = "[tck-id-operational-behavior-host-application-connect-birth-payload] The Birth Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'true'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ = "operational-behavior-host-application-connect-birth-payload-bdseq";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ = "[tck-id-operational-behavior-host-application-connect-birth-payload-bdseq] The bdSeq metric value MUST be be the same value set in the immediately prior MQTT CONNECT packet's Will Message payload.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS = "operational-behavior-host-application-connect-birth-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS = "[tck-id-operational-behavior-host-application-connect-birth-qos] The Host Application's Birth MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED = "operational-behavior-host-application-connect-birth-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED = "[tck-id-operational-behavior-host-application-connect-birth-retained] The Host Application's Birth retained flag MUST be set to true.";

    // 5.14 Sparkplug Host Application Session Termination
    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC = "operational-behavior-host-application-death-topic";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC = "[tck-id-operational-behavior-host-application-death-topic] The Sparkplug Host Application's Death topic MUST be of the form 'spBv1.0/STATE/sparkplug_host_id' where host_id is the unique identifier of the Sparkplug Host Application.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD = "operational-behavior-host-application-death-payload";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD = "[tck-id-operational-behavior-host-application-death-payload] The Death Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'false'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ = "operational-behavior-host-application-death-payload-bdseq";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ = "[tck-id-operational-behavior-host-application-death-payload-bdseq] The Death Certificate's bdSeq number value MUST have a value of one more than the bdSeq number value sent in the prior MQTT CONNECT packet from the Host Application unless the previous value was 255. In this case the new bdSeq number value MUST be 0.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS = "operational-behavior-host-application-death-qos";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS = "[tck-id-operational-behavior-host-application-death-qos] The Sparkplug Host Application's Death MQTT QoS MUST be 1 (at least once).";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED = "operational-behavior-host-application-death-retained";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED = "[tck-id-operational-behavior-host-application-death-retained] The Sparkplug Host Application's Death retained flag MUST be set to true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL = "operational-behavior-host-application-disconnect-intentional";
    public final static String OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL = "[tck-id-operational-behavior-host-application-disconnect-intentional] In the case of intentionally disconnecting, an MQTT DISCONNECT packet MUST be sent immediately after the Death message is published.";

    // 5.15 Sparkplug Host Application Receive Data
    // 5.16 Data Publish
    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH = "operational-behavior-data-publish-nbirth";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH = "[tck-id-operational-behavior-data-publish-nbirth] NBIRTH messages MUST include all metrics for the specified Edge Node that will ever be published for that Edge Node within the established Sparkplug session.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "operational-behavior-data-publish-nbirth-values";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "[tck-id-operational-behavior-data-publish-nbirth-values] For each metric in the NBIRTH, the value must be set to the current value or if the current value is null, have the is_null flag set to true and no value specified.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE = "operational-behavior-data-publish-nbirth-change";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE = "[tck-id-operational-behavior-data-publish-nbirth-change] NDATA messages SHOULD only be published when Edge Node level metrics change.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER = "operational-behavior-data-publish-nbirth-order";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER = "[tck-id-operational-behavior-data-publish-nbirth-order] For all metrics where is_historical=false, NBIRTH and NDATA messages MUST keep metric values in chronological order in the list of metrics in the payload.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH = "operational-behavior-data-publish-dbirth";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH = "[tck-id-operational-behavior-data-publish-dbirth] DBIRTH messages MUST include all metrics for the specified Device that will ever be published for that Device within the established Sparkplug session.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "operational-behavior-data-publish-dbirth-values";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "[tck-id-operational-behavior-data-publish-dbirth-values] For each metric in the DBIRTH, the value must be set to the current value or if the current value is null, have the is_null flag set to true and no value specified.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE = "operational-behavior-data-publish-dbirth-change";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE = "[tck-id-operational-behavior-data-publish-dbirth-change] DDATA messages SHOULD only be published when Device level metrics change.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER = "operational-behavior-data-publish-dbirth-order";
    public final static String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER = "[tck-id-operational-behavior-data-publish-dbirth-order] For all metrics where is_historical=false, DBIRTH and DDATA messages MUST keep metric values in chronological order in the list of metrics in the payload.";

    // 5.17 Commands
    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "operational-behavior-data-commands-rebirth-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "[tck-id-operational-behavior-data-commands-rebirth-name] An NBIRTH message MUST include a metric with a name of 'Node Control/Rebirth'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME_ALIASES = "operational-behavior-data-commands-rebirth-name-aliases";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME_ALIASES = "[tck-id-operational-behavior-data-commands-rebirth-name-aliases] When aliases are being used by an Edge Node an NBIRTH message MUST NOT include an alias for the 'Node Control/Rebirth' metric.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "operational-behavior-data-commands-rebirth-datatype";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "[tck-id-operational-behavior-data-commands-rebirth-datatype] The 'Node Control/Rebirth' metric in the NBIRTH message MUST have a datatype of 'Boolean'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "operational-behavior-data-commands-rebirth-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "[tck-id-operational-behavior-data-commands-rebirth-value] The 'Node Control/Rebirth' metric value in the NBIRTH message MUST have a value of false.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB = "operational-behavior-data-commands-ncmd-rebirth-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB = "[tck-id-operational-behavior-data-commands-ncmd-rebirth-verb] A Rebirth Request MUST use the NCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME = "operational-behavior-data-commands-ncmd-rebirth-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME = "[tck-id-operational-behavior-data-commands-ncmd-rebirth-name] A Rebirth Request MUST include a metric with a name of 'Node Control/Rebirth'.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE = "operational-behavior-data-commands-ncmd-rebirth-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE = "[tck-id-operational-behavior-data-commands-ncmd-rebirth-value] A Rebirth Request MUST include a metric value of true.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "operational-behavior-data-commands-rebirth-action-1";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "[tck-id-operational-behavior-data-commands-rebirth-action-1] When an Edge Node receives a Rebirth Request, it MUST immediately stop sending DATA messages.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "operational-behavior-data-commands-rebirth-action-2";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "[tck-id-operational-behavior-data-commands-rebirth-action-2] After an Edge Node stops sending DATA messages, it MUST send a complete BIRTH sequence including the NBIRTH and DBIRTH(s) if applicable.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "operational-behavior-data-commands-rebirth-action-3";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "[tck-id-operational-behavior-data-commands-rebirth-action-3] The NBIRTH MUST include the same bdSeq metric with the same value it had included in the Will Message of the previous MQTT CONNECT packet.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB = "operational-behavior-data-commands-ncmd-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB = "[tck-id-operational-behavior-data-commands-ncmd-verb] An Edge Node level command MUST use the NCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME = "operational-behavior-data-commands-ncmd-metric-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME = "[tck-id-operational-behavior-data-commands-ncmd-metric-name] An NCMD message SHOULD include a metric name that was included in the associated NBIRTH message for the Edge Node.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE = "operational-behavior-data-commands-ncmd-metric-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE = "[tck-id-operational-behavior-data-commands-ncmd-metric-value] An NCMD message MUST include a compatible metric value for the metric name that it is writing to.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB = "operational-behavior-data-commands-dcmd-verb";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB = "[tck-id-operational-behavior-data-commands-dcmd-verb] A Device level command MUST use the DCMD Sparkplug verb.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_NAME = "operational-behavior-data-commands-dcmd-metric-name";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_NAME = "[tck-id-operational-behavior-data-commands-dcmd-metric-name] A DCMD message SHOULD include a metric name that was included in the associated DBIRTH message for the Device.";

    public final static String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_VALUE = "operational-behavior-data-commands-dcmd-metric-value";
    public final static String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_METRIC_VALUE = "[tck-id-operational-behavior-data-commands-dcmd-metric-value] A DCMD message MUST include a compatible metric value for the metric name that it is writing to.";

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
    public final static String PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED = "[tck-id-payloads-sequence-num-always-included] A sequence number MUST be included in the payload of every Sparkplug MQTT message except NDEATH messages.";

    public final static String ID_PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH = "payloads-sequence-num-zero-nbirth";
    public final static String PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH = "[tck-id-payloads-sequence-num-zero-nbirth] A NBIRTH message MUST always contain a sequence number of zero.";

    public final static String ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING = "payloads-sequence-num-incrementing";
    public final static String PAYLOADS_SEQUENCE_NUM_INCREMENTING = "[tck-id-payloads-sequence-num-incrementing] All subsequent messages MUST contain a sequence number that is continually increasing by one in each message until a value of 255 is reached. At that point, the sequence number of the following message MUST be zero.";

    // 6.4.6 Metric
    public final static String ID_PAYLOADS_NAME_REQUIREMENT = "payloads-name-requirement";
    public final static String PAYLOADS_NAME_REQUIREMENT = "[tck-id-payloads-name-requirement] The name MUST be included with every metric unless aliases are being used.";

    public final static String ID_PAYLOADS_ALIAS_UNIQUENESS = "payloads-alias-uniqueness";
    public final static String PAYLOADS_ALIAS_UNIQUENESS = "[tck-id-payloads-alias-uniqueness] If supplied in an NBIRTH or DBIRTH it MUST be a unique number across this Edge Node's entire set of metrics.";

    public final static String ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "payloads-alias-birth-requirement";
    public final static String PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "[tck-id-payloads-alias-birth-requirement] NBIRTH and DBIRTH messages MUST include both a metric name and alias.";

    public final static String ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "payloads-alias-data-cmd-requirement";
    public final static String PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "[tck-id-payloads-alias-data-cmd-requirement] NDATA, DDATA, NCMD, and DCMD messages MUST only include an alias and the metric name MUST be excluded.";

    public final static String ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "payloads-name-birth-data-requirement";
    public final static String PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "[tck-id-payloads-name-birth-data-requirement] The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.";

    public final static String ID_PAYLOADS_NAME_CMD_REQUIREMENT = "payloads-name-cmd-requirement";
    public final static String PAYLOADS_NAME_CMD_REQUIREMENT = "[tck-id-payloads-name-cmd-requirement] The timestamp MAY be included with metrics in NCMD and DCMD messages.";

    public final static String ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC = "payloads_metric_timestamp_in_UTC";
    public final static String PAYLOADS_METRIC_TIMESTAMP_IN_UTC = "[tck-id-payloads_metric_timestamp_in_UTC] The timestamp MUST be in UTC.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "payloads-metric-datatype-value-type";
    public final static String PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "[tck-id-payloads-metric-datatype-value-type] The datatype MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_VALUE = "payloads-metric-datatype-value";
    public final static String PAYLOADS_METRIC_DATATYPE_VALUE = "[tck-id-payloads-metric-datatype-value] The datatype MUST be one of the enumerated values as shown in the valid Sparkplug Data Types.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_REQ = "payloads-metric-datatype-req";
    public final static String PAYLOADS_METRIC_DATATYPE_REQ = "[tck-id-payloads-metric-datatype-req] The datatype MUST be included with each metric definition in NBIRTH and DBIRTH messages.";

    public final static String ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ = "payloads-metric-datatype-not-req";
    public final static String PAYLOADS_METRIC_DATATYPE_NOT_REQ = "[tck-id-payloads-metric-datatype-not-req] The datatype SHOULD NOT be included with metric definitions in NDATA, NCMD, DDATA, and DCMD messages.";

    // 6.4.7 MetaData
    // 6.4.8 PropertySet
    public final static String ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "payloads-propertyset-keys-array-size";
    public final static String PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "[tck-id-payloads-propertyset-keys-array-size] The array of keys in a PropertySet MUST contain the same number of values included in the array of PropertyValue objects.";

    public final static String ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "payloads-propertyset-values-array-size";
    public final static String PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "[tck-id-payloads-propertyset-values-array-size] The array of values in a PropertySet MUST contain the same number of items that are in the keys array.";

    // 6.4.9 PropertyValue
    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "payloads-metric-propertyvalue-type-type";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "[tck-id-payloads-metric-propertyvalue-type-type] This MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "payloads-metric-propertyvalue-type-value";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "[tck-id-payloads-metric-propertyvalue-type-value] This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types or the Sparkplug Property Value Data Types.";

    public final static String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "payloads-metric-propertyvalue-type-req";
    public final static String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "[tck-id-payloads-metric-propertyvalue-type-req] This MUST be included in Property Value Definitions in NBIRTH and DBIRTH messages.";

    // 6.4.9.1 Quality Codes
    public final static String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "payloads-propertyset-quality-value-type";
    public final static String PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "[tck-id-payloads-propertyset-quality-value-type] The 'type' of the Property Value MUST be a value of 3 which represents a Signed 32-bit Integer.";

    public final static String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "payloads-propertyset-quality-value-value";
    public final static String PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "[tck-id-payloads-propertyset-quality-value-value] The 'value' of the Property Value MUST be an int_value and be one of the valid quality codes of 0, 192, or 500.";

    // 6.4.10 PropertySetList
    // 6.4.11 DataSet
    public final static String ID_PAYLOADS_DATASET_COLUMN_SIZE = "payloads-dataset-column-size";
    public final static String PAYLOADS_DATASET_COLUMN_SIZE = "[tck-id-payloads-dataset-column-size] This MUST be an unsigned 64-bit integer representing the number of columns in this DataSet.";

    public final static String ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS = "payloads-dataset-column-num-headers";
    public final static String PAYLOADS_DATASET_COLUMN_NUM_HEADERS = "[tck-id-payloads-dataset-column-num-headers] The size of the array MUST have the same number of elements that the types array contains.";

    public final static String ID_PAYLOADS_DATASET_TYPES_DEF = "payloads-dataset-types-def";
    public final static String PAYLOADS_DATASET_TYPES_DEF = "[tck-id-payloads-dataset-types-def] This MUST be an array of unsigned 32 bit integers representing the datatypes of the columns.";

    public final static String ID_PAYLOADS_DATASET_TYPES_NUM = "payloads-dataset-types-num";
    public final static String PAYLOADS_DATASET_TYPES_NUM = "[tck-id-payloads-dataset-types-num] The array of types MUST have the same number of elements that the columns array contains.";

    public final static String ID_PAYLOADS_DATASET_TYPES_TYPE = "payloads-dataset-types-type";
    public final static String PAYLOADS_DATASET_TYPES_TYPE = "[tck-id-payloads-dataset-types-type] The values in the types array MUST be a unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_DATASET_TYPES_VALUE = "payloads-dataset-types-value";
    public final static String PAYLOADS_DATASET_TYPES_VALUE = "[tck-id-payloads-dataset-types-value] This values in the types array MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types.";

    public final static String ID_PAYLOADS_DATASET_PARAMETER_TYPE_REQ = "payloads-dataset-parameter-type-req";
    public final static String PAYLOADS_DATASET_PARAMETER_TYPE_REQ = "[tck-id-payloads-dataset-parameter-type-req] The types array MUST be included in all DataSets.";

    // 6.4.12 DataSet.Row
    // 6.4.13 DataSet.DataSetValue
    public final static String ID_PAYLOADS_TEMPLATE_DATASET_VALUE = "payloads-template-dataset-value";
    public final static String PAYLOADS_TEMPLATE_DATASET_VALUE = "[tck-id-payloads-template-dataset-value] The value supplied MUST be one of the following Google Protobuf types: uint32, uint64, float, double, bool, or string.";

    // 6.4.14 Template
    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY = "payloads-template-definition-nbirth-only";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY = "[tck-id-payloads-template-definition-nbirth-only] Template Definitions MUST only be included in NBIRTH messages.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION = "payloads-template-definition-is-definition";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION = "[tck-id-payloads-template-definition-is-definition] A Template Definition MUST have is_definition set to true.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_REF = "payloads-template-definition-ref";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_REF = "[tck-id-payloads-template-definition-ref] A Template Definition MUST omit the template_ref field.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS = "payloads-template-definition-members";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_MEMBERS = "[tck-id-payloads-template-definition-members] A Template Definition MUST include all member metrics that will ever be included in corresponding template instances.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH = "payloads-template-definition-nbirth";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_NBIRTH = "[tck-id-payloads-template-definition-nbirth] A Template Definition MUST be included in the NBIRTH for all Template Instances that are included in the NBIRTH and DBIRTH messages.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS = "payloads-template-definition-parameters";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS = "[tck-id-payloads-template-definition-parameters] A Template Definition MUST include all parameters that will be included in the corresponding Template Instances.";

    public final static String ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT = "payloads-template-definition-parameters-default";
    public final static String PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT = "[tck-id-payloads-template-definition-parameters-default] A Template Definition MAY include values for parameters in the Template Definition parameters.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION = "payloads-template-instance-is-definition";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION = "[tck-id-payloads-template-instance-is-definition] A Template Instance MUST have is_definition set to false.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_REF = "payloads-template-instance-ref";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_REF = "[tck-id-payloads-template-instance-ref] A Template Instance MUST have template_ref set to the type of template definition it is.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS = "payloads-template-instance-members";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_MEMBERS = "[tck-id-payloads-template-instance-members] A Template Instance MUST include only members that were included in the corresponding template definition.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH = "payloads-template-instance-members-birth";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH = "[tck-id-payloads-template-instance-members-birth] A Template Instance in a NBIRTH or DBIRTH message MUST include all members that were included in the corresponding Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA = "payloads-template-instance-members-data";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA = "[tck-id-payloads-template-instance-members-data] A Template Instance in a NDATA or DDATA message MAY include only a subset of the members that were included in the corresponding template definition.";

    public final static String ID_PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS = "payloads-template-instance-parameters";
    public final static String PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS = "[tck-id-payloads-template-instance-parameters] A Template Instance MAY include parameter values for any parameters that were included in the corresponding Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_VERSION = "payloads-template-version";
    public final static String PAYLOADS_TEMPLATE_VERSION = "[tck-id-payloads-template-version] If included, the version MUST be a UTF-8 string representing the version of the Template.";

    public final static String ID_PAYLOADS_TEMPLATE_REF_DEFINITION = "payloads-template-ref-definition";
    public final static String PAYLOADS_TEMPLATE_REF_DEFINITION = "[tck-id-payloads-template-ref-definition] This MUST be omitted if this is a Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_REF_INSTANCE = "payloads-template-ref-instance";
    public final static String PAYLOADS_TEMPLATE_REF_INSTANCE = "[tck-id-payloads-template-ref-instance] This MUST be a UTF-8 string representing a reference to a Template Definition name if this is a Template Instance.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION = "payloads-template-is-definition";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION = "[tck-id-payloads-template-is-definition] This MUST be included in every Template Definition and Template Instance.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION = "payloads-template-is-definition-definition";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION = "[tck-id-payloads-template-is-definition-definition] This MUST be set to true if this is a Template Definition.";

    public final static String ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE = "payloads-template-is-definition-instance";
    public final static String PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE = "[tck-id-payloads-template-is-definition-instance] This MUST be set to false if this is a Template Instance.";

    // 6.4.15 Template.Parameter
    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED = "payloads-template-parameter-name-required";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED = "[tck-id-payloads-template-parameter-name-required] This MUST be included in every Template Parameter definition.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE = "payloads-template-parameter-name-type";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE = "[tck-id-payloads-template-parameter-name-type] This MUST be a UTF-8 string representing the name of the Template parameter.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE = "payloads-template-parameter-value-type";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE = "[tck-id-payloads-template-parameter-value-type] This MUST be an unsigned 32-bit integer representing the datatype.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE = "payloads-template-parameter-type-value";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE = "[tck-id-payloads-template-parameter-type-value] This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ = "payloads-template-parameter-type-req";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ = "[tck-id-payloads-template-parameter-type-req] This MUST be included in Template Parameter Definitions in NBIRTH and DBIRTH messages.";

    public final static String ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE = "payloads-template-parameter-value";
    public final static String PAYLOADS_TEMPLATE_PARAMETER_VALUE = "[tck-id-payloads-template-parameter-value] The value supplied MUST be one of the following Google Protocol Buffer types: uint32, uint64, float, double, bool, or string.";

    // 6.4.16 Data Types
    // 6.4.17 Datatype Details
    // 6.4.18 Payload Representation on Host Applications
    // 6.4.19 NBIRTH
    public final static String ID_PAYLOADS_NBIRTH_TIMESTAMP = "payloads-nbirth-timestamp";
    public final static String PAYLOADS_NBIRTH_TIMESTAMP = "[tck-id-payloads-nbirth-timestamp] NBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR = "payloads-nbirth-edge-node-descriptor";
    public final static String PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR = "[tck-id-payloads-nbirth-edge-node-descriptor] Every Edge Node Descriptor in any Sparkplug infrastructure MUST be unique in the system.";

    public final static String ID_PAYLOADS_NBIRTH_SEQ = "payloads-nbirth-seq";
    public final static String PAYLOADS_NBIRTH_SEQ = "[tck-id-payloads-nbirth-seq] Every NBIRTH message MUST include a sequence number and it MUST have a value between 0 and 255 (inclusive).";

    public final static String ID_PAYLOADS_NBIRTH_BDSEQ = "payloads-nbirth-bdseq";
    public final static String PAYLOADS_NBIRTH_BDSEQ = "[tck-id-payloads-nbirth-bdseq] Every NBIRTH message MUST include a bdSeq number metric.";

    public final static String ID_PAYLOADS_NBIRTH_BDSEQ_REPEAT = "payloads-nbirth-bdseq-repeat";
    public final static String PAYLOADS_NBIRTH_BDSEQ_REPEAT = "[tck-id-payloads-nbirth-bdseq-repeat] The bdSeq number value MUST match the bdSeq number value that was sent in the prior MQTT CONNECT packet WILL Message.";

    public final static String ID_PAYLOADS_NBIRTH_REBIRTH_REQ = "payloads-nbirth-rebirth-req";
    public final static String PAYLOADS_NBIRTH_REBIRTH_REQ = "[tck-id-payloads-nbirth-rebirth-req] Every NBIRTH MUST include a metric with the name 'Node Control/Rebirth' and have a boolean value of false.";

    public final static String ID_PAYLOADS_NBIRTH_QOS = "payloads-nbirth-qos";
    public final static String PAYLOADS_NBIRTH_QOS = "[tck-id-payloads-nbirth-qos] NBIRTH messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NBIRTH_RETAIN = "payloads-nbirth-retain";
    public final static String PAYLOADS_NBIRTH_RETAIN = "[tck-id-payloads-nbirth-retain] NBIRTH messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.20 DBIRTH
    public final static String ID_PAYLOADS_DBIRTH_TIMESTAMP = "payloads-dbirth-timestamp";
    public final static String PAYLOADS_DBIRTH_TIMESTAMP = "[tck-id-payloads-dbirth-timestamp] DBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DBIRTH_SEQ = "payloads-dbirth-seq";
    public final static String PAYLOADS_DBIRTH_SEQ = "[tck-id-payloads-dbirth-seq] Every DBIRTH message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DBIRTH_SEQ_INC = "payloads-dbirth-seq-inc";
    public final static String PAYLOADS_DBIRTH_SEQ_INC = "[tck-id-payloads-dbirth-seq-inc] Every DBIRTH message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DBIRTH_ORDER = "payloads-dbirth-order";
    public final static String PAYLOADS_DBIRTH_ORDER = "[tck-id-payloads-dbirth-order] All DBIRTH messages sent by an Edge Node MUST be sent immediately after the NBIRTH and before any NDATA or DDATA messages are published by the Edge Node.";

    public final static String ID_PAYLOADS_DBIRTH_QOS = "payloads-dbirth-qos";
    public final static String PAYLOADS_DBIRTH_QOS = "[tck-id-payloads-dbirth-qos] DBIRTH messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DBIRTH_RETAIN = "payloads-dbirth-retain";
    public final static String PAYLOADS_DBIRTH_RETAIN = "[tck-id-payloads-dbirth-retain] DBIRTH messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.21 NDATA
    public final static String ID_PAYLOADS_NDATA_TIMESTAMP = "payloads-ndata-timestamp";
    public final static String PAYLOADS_NDATA_TIMESTAMP = "[tck-id-payloads-ndata-timestamp] NDATA messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NDATA_SEQ = "payloads-ndata-seq";
    public final static String PAYLOADS_NDATA_SEQ = "[tck-id-payloads-ndata-seq] Every NDATA message MUST include a sequence number.";

    public final static String ID_PAYLOADS_NDATA_SEQ_INC = "payloads-ndata-seq-inc";
    public final static String PAYLOADS_NDATA_SEQ_INC = "[tck-id-payloads-ndata-seq-inc] Every NDATA message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_NDATA_ORDER = "payloads-ndata-order";
    public final static String PAYLOADS_NDATA_ORDER = "[tck-id-payloads-ndata-order] All NDATA messages sent by an Edge Node MUST NOT be sent until all the NBIRTH and all DBIRTH messages have been published by the Edge Node.";

    public final static String ID_PAYLOADS_NDATA_QOS = "payloads-ndata-qos";
    public final static String PAYLOADS_NDATA_QOS = "[tck-id-payloads-ndata-qos] NDATA messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NDATA_RETAIN = "payloads-ndata-retain";
    public final static String PAYLOADS_NDATA_RETAIN = "[tck-id-payloads-ndata-retain] NDATA messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.22 DDATA
    public final static String ID_PAYLOADS_DDATA_TIMESTAMP = "payloads-ddata-timestamp";
    public final static String PAYLOADS_DDATA_TIMESTAMP = "[tck-id-payloads-ddata-timestamp] DDATA messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DDATA_SEQ = "payloads-ddata-seq";
    public final static String PAYLOADS_DDATA_SEQ = "[tck-id-payloads-ddata-seq] Every DDATA message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DDATA_SEQ_INC = "payloads-ddata-seq-inc";
    public final static String PAYLOADS_DDATA_SEQ_INC = "[tck-id-payloads-ddata-seq-inc] Every DDATA message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DDATA_ORDER = "payloads-ddata-order";
    public final static String PAYLOADS_DDATA_ORDER = "[tck-id-payloads-ddata-order] All DDATA messages sent by an Edge Node MUST NOT be sent until all the NBIRTH and all DBIRTH messages have been published by the Edge Node.";

    public final static String ID_PAYLOADS_DDATA_QOS = "payloads-ddata-qos";
    public final static String PAYLOADS_DDATA_QOS = "[tck-id-payloads-ddata-qos] DDATA messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DDATA_RETAIN = "payloads-ddata-retain";
    public final static String PAYLOADS_DDATA_RETAIN = "[tck-id-payloads-ddata-retain] DDATA messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.23 NCMD
    public final static String ID_PAYLOADS_NCMD_TIMESTAMP = "payloads-ncmd-timestamp";
    public final static String PAYLOADS_NCMD_TIMESTAMP = "[tck-id-payloads-ncmd-timestamp] NCMD messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_NCMD_SEQ = "payloads-ncmd-seq";
    public final static String PAYLOADS_NCMD_SEQ = "[tck-id-payloads-ncmd-seq] Every NCMD message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_NCMD_QOS = "payloads-ncmd-qos";
    public final static String PAYLOADS_NCMD_QOS = "[tck-id-payloads-ncmd-qos] NCMD messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_NCMD_RETAIN = "payloads-ncmd-retain";
    public final static String PAYLOADS_NCMD_RETAIN = "[tck-id-payloads-ncmd-retain] NCMD messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.24 DCMD
    public final static String ID_PAYLOADS_DCMD_TIMESTAMP = "payloads-dcmd-timestamp";
    public final static String PAYLOADS_DCMD_TIMESTAMP = "[tck-id-payloads-dcmd-timestamp] DCMD messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DCMD_SEQ = "payloads-dcmd-seq";
    public final static String PAYLOADS_DCMD_SEQ = "[tck-id-payloads-dcmd-seq] Every DCMD message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_DCMD_QOS = "payloads-dcmd-qos";
    public final static String PAYLOADS_DCMD_QOS = "[tck-id-payloads-dcmd-qos] DCMD messages MUST be published with the MQTT QoS set to 0.";

    public final static String ID_PAYLOADS_DCMD_RETAIN = "payloads-dcmd-retain";
    public final static String PAYLOADS_DCMD_RETAIN = "[tck-id-payloads-dcmd-retain] DCMD messages MUST be published with the MQTT retain flag set to false.";

    // 6.4.25 NDEATH
    public final static String ID_PAYLOADS_NDEATH_SEQ = "payloads-ndeath-seq";
    public final static String PAYLOADS_NDEATH_SEQ = "[tck-id-payloads-ndeath-seq] Every NDEATH message MUST NOT include a sequence number.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE = "payloads-ndeath-will-message";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE = "[tck-id-payloads-ndeath-will-message] An NDEATH message MUST be registered as a Will Message in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "payloads-ndeath-will-message-qos";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "[tck-id-payloads-ndeath-will-message-qos] The NDEATH message MUST set the MQTT Will QoS to 1 in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "payloads-ndeath-will-message-retain";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "[tck-id-payloads-ndeath-will-message-retain] The NDEATH message MUST set the MQTT Will Retained flag to false in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_BDSEQ = "payloads-ndeath-bdseq";
    public final static String PAYLOADS_NDEATH_BDSEQ = "[tck-id-payloads-ndeath-bdseq] The NDEATH message MUST include the same bdSeq number value that will be used in the associated NBIRTH message.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER = "payloads-ndeath-will-message-publisher";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER = "[tck-id-payloads-ndeath-will-message-publisher] An NDEATH message SHOULD be published by the Edge Node before it intentionally disconnects from the MQTT Server.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311 = "payloads-ndeath-will-message-publisher-disconnect-mqtt311";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311 = "[tck-id-payloads-ndeath-will-message-publisher-disconnect-mqtt311] If the Edge Node is using MQTT 3.1.1 and it sends an MQTT DISCONNECT packet, the Edge Node MUST publish an NDEATH message to the MQTT Server before it sends the MQTT DISCONNECT packet.";

    public final static String ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50 = "payloads-ndeath-will-message-publisher-disconnect-mqtt50";
    public final static String PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50 = "[tck-id-payloads-ndeath-will-message-publisher-disconnect-mqtt50] If the Edge Node is using MQTT 5.0 and it sends an MQTT DISCONNECT packet, the MQTT v5.0 'Disconnect with Will Message' reason code MUST be set in the DISCONNECT packet.";

    // 6.4.26 DDEATH
    public final static String ID_PAYLOADS_DDEATH_TIMESTAMP = "payloads-ddeath-timestamp";
    public final static String PAYLOADS_DDEATH_TIMESTAMP = "[tck-id-payloads-ddeath-timestamp] DDEATH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public final static String ID_PAYLOADS_DDEATH_SEQ = "payloads-ddeath-seq";
    public final static String PAYLOADS_DDEATH_SEQ = "[tck-id-payloads-ddeath-seq] Every DDEATH message MUST include a sequence number.";

    public final static String ID_PAYLOADS_DDEATH_SEQ_INC = "payloads-ddeath-seq-inc";
    public final static String PAYLOADS_DDEATH_SEQ_INC = "[tck-id-payloads-ddeath-seq-inc] Every DDEATH message MUST include a sequence number value that is one greater than the previous sequence number sent by the Edge Node. This value MUST never exceed 255. If in the previous sequence number sent by the Edge Node was 255, the next sequence number sent MUST have a value of 0.";

    public final static String ID_PAYLOADS_DDEATH_SEQ_NUMBER = "payloads-ddeath-seq-number";
    public final static String PAYLOADS_DDEATH_SEQ_NUMBER = "[tck-id-payloads-ddeath-seq-number] A sequence number MUST be included with the DDEATH messages so the Host Application can ensure order of messages and maintain the state of the data.";

    // 6.4.27 STATE
    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE = "payloads-state-will-message";
    public final static String PAYLOADS_STATE_WILL_MESSAGE = "[tck-id-payloads-state-will-message] Sparkplug Host Applications MUST register a Will Message in the MQTT CONNECT packet on the topic 'spBv1.0/STATE/[sparkplug_host_id]'.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_QOS = "payloads-state-will-message-qos";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_QOS = "[tck-id-payloads-state-will-message-qos] The Sparkplug Host Application MUST set the the MQTT Will QoS to 1 in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN = "payloads-state-will-message-retain";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_RETAIN = "[tck-id-payloads-state-will-message-retain] The Sparkplug Host Application MUST set the Will Retained flag to true in the MQTT CONNECT packet.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD = "payloads-state-will-message-payload";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD = "[tck-id-payloads-state-will-message-payload] The Death Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'false'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ = "payloads-state-will-message-payload-bdseq";
    public final static String PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ = "[tck-id-payloads-state-will-message-payload-bdseq] The Death Certificate's bdSeq number value MUST have a value of one more than the bdSeq number value sent in the prior MQTT CONNECT packet from the Host Application unless the previous value was 255. In this case the new bdSeq number value MUST be 0.";

    public final static String ID_PAYLOADS_STATE_SUBSCRIBE = "payloads-state-subscribe";
    public final static String PAYLOADS_STATE_SUBSCRIBE = "[tck-id-payloads-state-subscribe] After establishing an MQTT connection, the Sparkplug Host Application MUST subscribe on it's own 'spBv1.0/STATE/[sparkplug_host_id]' topic.";

    public final static String ID_PAYLOADS_STATE_BIRTH = "payloads-state-birth";
    public final static String PAYLOADS_STATE_BIRTH = "[tck-id-payloads-state-birth] After subscribing on it's own spBv1.0/STATE/[sparkplug_host_id] topic, the Sparkplug Host Application MUST publish an MQTT message on the topic 'spBv1.0/STATE/[sparkplug_host_id]' with a QoS of 1, and the retain flag set to true.";

    public final static String ID_PAYLOADS_STATE_BIRTH_PAYLOAD = "payloads-state-birth-payload";
    public final static String PAYLOADS_STATE_BIRTH_PAYLOAD = "[tck-id-payloads-state-birth-payload] The Birth Certificate Payload MUST be JSON UTF-8 data. It MUST include three key/value pairs where the one key MUST be 'online' and it's value is a boolean 'true'. Another key MUST be 'bdSeq' and have a numeric value between 0 and 255 (inclusive). The final key MUST be 'timestamp' and the value MUST be a numeric value representing the current UTC time in milliseconds since Epoch.";

    public final static String ID_PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ = "payloads-state-birth-payload-bdseq";
    public final static String PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ = "[tck-id-payloads-state-birth-payload-bdseq] The bdSeq metric value MUST be be the same value set in the immediately prior MQTT CONNECT packet's Will Message payload.";

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
    public final static String CONFORMANCE_PRIMARY_HOST = "[tck-id-conformance-primary-host] Sparkplug Host Applications MUST publish 'STATE' messages that represent its Birth and Death Certificates.";

    // 10.1.3 Sparkplug Compliant MQTT Server
    public final static String ID_CONFORMANCE_MQTT_QOS0 = "conformance-mqtt-qos0";
    public final static String CONFORMANCE_MQTT_QOS0 = "[tck-id-conformance-mqtt-qos0] A Sparkplug conformant MQTT Server MUST support publish and subscribe on QoS 0";

    public final static String ID_CONFORMANCE_MQTT_QOS1 = "conformance-mqtt-qos1";
    public final static String CONFORMANCE_MQTT_QOS1 = "[tck-id-conformance-mqtt-qos1] A Sparkplug conformant MQTT Server MUST support publish and subscribe on QoS 1";

    public final static String ID_CONFORMANCE_MQTT_WILL_MESSAGES = "conformance-mqtt-will-messages";
    public final static String CONFORMANCE_MQTT_WILL_MESSAGES = "[tck-id-conformance-mqtt-will-messages] A Sparkplug conformant MQTT Server MUST support all aspects of Will Messages including use of the 'retain flag' and QoS 1";

    public final static String ID_CONFORMANCE_MQTT_RETAINED = "conformance-mqtt-retained";
    public final static String CONFORMANCE_MQTT_RETAINED = "[tck-id-conformance-mqtt-retained] A Sparkplug conformant MQTT Server MUST support all aspects of the 'retain flag'";

    // 10.1.4 Sparkplug Aware MQTT Server
    public final static String ID_CONFORMANCE_MQTT_AWARE_BASIC = "conformance-mqtt-aware-basic";
    public final static String CONFORMANCE_MQTT_AWARE_BASIC = "[tck-id-conformance-mqtt-aware-basic] A Sparkplug Aware MQTT Server MUST support all aspects of a Sparkplug Compliant MQTT Server";

    public final static String ID_CONFORMANCE_MQTT_AWARE_STORE = "conformance-mqtt-aware-store";
    public final static String CONFORMANCE_MQTT_AWARE_STORE = "[tck-id-conformance-mqtt-aware-store] A Sparkplug Aware MQTT Server MUST store NBIRTH and DBIRTH messages as they pass through the MQTT Server";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC = "conformance-mqtt-aware-nbirth-mqtt-topic";
    public final static String CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_TOPIC = "[tck-id-conformance-mqtt-aware-nbirth-mqtt-topic] A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on a topic of the form: $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN = "conformance-mqtt-aware-nbirth-mqtt-retain";
    public final static String CONFORMANCE_MQTT_AWARE_NBIRTH_MQTT_RETAIN = "[tck-id-conformance-mqtt-aware-nbirth-mqtt-retain] A Sparkplug Aware MQTT Server MUST make NBIRTH messages available on the topic: $sparkplug/certificates/namespace/group_id/NBIRTH/edge_node_id with the MQTT retain flag set to true";

    public final static String ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC = "conformance-mqtt-aware-dbirth-mqtt-topic";
    public final static String CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_TOPIC = "[tck-id-conformance-mqtt-aware-dbirth-mqtt-topic] A Sparkplug Aware MQTT Server MUST make DBIRTH messages available on a topic of the form: $sparkplug/certificates/namespace/group_id/DBIRTH/edge_node_id/device_id";

    public final static String ID_CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN = "conformance-mqtt-aware-dbirth-mqtt-retain";
    public final static String CONFORMANCE_MQTT_AWARE_DBIRTH_MQTT_RETAIN = "[tck-id-conformance-mqtt-aware-dbirth-mqtt-retain] A Sparkplug Aware MQTT Server MUST make DBIRTH messages available on the topic: $sparkplug/certificates/namespace/group_id/DBIRTH/edge_node_id/device_id with the MQTT retain flag set to true";

    public final static String ID_CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP = "conformance-mqtt-aware-ndeath-timestamp";
    public final static String CONFORMANCE_MQTT_AWARE_NDEATH_TIMESTAMP = "[tck-id-conformance-mqtt-aware-ndeath-timestamp] A Sparkplug Aware MQTT Server MAY replace the timestmap of NDEATH messages. If it does, it MUST set the timestamp to the UTC time at which it attempts to deliver the NDEATH to subscribed clients";

    // 11 Appendix A: Open Source Software (non-normative)
    // 11.1 OASIS MQTT Specifications
    // 11.2 Eclipse Foundation IoT Resources
    // 11.3 Eclipse Paho
    // 11.4 Google Protocol Buffers
    // 11.5 Eclipse Kura Google Protocol Buffer Schema
    // 11.6 Raspberry Pi Hardware
    // 12 Appendix B: List of Normative Statements (non-normative)
}
// no of assertions 299
