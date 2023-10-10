# Sparkplug v4.0.0 Features

This document summarizes the new features, specification bugs, and TCK improvements targeted for the v4.0.0 release of Sparkplug. Questions asked by community members will also impact the specification and are listed here as well. The link found in the first column of each of the tables below leads to the GitHub issue where the feature, question, or bug was discussed.

The official v4.0.0 scope adopted by the Sparkplug specification committee was originally found in this document: [https://docs.google.com/spreadsheets/d/1SI-gzA3ofd9DMsQwfX3gnpbNPZ95A7FTAooTdZP1iYQ)](https://docs.google.com/spreadsheets/d/1SI-gzA3ofd9DMsQwfX3gnpbNPZ95A7FTAooTdZP1iYQ) 

## Features
| GitHub Issue | Summary | Assignee | Status | Epic | Notes | Open Questions/Concerns |
| ------------ | ------- | -------- | ------ | ---- | ----- | ----------------------- |
| [#61](https://github.com/eclipse-sparkplug/sparkplug/issues/61) | Support for an array of basic types |  | Include |   | Already sort of supported - limited today and needs improvement - will be included in 4.0.0 |
| [#62](https://github.com/eclipse-sparkplug/sparkplug/issues/62) | Make use of more built-in protobuf scalar types |  | Include |  | Seems like a reasonable recommendation - will be included in 4.0.0 |
| [#96](https://github.com/eclipse-sparkplug/sparkplug/issues/96) | Consider/design Sparkplug modifications to support Secondary Host Applications |  | Include |   | Will be included in 4.0.0 |
| [#97](https://github.com/eclipse-sparkplug/sparkplug/issues/97) | Add support for historical data messages to be published on QoS1 |  | Exclude | | Will be included in 4.0.0 - also need to increase usage of the sequence numbers to be greater than 255 \* Further discussion - its too dangerous to allow. This is going to be exlcuded | Should we consider QoS1 for all messages? \* Further discussion - No. Not using QoS1 at all |
| [#104](https://github.com/eclipse-sparkplug/sparkplug/issues/104) | Add Engineering units metadata field for metrics |  | Include |   | Need to define a list (or find one) and allow free form as well | Undecided for now |
| [#114](https://github.com/eclipse-sparkplug/sparkplug/issues/114) | Missing or incorrect definitions for Metric message |  | Dup | | Dup of #61 |
| [#124](https://github.com/eclipse-sparkplug/sparkplug/issues/124) | Allow individual metrics to reuse the payload timestamp instead of including their own |  | Include | | Will be included in 4.0.0
| [#130](https://github.com/eclipse-sparkplug/sparkplug/issues/130) | Template defs in DBIRTH messages |  | Exclude |  | Won't do this in 4.0.0 but consider the option of 'partial birth' option to dynamically introduce new/changed edge data |
| [#145](https://github.com/eclipse-sparkplug/sparkplug/issues/145) | Add concept of 'quality context' to the spec | Wes Johnson | Include |  | Will be included but needs better definition | Maybe allow for user defined codes. Also potentially allow for mappings of various codes |
| [#172](https://github.com/eclipse-sparkplug/sparkplug/issues/172) | Add build automation to the Sparkplug spec and TCK which supports posting of milestones |  | Include |  | Jenkins is here: [https://ci.eclipse.org/sparkplug/](https://ci.eclipse.org/sparkplug/) |
| [#174](https://github.com/eclipse-sparkplug/sparkplug/issues/174) | Clarify Edge Node/MQTT enabled devices in the spec | Nathan Davenport | Undecided |  | We will look to address this if it is still unclear |
| [#185](https://github.com/eclipse-sparkplug/sparkplug/issues/185) | Incorrect datatype in payload examples |  | Include |  | We will include this but ensure we are consistent throughout. Maybe requires more description and/or code sample links in the spec. Maybe include a '\*' on every JSON doc to denote this is not how the data is actually represented in Sparkplug |
| [#196](https://github.com/eclipse-sparkplug/sparkplug/issues/196) | Discourage or disallow additional unicode characters | Wes Johnson | Include |  | Include a application note or statement denoting this may need to be handled on a application specific basis (e.g. character encoding) |
| [#204](https://github.com/eclipse-sparkplug/sparkplug/issues/204) | PropertySetList |  | Skipped | 
| [#209](https://github.com/eclipse-sparkplug/sparkplug/issues/209) | PropertySet in BIRTH |  | Skipped |
| [#224](https://github.com/eclipse-sparkplug/sparkplug/issues/224) | Extensions on Metric |  |  Include |  | This was oversight in the 2.2 version of the protobuf version |
| [#231](https://github.com/eclipse-sparkplug/sparkplug/issues/231) | JSON encoding for Sparkplug |  | Exclude |  | We should write up the reasons for why we're doing this and make it public |
| [#232](https://github.com/eclipse-sparkplug/sparkplug/issues/232) | Timestamp accuracy |  | Include | 
| [#261](https://github.com/eclipse-sparkplug/sparkplug/issues/261) | Standardize a delimiter for use in the Group ID | Jens Deters | Include | Namespace | We want solve the issue but don't like this as a solution. This needs to be considered in an overall topic namespace overhaul. |
| [#262](https://github.com/eclipse-sparkplug/sparkplug/issues/262) | Standardize a way to expand metrics into MQTT topics | Jens Deters | Probably Include | Namespace | Maybe this is outside the scope of Sparkplug. Maybe this can be addressed in a Sparkplug Aware server? | Just and idea below: \* \[default\]G1/E1/D1 (with metric: M1/M2/T1) -> $SPARKPLUG/G1/BROKER/E1/D1/M1/M2/T1 This may complicate implementations of host applications that may or may not have access to a SAMS (Sparkplug Aware MQTT Server). Maybe this could be an external application and isn't built in specifically into the MQTT Server |
| [#306](https://github.com/eclipse-sparkplug/sparkplug/issues/306) | Add to NCMD and DCMD payloads an identifier of the source of the command | Jens Deters | Include |  | Maybe include a UUID and a timeout for error feedback in #442 |
| [#324](https://github.com/eclipse-sparkplug/sparkplug/issues/324) | Publishing partial updates for DataSet |  | Include |  | Need to define a mechanism for publishing indivual values and multiple changed values |
| [#429](https://github.com/eclipse-sparkplug/sparkplug/issues/429) | Make re-usable one-of value structure in protobuf to ease programming |  | Include |
| [#432](https://github.com/eclipse-sparkplug/sparkplug/issues/432) | Additions to SpC roadmap | Wes Johnson| Done |  | Needs to be split out into individual issues |
| [#438](https://github.com/eclipse-sparkplug/sparkplug/issues/438) | Expand Sparkplug Aware MQTT Server definition to include automatic expansion of DATA and BIRTH metric content into individual topics |  | Dup |   | Dup of #262 | 
| [#439](https://github.com/eclipse-sparkplug/sparkplug/issues/439) | Expand Sparkplug Aware MQTT Server definition to a REST API for querying metadata | Closed | Exclude |  | Good idea - but not Sparkplug - maybe we want to implement this in the TCK | 
| [#440](https://github.com/eclipse-sparkplug/sparkplug/issues/440) | Expand Sparkplug Aware MQTT Server definition to a REST API for querying most recent data and for connectionless publishing of DATA and BIRTH messages | Closed | Exclude |  | Good idea - but not Sparkplug - maybe we want to implement this in the TCK |
| [#441](https://github.com/eclipse-sparkplug/sparkplug/issues/441) | Add strong data typing for inputs and output to commands as part of BIRTH or METADATA |  | ??? |  | Not sure what this means |
| [#442](https://github.com/eclipse-sparkplug/sparkplug/issues/442) | Implement a reliable request/response mechanism so that commands can return data, error codes, or other result content |  | Include as Changed |  | Maybe we just need to have a topic for 'error messages' from Edge Nodes. Maybe a 'INFO' or 'LOG' topic verb |
| [#443](https://github.com/eclipse-sparkplug/sparkplug/issues/443) | Consider eliminating group/node/edge IDs altogether and allowing a flexible hiearchy using namespace/message\_type/any\_hiearchy | Closed | Exclude | 
| [#444](https://github.com/eclipse-sparkplug/sparkplug/issues/444) | Add new primitive types for location and generic JSON |  | Partially |  | Agree on lat/long/altitude/timestamp - get clarification on JSON part - Add 'Document' data type for JSON data |
| [#445](https://github.com/eclipse-sparkplug/sparkplug/issues/445) | Separate data and metadata in BIRTH messages and leverage the MQTT retain flag to optimize/minimize load on edge nodes and to enable application tooling to discover metadata even if an edge node is offline |  | Include |  | This is a dup of metadata availability | 
| [#446](https://github.com/eclipse-sparkplug/sparkplug/issues/446) | Clarify which components of a Sparkplug metric are mandatory and which are optional |  | Include |  | We should make sure this is clear in 3.0.0 | 
| [#447](https://github.com/eclipse-sparkplug/sparkplug/issues/447) | Differentiate between 'rebirth requests' and 'command writes' in NCMDs and the topic level |  | Include |  | Rebirth maybe becomes its own topic token - maybe multiple topics for 'Node controll commands' vs writes | 
| [#448](https://github.com/eclipse-sparkplug/sparkplug/issues/448) | Add support for Amended BIRTH if there are additions to an Edge Node or Device |  | Include |  | Probably requires a new VERB - maybe we need a concept of 'removal' as well | 
| [#449](https://github.com/eclipse-sparkplug/sparkplug/issues/449) | Make optional MetricDataTypes either required or not allowed in DATA messages - don't allow them to be optional |  | Include |  | MUST be included in BIRTH and MUST NOT in DATA - changes are covered by amended BIRTH messages |
| [#450](https://github.com/eclipse-sparkplug/sparkplug/issues/450) | Add support for arrays of Templates |  | Include | 
| | Add some type of feedback to host applications about what may or may not be supported |  | Include |  | Maybe just via logging - maybe an app note to define behavior and that it is ok to not support certain datatypes |
| | Define a standard JSON definition for Sparkplug representation - but it will not be used on the wire in Sparkplug messages |

## Questions
| GitHub Issue | Summary | Assignee | Status | Notes |
| ------------ | ------- | -------- | ------ | ----- |
| [#195](https://github.com/eclipse-sparkplug/sparkplug/issues/195) | Trim/Remove concept of Primary Host | Wes Johnson | In Progress |
| [#249](https://github.com/eclipse-sparkplug/sparkplug/issues/249) | A couple questions about Host Applications | Bryce Nakatani | Closed |
| [#386](https://github.com/eclipse-sparkplug/sparkplug/issues/386) | Couple of questions on how tot use TCK | Wes Johnson | Closed | 
| [#415](https://github.com/eclipse-sparkplug/sparkplug/issues/415) | ./gradlew build -> Task :specification:asciidoctorDocbook FAILED | Wes Johnson | In Progress |
| [#425](https://github.com/eclipse-sparkplug/sparkplug/issues/425) | Confused about bdSeq - TCK seems to want something different from the spec | Bryce Nakatani | Closed | 
| [#426](https://github.com/eclipse-sparkplug/sparkplug/issues/426) | Working with the UNKNOWN data type | Bryce Nakatani | In Progress | 
| [#427](https://github.com/eclipse-sparkplug/sparkplug/issues/427) | Question: historical data corner case | Joshua Wolf | 
| [#433](https://github.com/eclipse-sparkplug/sparkplug/issues/433) | Sparklug C addition | Travis Cox | 
| [#434](https://github.com/eclipse-sparkplug/sparkplug/issues/434) | How are enhancements and feature requests handled | Wes Johnson | In Progress | 
| [#437](https://github.com/eclipse-sparkplug/sparkplug/issues/437) | Question: Is the example BooleanArray in Sparkplug Specification 3.0.0 correct | Ilya Binshtok| In Progress |

## Spec Bugs
| GitHub Issue | Summary | Assignee | Status | Notes |
| ------------ | ------- | -------- | ------ | ----- |
| [#420](https://github.com/eclipse-sparkplug/sparkplug/issues/420) | TCK Edge Tests Fail to Start (Tag 3.0.0) | 
| [#421](https://github.com/eclipse-sparkplug/sparkplug/issues/421) | Edge Node Tests; TCK Fails When NBIRTH Sequence ID Skips (Tag 3.0.0) | Wes Johnson | In Progress |
| [#422](https://github.com/eclipse-sparkplug/sparkplug/issues/422) | TCK test fails because an edge node is incapable of retaining the node birth sequence ID. | 
| [#424](https://github.com/eclipse-sparkplug/sparkplug/issues/424) | TCK test fails if EoN device does not wait for a primary application | 
| [#435](https://github.com/eclipse-sparkplug/sparkplug/issues/435) | Bug Report: FloatArray and DoubleArray examples in Sparkplug Specification have incorrect endianness. DateTimeArray and Int8Array examples also wrong. | Ilya Binshtok | In Progress |

## TCK
| GitHub Issue | Summary | Assignee | Status | Notes |
| ------------ | ------- | -------- | ------ | ----- |
| [#168](https://github.com/eclipse-sparkplug/sparkplug/issues/168) | Add all possible assertion tests to the MQTT client listener | Ian Craggs | In Progress |
| [#377](https://github.com/eclipse-sparkplug/sparkplug/issues/377) | TCK binary packaging | Ian Craggs | In Progress | 
| [#416](https://github.com/eclipse-sparkplug/sparkplug/issues/416) | Bug Report: Edge Node SendComplexDataTest fails to validate PropertyValue.Type | 
| [#417](https://github.com/eclipse-sparkplug/sparkplug/issues/417) | Source code files missing from the Sparkplug TCK zip file required for result report generation |
| [#418](https://github.com/eclipse-sparkplug/sparkplug/issues/418) | Trim the TCK results report to only include the relevant profile sections | 
| [#430](https://github.com/eclipse-sparkplug/sparkplug/issues/430) | Bug Report: The TCK needs to check for presence of a sequence number in all required messages |