# Eclipse Sparkplug v3.0.0

## Sparkplug Specification
* Formalized the previous version (v2.2) of the Sparkplug Specification
* Created 299 total assertions (298 testable) in the v3.0.0 version of the Sparkplug Specification
* Improved, clarified, and expanded on concepts in the Sparkplug Specification
* Implemented in Asciidoc for better version control going forward
* Converted all images to PlantUML for better version control going forward
* Used annotations in the specification to track which assertions are tested by the TCK
* Incorporated tooling to output HTML and PDF versions of the specification as part of the build
* Modified the Host Application STATE message topic and payload to eliminate potential 'stranded' Edge Nodes

## Sparkplug Technology Compatibility Kit (TCK)
* Created the base framework for the TCK
* Incorprated a 'coverage report' to show which assertions in the spec are covered by the TCK
* Created an interactive web UI for using the TCK
* Added automated tests for all 298 testable assertions in the specification
* Added output reporting for users when exercising the TCK
