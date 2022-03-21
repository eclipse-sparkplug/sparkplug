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

    //@SpecAssertions works only with constants like string but not enum or arrays

    //Section 6.4.5 - Payload [payloads_b_payload]
    // Section.PAYLOADS_B_PAYLOAD
    public final static String ID_PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED = "payloads-sequence-num-always-included";
    public final static String PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED = "A sequence number MUST be included in the payload of every Sparkplug MQTT message except NDEATH messages.";

    public final static String ID_PAYLOAD_SEQUENCE_NUM_ZERO_NBIRTH = "payloads-sequence-num-zero-nbirth";
    public final static String PAYLOAD_SEQUENCE_NUM_ZERO_NBIRTH = "A NBIRTH message MUST always contain a sequence number of zero.";

    public static final String ID_PAYLOADS_NAME_CMD_REQUIREMENT = "payloads-name-cmd-requirement";
    public static final String PAYLOADS_NAME_CMD_REQUIREMENT = "The timestamp MAY be included with metrics in NCMD and DCMD messages.";

    //TODO
    public static final String PAYLOAD_SEQUENCE_NUM_INCREMENTING = "All subsequent messages MUST contain a sequence number that is continually increasing by one in each message until a value of 255 is reached. At that point, the sequence number of the following message MUST be zero.";


    // Section 6.4.6 Metric [payloads_b_metric]
    // Section.PAYLOADS_B_METRIC
    public final static String ID_PAYLOADS_NAME_REQUIREMENT = "payloads-name-requirement";
    public final static String PAYLOADS_NAME_REQUIREMENT = "The name MUST be included with every metric unless aliases are being used.";

    public final static String ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "payloads-alias-birth-requirement";
    public static final String PAYLOADS_ALIAS_BIRTH_REQUIREMENT = "NBIRTH and DBIRTH messages MUST include both a metric name and alias.";

    public final static String ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "payloads-alias-data-cmd-requirement";
    public static final String PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT = "NDATA, DDATA, NCMD, and DCMD messages MUST only include an alias and the metric name MUST be excluded.";

    public static final String ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "payloads-name-birth-data-requirement";
    public static final String PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT = "The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.";

    public static final String ID_PAYLOADS_METRIC_DATATYPE_REQ = "payloads-metric-datatype-req";
    public static final String PAYLOADS_METRIC_DATATYPE_REQ = "The datatype MUST be included with each metric definition in NBIRTH and DBIRTH messages.";

    public static final String ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ = "payloads-metric-datatype-not-req";
    public static final String PAYLOADS_METRIC_DATATYPE_NOT_REQ = "The datatype SHOULD NOT be included with metric definitions in NDATA, NCMD, DDATA, and DCMD messages.";


    public static final String ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "payloads-metric-datatype-value-type";
    public static final String PAYLOADS_METRIC_DATATYPE_VALUE_TYPE = "The datatype MUST be an unsigned 32-bit integer representing the datatype.";

    public static final String ID_PAYLOADS_METRIC_DATATYPE_VALUE = "payloads-metric-datatype-value";
    public static final String PAYLOADS_METRIC_DATATYPE_VALUE = "The datatype MUST be one of the enumerated values as shown in the valid Sparkplug Data Types.";

    //TODO
    public static final String PAYLOADS_ALIAS_UNIQUENESS = "If supplied in an NBIRTH or DBIRTH it MUST be a unique number across this Edge Node's entire set of metrics.";


    // payloads_b_propertyset
    // @Sections.PAYLOADS_B_PROPERTYSET
    public static final String ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "payloads-propertyset-keys-array-size";
    public static final String PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE = "The array of keys in a PropertySet MUST contain the same number of values included in the array of PropertyValue objects.";
    public static final String ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "payloads-propertyset-values-array-size";
    public static final String PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE = "The array of values in a PropertySet MUST contain the same number of items that are in the keys array.";

    // Section 6.4.9 - PropertyValue [payloads_b_propertyvalue]
    // @Sections.PAYLOADS_B_PROPERTYVALUE
    public static final String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "payloads-metric-propertyvalue-type-type";
    public static final String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE = "This MUST be an unsigned 32-bit integer representing the datatype.";

    public static final String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "payloads-metric-propertyvalue-type-value";
    public static final String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE = "This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types or the Sparkplug Property Value Data Types.";

    public static final String ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "payloads-metric-propertyvalue-type-req";
    public static final String PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ = "This MUST be included in Property Value Definitions in NBIRTH and DBIRTH messages.";


    //Sparkplug_4_Topics Section [payloads_desc_nbirth]
    // @Sections.PAYLOADS_DESC_NBIRTH
    public static final String ID_TOPICS_NBIRTH_MQTT = "topics-nbirth-mqtt";
    public static final String TOPICS_NBIRTH_MQTT = "NBIRTH messages MUST be published";

    public static final String ID_TOPICS_NBIRTH_SEQ_NUM = "topics-nbirth-seq-num";
    public static final String TOPICS_NBIRTH_SEQ_NUM = "The NBIRTH MUST include a sequence number in the payload and it MUST have a value of 0";

    public static final String ID_TOPICS_NBIRTH_TIMESTAMP = "topics-nbirth-timestamp";
    public static final String TOPICS_NBIRTH_TIMESTAMP = "The NBIRTH MUST include a timestamp denoting the Date/Time the message was sent from the Edge Node.";

    public static final String ID_TOPICS_NBIRTH_METRICS = "topics-nbirth-metrics";
    public static final String TOPICS_NBIRTH_METRICS = "At a minimum each metric MUST include the following.";

    public static final String ID_TOPICS_NBIRTH_BDSEQ_INCLUDED = "topics-nbirth-bdseq-included";
    public static final String TOPICS_NBIRTH_BDSEQ_INCLUDED = "A bdSeq number as a metric MUST be included in the payload.";

    public static final String ID_TOPICS_NBIRTH_BDSEQ_MATCHING = "topics-nbirth-bdseq-matching";
    public static final String TOPICS_NBIRTH_BDSEQ_MATCHING = "This MUST match the bdSeq number provided in the MQTT CONNECT packet’s Will Message payload.";

    public static final String ID_TOPICS_NBIRTH_REBIRTH_METRIC = "topics-nbirth-rebirth-metric";
    public static final String TOPICS_NBIRTH_REBIRTH_METRIC = "The NBIRTH message MUST include the metric 'node control/rebirth'";

    // Sections.PAYLOADS_DESC_DBIRTH
    public static final String ID_TOPICS_DBIRTH_METRICS = "topics-dbirth-metrics";
    public static final String TOPICS_DBIRTH_METRICS = "At a minimum each metric MUST include the following.";
    // Sparkplug_2_principles_birth_and_death_certificates Section [principles_birth_and_death_certificates]
    // Sections.PRINCIPLES_BIRTH_AND_DEATH_CERTIFICATES,
    public static final String ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "principles-birth-certificates-order";

    public static final String PRINCIPLES_BIRTH_CERTIFICATES_ORDER = "Birth certificates must be first";

    public static final String ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION = "principles-persistence-clean-session";
    public static final String PRINCIPLES_PERSISTENCE_CLEAN_SESSION = "Clean session should be set to true.";

    // Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH
    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "operational-behavior-data-publish-nbirth-values";
    public static final String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES = "NBIRTH messages MUST include current values for all metrics.";

    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "operational-behavior-data-publish-dbirth-values";
    public static final String OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES = "DBIRTH messages MUST include current values for all metrics.";


    // Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "operational-behavior-data-commands-rebirth-name";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME = "An NBIRTH message MUST include a metric with a name of 'Node Control/Rebirth'.";

    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "operational-behavior-data-commands-rebirth-datatype";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE = "The'Node Control/Rebirth' metric in the NBIRTH message MUST have a datatype of 'Boolean'.";

    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "operational-behavior-data-commands-rebirth-value";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE = "NBIRTH 'node control/rebirth' metric must == false";

    public static final String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_SUBSCRIBE = "message-flow-edge-node-birth-publish-subscribe";
    public static final String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_SUBSCRIBE = "Any Edge Node " +
            "in the MQTT infrastructure MUST verify the Primary Host Application is ONLINE via the STATE topic if a Primary Host Application is configured for the Edge Node before publishing NBIRTH and DBIRTH messages.";

    public static final String ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "message-flow-edge-node-birth-publish-connect";
    public static final String MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT = "Any Edge Node in the MQTT infrastructure MUST establish an MQTT Session prior to publishing NBIRTH and DBIRTH messages.";


    // Sparkplug_6_Payloads [[payloads_b_nbirth]
    // Sections.PAYLOADS_B_NBIRTH,
    public static final String ID_PAYLOADS_NBIRTH_RETAIN = "payloads-nbirth-retain";
    public static final String PAYLOADS_NBIRTH_RETAIN = "NBIRTH retained flag must be false";

    public static final String ID_PAYLOADS_NBIRTH_QOS = "payloads-nbirth-qos";
    public static final String PAYLOADS_NBIRTH_QOS = "NBIRTH message must have Qos set to 0.";

    public static final String ID_PAYLOADS_NBIRTH_SEQ = "payloads-nbirth-seq";
    public static final String PAYLOADS_NBIRTH_SEQ = "Every NBIRTH message MUST include a sequence number and it MUST have a value of 0.";

    public static final String ID_PAYLOADS_NBIRTH_TIMESTAMP = "payloads-nbirth-timestamp";
    public static final String PAYLOADS_NBIRTH_TIMESTAMP = "NBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.";

    public static final String ID_PAYLOADS_NBIRTH_REBIRTH_REQ = "payloads-nbirth-rebirth-req";
    public static final String PAYLOADS_NBIRTH_REBIRTH_REQ = "NBIRTH must include a 'Node Control/Rebirth' metric";

    public static final String ID_PAYLOADS_NBIRTH_BDSEQ = "payloads-nbirth-bdseq";
    public static final String PAYLOADS_NBIRTH_BDSEQ = "NBIRTH must include a bdSeq";

    public static final String ID_PAYLOADS_NDEATH_BDSEQ = "payloads-ndeath-bdseq";
    public static final String PAYLOADS_NDEATH_BDSEQ = "NBIRTH bdSeq must match bdSeq provided in Will Message payload of connect packet";

    // Sparkplug_6_Payloads [[payloads_b_dbirth]
    // Section payloads_b_dbirth
    public static final String ID_PAYLOADS_DBIRTH_QOS = "payloads-dbirth-qos";
    public static final String PAYLOADS_DBIRTH_QOS = "DBIRTH message must have Qos set to 0";

    public static final String ID_PAYLOADS_DBIRTH_RETAIN = "payloads-dbirth-retain";
    public static final String PAYLOADS_DBIRTH_RETAIN = "DBIRTH retained flag must be false";

    public static final String ID_TOPICS_DBIRTH_MQTT = "topics-dbirth-mqtt";
    public static final String TOPICS_DBIRTH_MQTT = "DBIRTH Qos must be 0 and retained must be false";


    public static final String ID_TOPICS_DBIRTH_TIMESTAMP = "topics-dbirth-timestamp";
    public static final String TOPICS_DBIRTH_TIMESTAMP = "DBIRTH must include payload timestamp that denotes the time at which the message was published";

    public static final String ID_PAYLOADS_DBIRTH_TIMESTAMP = "payloads-dbirth-timestamp";
    public static final String PAYLOADS_DBIRTH_TIMESTAMP = TOPICS_DBIRTH_TIMESTAMP;

    public static final String ID_PAYLOADS_DBIRTH_SEQ = "payloads-dbirth-seq";
    public static final String PAYLOADS_DBIRTH_SEQ = "DBIRTH must include a sequence number";

    public static final String ID_TOPICS_DBIRTH_SEQ = "topics-dbirth-seq";
    public static final String TOPICS_DBIRTH_SEQ =
            "DBIRTH sequence number must have a value of one greater than the previous MQTT message from the"
                    + "edge node unless the previous MQTT message contained a value of 255; in this case, sequence number must be 0.";

    public static final String ID_PAYLOADS_DBIRTH_SEQ_INC = "payloads-dbirth-seq-inc";
    public static final String PAYLOADS_DBIRTH_SEQ_INC = TOPICS_DBIRTH_SEQ;

    public static final String ID_PAYLOADS_DBIRTH_ORDER = "payloads-dbirth-order";
    public static final String PAYLOADS_DBIRTH_ORDER = "DBIRTH must be sent before any NDATA/DDATA messages are published by the edge node";


    // Sparkplug_6_Payloads [[payloads_b_ndeath]
    // Section payloads_b_ndeath
    public static final String ID_TOPICS_NDEATH_SEQ = "topics-ndeath-seq";
    public static final String TOPICS_NDEATH_SEQ = "NDEATH must not include a sequence number";

    public static final String ID_PAYLOADS_NDEATH_SEQ = "payloads-ndeath-seq";
    public static final String PAYLOADS_NDEATH_SEQ = TOPICS_NDEATH_SEQ;

    public static final String ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "payloads-ndeath-will-message-qos";
    public static final String PAYLOADS_NDEATH_WILL_MESSAGE_QOS = "NDEATH message must have Qos set to 1";

    public static final String ID_TOPICS_NDEATH_PAYLOAD = "topics-ndeath-payload";
    public static final String TOPICS_NDEATH_PAYLOAD = "NDEATH payload must only include a single metric, the bdSeq number";

    public static final String ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "payloads-ndeath-will-message-retain";
    public static final String PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN = "NDEATH retained flag must be false";

    public static final String ID_PAYLOADS_NDEATH_WILL_MESSAGE = "payloads-ndeath-will-message";
    public static final String PAYLOADS_NDEATH_WILL_MESSAGE = "NDEATH not registered as Will in connect packet";


    // not found in specification ???
    public static final String ID_EDGE_SUBSCRIBE_NCMD = "edge-subscribe-ncmd";
    public static final String EDGE_SUBSCRIBE_NCMD =
            "Edge node should subscribe to NCMD level topics to ensure Edge node targeted message from the primary host application are delivered";

    public static final String ID_EDGE_SUBSCRIBE_DCMD = "edge-subscribe-dcmd";
    public static final String EDGE_SUBSCRIBE_DCMD = "Edge node should subscribe to DCMD level topics to ensure device targeted message from the primary host application are delivered";


    // Section 6.4.9.1 - Quality Codes [payloads_b_quality_codes]
    public static final String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "payloads-propertyset-quality-value-type";
    public static final String PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE = "The 'type' of the Property Value MUST be a value of 3 which represents a Signed 32-bit Integer.";

    public static final String ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "payloads-propertyset-quality-value-value";
    public static final String PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE = "The 'value' of the Property Value MUST be an int_value and be one of the valid quality codes of 0, 192, or 500.";


    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "operational-behavior-data-commands-rebirth-action-1";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1 = "When an Edge Node receives a Rebirth Request, it MUST immediately stop sending DATA messages.";

    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "operational-behavior-data-commands-rebirth-action-2";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2 = "After an Edge Node stops sending DATA messages, it MUST send a complete BIRTH sequence including the NBIRTH and DBIRTH(s) if applicable.";

    public static final String ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "operational-behavior-data-commands-rebirth-action-3";
    public static final String OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3 = "The NBIRTH MUST include the same bdSeq metric with the same value it had included in the Will Message of the previous MQTT CONNECT packet.";
}