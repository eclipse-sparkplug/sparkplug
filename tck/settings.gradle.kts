rootProject.name = "sparkplug-tck"

includeBuild("../specification")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.hivemq.extension") version "${extra["plugin.hivemq-extension.version"]}"
        id("com.github.hierynomus.license-report") version "${extra["plugin.license.version"]}"
        id("io.github.sgtsilvio.gradle.defaults") version "${extra["plugin.defaults.version"]}"
        id("de.undercouch.download") version "${extra["plugin.download.version"]}"
    }
}

buildscript {
    repositories {
        mavenCentral()
    }

    val jbossTestAuditVersion = extra["jboss.test-audit.version"]

    dependencies {
        classpath("org.jboss.test-audit:jboss-test-audit-impl:$jbossTestAuditVersion")
    }
}
