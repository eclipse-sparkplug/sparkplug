rootProject.name = "sparkplug-spec"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.asciidoctor.jvm.base") version "${extra["plugin.asciidoctor.version"]}"
    }
}
