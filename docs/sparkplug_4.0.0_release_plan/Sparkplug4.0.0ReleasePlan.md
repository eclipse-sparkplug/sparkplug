# Eclipse Sparkplug 4.0.0 Release Plan
The Sparkplug Specification Team is working towards a new release of the Sparkplug Specification. When it is released, this will be version 4.0.0. It will have significant changes that will require a new initial topic token identifier of ‘spCv1.0’ rather than the current 3.0.0 version of ‘spBv1.0’. This is required due to what will be significant differences in the MQTT topic structure and the payload encoding.

The goal of the new release is to incorporate as many changes as possible into a single release. Because Sparkplug is protocol for a cross-vendor, cross-platform distributed system, we would like to prevent multiple smaller incremental releases. This will make it simpler for implementers of Sparkplug in that a single update can be performed rather than multiple smaller updates.

Note the Sparkplug B definition will still remain in the Sparkplug 4.0.0 Specification document. The final document will include language for both the existing Sparkplug B variant as well as the Sparkplug C variant.

## Scope
There are a number of design goals associated with the Sparkplug 4.0.0 release. At a high level these are:

- Improve support and reduce disruptions when using multiple Sparkplug Host Applications
- Improvements and allow more flexibility in the topic namespace structure
- Expand on the ‘Sparkplug Aware MQTT Server’ profile
- Elevate prominence of some metadata in the payload structure such as quality
- General improvements to the payload format
- Improve feedback and metadata around Sparkplug commands
- Improve overall specification clarity


A full working list of the Sparkplug issues can be found here:
https://docs.google.com/spreadsheets/d/1SI-gzA3ofd9DMsQwfX3gnpbNPZ95A7FTAooTdZP1iYQ/edit#gid=0

Note, the list of issues includes issues that have been discussed by the Sparkplug project team that we’ve decided not to include. Specific reasons as to why they will not be included are in the associated Github issues.

## Deliverables
There are three artifacts associated with the release. Each of the milestone builds and release candidates will include all three. These are the Sparkplug Specification itself, the Technology Compatibility Kit (TCK), and compatible implementations for each of the Sparkplug profiles.

### Sparkplug Specification
A new draft of the Sparkplug Specification will be written. It will be modified in such a way that Sparkplug B and Sparkplug C are both included in the document. It will contain all of the new features for Sparkplug C. It will likely also include some additional cleanup and clarification in the language around Sparkplug B.

### Sparkplug TCK
The Sparkplug TCK will be modified to support testing and validation of both Sparkplug B and Sparkplug C. There will also be changes to the reporting mechanism of the TCK to provide better feedback to users of the TCK about their implementation.

### Sparkplug Compatible Implementations
At a minimum the Java implementation of Sparkplug will be expanded to support Sparkplug C in the new Sparkplug 4.0.0 Specification.

## Release Milestones
There will be two milestone releases associated with the release of Sparkplug 4.0.0. After the release of M2 there will also be at least one release candidate as we get closer to the final release. There may be additional release candidates as well.

Each of the milestones will include all three deliverables. These will be the Specification document itself, the TCK, and the Java based compatible implementations.. These are the Sparkplug Specification itself, the TCK, and the compatible implementations.

The milestone builds are as follows.

- M1
  - This will include all major MQTT topic namespace changes planned for the 4.0.0 release.
- M2
  - This will include everything that is included in M1 plus all major payload changes required to Sparkplug 4.0.0.
- RC1
  - This will be an initial release candidate and will include all major changes to the Sparkplug Specification for the 4.0.0 release.

## FAQ
TBD
