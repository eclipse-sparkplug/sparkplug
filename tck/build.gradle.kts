import de.undercouch.gradle.tasks.download.Download
import nl.javadude.gradle.plugins.license.DownloadLicensesExtension.license

plugins {
    id("com.hivemq.extension")
    id("com.github.hierynomus.license-report")
    id("io.github.sgtsilvio.gradle.defaults")
    id("de.undercouch.download")
}


/* ******************** metadata ******************** */

group = "org.eclipse.sparkplug"
description = "Technology Compatibility Kit for Eclipse Sparkplug"

hivemqExtension {
    name.set("Eclipse™ Sparkplug™ TCK")
    author.set("Eclipse")
    priority.set(0)
    startPriority.set(1000)
    mainClass.set("org.eclipse.sparkplug.tck.SparkplugHiveMQExtension")
    sdkVersion.set("4.4.4")
}


/* ******************** spec dependency ******************** */

val tckAuditXml: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named("tck-audit"))
    }
}

dependencies {
    tckAuditXml("org.eclipse.sparkplug:sparkplug-spec")
}


/* ******************** extension related ******************** */

hivemqExtension.resources {
    //not sure why the same file was copied twice with different classifiers.
    from(tckAuditXml) {
        rename { "tck-audit-suite.xml" }
        into("coverage")
    }
    from(tckAuditXml) {
        rename { "tck-audit-audit.xml" }
        into("coverage")
    }
    from(files(buildDir.resolve("coverage-report")).builtBy(tasks.compileJava)) {
        into("coverage")
    }
    from("LICENSE")
    from(files(tasks.downloadLicenses.map { it.htmlDestination.resolve("license-dependency.html") }).builtBy(tasks.downloadLicenses)) {
        into("third-party-licenses")
    }
}

tasks.hivemqExtensionJar {
    from("../LICENSE") { into("META-INF") }
    from("../NOTICE") { into("META-INF") }
}


/* ******************** dependencies ******************** */

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.jboss.test-audit:jboss-test-audit-impl:${property("jboss.test-audit.version")}")

    implementation("com.google.protobuf:protobuf-java:${property("protobuf.version")}")

    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:${property("paho.version")}")
    implementation("org.hibernate.beanvalidation.tck:beanvalidation-tck-tests:${property("beanvalidation.tck.version")}")
    implementation("jakarta.annotation:jakarta.annotation-api:${property("jakarta.annotation.version")}")

    implementation("com.fasterxml.jackson.core:jackson-core:${property("jackson.version")}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${property("jackson.version")}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${property("jackson.version")}")

    implementation("com.google.guava:guava:${property("guava.version")}")
    implementation("com.hivemq:hivemq-mqtt-client:${property("hivemq-client.version")}")
    implementation("org.jetbrains:annotations:${property("jetbrainsAnnotations.version")}")
}


/* ******************** license ******************** */

/*
<plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>license-maven-plugin</artifactId>
                <version>1.8</version>
                <executions>
                    <execution>
                        <id>add-third-party</id>
                        <phase>package</phase>
                        <goals>
                            <goal>add-third-party</goal>
                            <goal>download-licenses</goal>
                        </goals>
                        <configuration>
                            <useMissingFile>true</useMissingFile>
                            <excludedScopes>test</excludedScopes>
                            <licenseMerges>
                                <licenseMerge>The Apache Software License, Version
                                    2.0|Apache License, Version 2.0|Apache Public License
                                    2.0|Apache License 2.0|Apache Software License -
                                    Version 2.0</licenseMerge>
                            </licenseMerges>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
 */

downloadLicenses {
    val apache_2 = license("Apache License, Version 2.0", "https://opensource.org/licenses/Apache-2.0")
    val mit = license("MIT License", "https://opensource.org/licenses/MIT")
    val cddl_1_0 = license("CDDL, Version 1.0", "https://opensource.org/licenses/CDDL-1.0")
    val cddl_1_1 = license("CDDL, Version 1.1", "https://oss.oracle.com/licenses/CDDL+GPL-1.1")
    val lgpl_2_0 = license("LGPL, Version 2.0", "https://opensource.org/licenses/LGPL-2.0")
    val lgpl_2_1 = license("LGPL, Version 2.1", "https://opensource.org/licenses/LGPL-2.1")
    val lgpl_3_0 = license("LGPL, Version 3.0", "https://opensource.org/licenses/LGPL-3.0")
    val epl_1_0 = license("EPL, Version 1.0", "https://opensource.org/licenses/EPL-1.0")
    val epl_2_0 = license("EPL, Version 2.0", "https://opensource.org/licenses/EPL-2.0")
    val edl_1_0 = license("EDL, Version 1.0", "https://www.eclipse.org/org/documents/edl-v10.php")
    val bsd_3clause = license("BSD 3-Clause License", "https://opensource.org/licenses/BSD-3-Clause")
    val w3c = license("W3C License", "https://opensource.org/licenses/W3C")
    val cc0 = license("CC0", "https://creativecommons.org/publicdomain/zero/1.0/")

    aliases = mapOf(
        apache_2 to listOf(
            "Apache 2",
            "Apache 2.0",
            "Apache License 2.0",
            "Apache License, 2.0",
            "Apache License v2.0",
            "Apache License, Version 2",
            "Apache License Version 2.0",
            "Apache License, Version 2.0",
            "Apache License, version 2.0",
            "The Apache License, Version 2.0",
            "Apache Software License - Version 2.0",
            "Apache Software License, version 2.0",
            "The Apache Software License, Version 2.0"
        ),
        mit to listOf(
            "MIT License",
            "MIT license",
            "The MIT License",
            "The MIT License (MIT)"
        ),
        cddl_1_0 to listOf(
            "CDDL, Version 1.0",
            "Common Development and Distribution License 1.0",
            "COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0",
            license("CDDL", "https://glassfish.dev.java.net/public/CDDLv1.0.html")
        ),
        cddl_1_1 to listOf(
            "CDDL 1.1",
            "CDDL, Version 1.1",
            "Common Development And Distribution License 1.1",
            "CDDL+GPL License",
            "CDDL + GPLv2 with classpath exception",
            "Dual license consisting of the CDDL v1.1 and GPL v2",
            "CDDL or GPLv2 with exceptions",
            "CDDL/GPLv2+CE"
        ),
        lgpl_2_0 to listOf(
            "LGPL, Version 2.0",
            "GNU General Public License, version 2"
        ),
        lgpl_2_1 to listOf(
            "LGPL, Version 2.1",
            "LGPL, version 2.1",
            "GNU Lesser General Public License version 2.1 (LGPLv2.1)",
            license("GNU Lesser General Public License", "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html")
        ),
        lgpl_3_0 to listOf(
            "LGPL, Version 3.0",
            "Lesser General Public License, version 3 or greater"
        ),
        epl_1_0 to listOf(
            "EPL, Version 1.0",
            "Eclipse Public License - v 1.0",
            "Eclipse Public License - Version 1.0",
            license("Eclipse Public License", "http://www.eclipse.org/legal/epl-v10.html")
        ),
        epl_2_0 to listOf(
            "EPL 2.0",
            "EPL, Version 2.0"
        ),
        edl_1_0 to listOf(
            "EDL 1.0",
            "EDL, Version 1.0",
            "Eclipse Distribution License - v 1.0"
        ),
        bsd_3clause to listOf(
            "BSD 3-clause",
            "BSD-3-Clause",
            "BSD 3-Clause License",
            "3-Clause BSD License",
            "New BSD License",
            license("BSD", "http://asm.ow2.org/license.html"),
            license("BSD", "http://asm.objectweb.org/license.html"),
            license("BSD", "LICENSE.txt")
        ),
        w3c to listOf(
            "W3C License",
            "W3C Software Copyright Notice and License",
            "The W3C Software License"
        ),
        cc0 to listOf(
            "CC0",
            "Public Domain"
        )
    )

    dependencyConfiguration = "runtimeClasspath"
    excludeDependencies = listOf(
    )


}


/* ******************** build process ******************** */

sourceSets {
    main {
        java.srcDir(buildDir.resolve("generated/sources/audit"))
    }
}

//Fetches created tck-audit file from specification project.
val audit by tasks.registering {
    inputs.files(tckAuditXml)
    val generatedSourcesDir = layout.buildDirectory.dir("generated/sources/audit")
    outputs.file(generatedSourcesDir)

    doLast {
        org.jboss.test.audit.generate.SectionsClassGenerator.main(
            arrayOf(
                tckAuditXml.singleFile.absolutePath,
                "org.eclipse.sparkplug.tck",
                generatedSourcesDir.get().asFile.absolutePath
            )
        )
    }
}

val generateRequirements by tasks.registering {
    inputs.files(tckAuditXml)
    doLast {
        exec {
            commandLine = listOf("python3", "requirements.py")
        }
    }
}

//Creates coverage-report with jboss audit annotation processor
tasks.compileJava {
    dependsOn(audit)
    dependsOn(generateRequirements)

    inputs.files(tckAuditXml)
    val coverageReportDir = layout.buildDirectory.dir("coverage-report")
    outputs.file(coverageReportDir)

    options.compilerArgs.addAll(
        listOf(
            "-AauditXml=${tckAuditXml.singleFile.absolutePath}",
            "-AoutputDir=${coverageReportDir.get().asFile.absolutePath}"
        )
    )
}


/* ******************** debug run ******************** */

val downloadHivemqCe by tasks.registering(Download::class) {
    src("https://github.com/hivemq/hivemq-community-edition/releases/download/2021.1/hivemq-ce-2021.1.zip")
    dest(buildDir.resolve("hivemq-ce.zip"))
    overwrite(false)
}

val unzipHivemq by tasks.registering(Sync::class) {
    dependsOn(downloadHivemqCe)
    from(zipTree(buildDir.resolve("hivemq-ce.zip")))
    into({ temporaryDir })
}

tasks.prepareHivemqHome {
    //use your own hivemq professional edition instead of unzip each time

    hivemqHomeDirectory.set(layout.dir(unzipHivemq.map { it.destinationDir.resolve("hivemq-ce-2021.1") }))
    from(projectDir.resolve("hivemq-configuration/config.xml")) {
        into("conf")
    }
    from(projectDir.resolve("hivemq-configuration/logback.xml")) {
        into("conf")
    }
    /**
    from(projectDir.resolve("hivemq-configuration/extensions/hivemq-sparkplug-aware-extension.zip")) {
    into("extensions")
    /** must be unzipped for aware tests **/
    }
     **/
}

tasks.runHivemqWithExtension {
    environment["HIVEMQ_LOG_LEVEL"] = "DEBUG"
    debugOptions {
        enabled.set(false)
    }
}


tasks.jar {
    manifest.attributes["Main-Class"] = "org.eclipse.sparkplug.tck.utility.Device"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
