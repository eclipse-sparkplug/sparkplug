rootProject.name = "sparkplug-tck"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("com.hivemq.extension") version "${extra["plugin.hivemq-extension.version"]}"
        id("com.github.hierynomus.license-report") version "${extra["plugin.license.version"]}"
        id("com.github.sgtsilvio.gradle.utf8") version "${extra["plugin.utf8.version"]}"
    }
}

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repository.jboss.org/nexus/content/groups/public-jboss/")
        }
    }

    val annotationVersion = extra["javax.annotation.version"]
    val jbossTestAuditVersion = extra["jboss.test-audit.version"]

    dependencies {
        classpath("javax.annotation:javax.annotation-api:$annotationVersion")
        classpath("org.jboss.test-audit:jboss-test-audit-impl:$jbossTestAuditVersion")
    }
}
