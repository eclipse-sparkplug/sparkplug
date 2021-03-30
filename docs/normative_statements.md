## Definitions
* The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in RFC 2119. RFC 2119: https://tools.ietf.org/html/rfc2119

## Sparkplug Topics
* All Sparkplug topics MUST be of the following forms
	* [Namespace Token]/[Group ID]/[Sparkplug Verb]/[Edge Node ID]
	* [Namespace Token]/[Group ID]/[Sparkplug Verb]/[Edge Node ID]/[Device ID]
	* STATE/[Primary Host ID]

## Sparkplug Topic Tokens
* Primary Host ID: The ASCII identifier for the primary host client.
* Namespace Token: The first MQTT topic token MUST always be spAv1.0 or spBv1.0 with the exception of STATE messages. This denotes the payload encoding.
* Group ID: This MUST be included as the second topic token for every non-STATE topic
* Edge Node ID: This MUST be included as the fourth topic token for every non-STATE topic
* Device ID: This MUST be included as the fifth topic token for any non-STATE message where the Sparkplug Verb is DBIRTH, DDEATH, DDATA, or DCMD. It MUST not be included if the Sparkplug Verb is NBIRTH, NDEATH, NDATA, or NCMD.
* Sparkplug Verb: This MUST be included as the third topic token for every non-STATE topic and MUST be one of the following: NBIRTH, DBIRTH, NDEATH, DDEATH, NDATA, DDATA, NCMD, or DCMD.

## Sparkplug Host Client
* MUST subscribe to NBIRTH and NDEATH messages
* SHOULD subscribe to DBIRTH, DDEATH, NDATA, and DDATA messages
* MAY publish NCMD and DCMD messages to the MQTT Server
* MAY publish STATE messages - If it does, it is a 'Sparkplug Primary Host Client' and MUST follow the rules of the STATE topics

## Sparkplug Primary Host Client
* There MUST not be more than one Sparkplug Primary Host Client connected to any MQTT Server
* An MQTT 'Will Message' must be registered with the STATE topic. It MUST have a payload with the ASCII string 'OFFLINE', use QoS1, and MUST set the MQTT retain flag to true
* The STATE message MUST be published after the MQTT CONNACK packet is received with a 'Connection Accepted' response. The payload MUST be an ASCII string with the value of 'ONLINE', it MUST use QoS1, and MUST set the MQTT retain flag to true.

## Sparkplug Edge Client
* MUST publish an NBIRTH message after connecting to the MQTT Server and before publishing any other messages
* MUST register an MQTT Will topic with the topic '[Namespace Token]/[Group ID]/NDEATH/[Edge Node ID]', MQTT retain=false, and MQTT QoS=0. It MUST also include a non-null payload with a metric with name=bdSeq and a value that matches the 
pending bdSeq number metric that will be published in pending NBIRTH message
* SHOULD publish DBIRTH, NDATA, NDEATH, DDEATH, and DDATA messages
* MAY subscribe to NCMD and DCMD messages
* MAY subscribe to STATE messages
* Each Sparkplug edge client in the infrastructure MUST have a unique combination of Sparkplug Group ID and Edge Node ID

## Sparkplug Client
* This is any Sparkplug Edge Client, Sparkplug Host Client, or Sparkplug Primary Host Client

## Payloads
* When using the spBv1.0 'Namespace Token' in the topic the payload MUST be Google Protobuf encoded and use the protofile from here: https://github.com/eclipse/tahu/blob/master/sparkplug_b/sparkplug_b.proto
* LOTS MORE TO ADD HERE

## Quality of Service (QoS)
* All STATE messages published by Sparkplug Primary Host Clients MUST be published on Q0S1 including the MQTT Will message that is registered in the MQTT CONNECT packet
* All non-STATE messages from any Sparkplug Client MUST be published on QoS0

## Retained Messages
* All STATE messages MUST be published with the MQTT 'retain flag' set to true
* All non-STATE messages MUST be published with the MQTT 'retain flag' set to false

## MQTT Will Messages
* Sparkplug Primary Host Clients MUST register an MQTT Will message with the topic 'STATE/[Primary Host ID]', a payload of an ASCII string 'OFFLINE', MQTT retain=true, and QoS=1
* Sparkplug Host Clients that are not the Sparkplug Primary Host Clients MUST NOT register an MQTT Will message
* Sparkplug Edge Clients MUST register an MQTT Will message with the topic: '[Namespace Token]/[Group ID]/[Sparkplug Verb]/[Edge Node ID]'
	* MORE TO ADD HERE

## Clean Session
* The MQTT clean session flag MUST always be set to true for all Sparkplug clients
