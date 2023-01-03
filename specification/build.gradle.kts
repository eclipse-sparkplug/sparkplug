import org.asciidoctor.gradle.base.log.Severity
import org.asciidoctor.gradle.jvm.AsciidoctorJExtension
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

plugins {
    id("org.asciidoctor.jvm.base")
}

group = "org.eclipse.sparkplug"
description = "Sparkplug ${project.version} Specification"

repositories {
    mavenCentral()
}


/* ******************** asciidoctor ******************** */

val asciidoctorDocbook by tasks.registering(AsciidoctorTask::class) {
    group = "spec"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(layout.projectDirectory.dir("src/main/asciidoc"))
    sources {
        include("sparkplug_spec.adoc")
    }
    outputDirProperty.set(layout.buildDirectory.dir("docs/docbook"))

    outputOptions {
        setBackends(listOf("docbook"))
    }

    options = mapOf(
        "doctype" to "article",
        "header_footer" to "true"
    )
    attributes = mapOf(
        "project-version" to version,
        "imagesdir" to "assets/images"
    )

    failureLevel = Severity.WARN
}

val asciidoctorPdf by tasks.registering(AsciidoctorTask::class) {
    group = "spec"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(layout.dir(combineSpecSourceWithNormativeAppendix.map { it.destinationDir }))
    dependsOn(combineSpecSourceWithNormativeAppendix) // needed as sourceDirProperty does not capture dependency
    sources {
        include("sparkplug_spec.adoc")
    }
    outputDirProperty.set(layout.buildDirectory.dir("docs/pdf"))

    resources {
        from("src/main/asciidoc/assets/images")
        into("assets/images")
    }

    outputOptions {
        setBackends(listOf("pdf"))
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
            pdf.version(project.property("plugin.asciidoctor.pdf.version"))
        }
    }

    options = mapOf(
        "doctype" to "book",
        "header_footer" to "true",
        "template_engine" to "slim",
        "compact" to "false"
    )
    attributes = mapOf(
        "source-highlighter" to "highlight.js",
        "pagenums" to "true",
        "numbered" to "true",
        "docinfo2" to "true",
        "experimental" to "false",
        "linkcss" to "false",
        "toc" to "true",
        "project-version" to project.version,
        "imagesdir" to "assets/images",
        "pdf-themesdir" to "themes",
        "pdf-theme" to "sparkplug"
    )

    failureLevel = Severity.WARN
}

val asciidoctorHtml by tasks.registering(AsciidoctorTask::class) {
    group = "spec"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(layout.dir(combineSpecSourceWithNormativeAppendix.map { it.destinationDir }))
    dependsOn(combineSpecSourceWithNormativeAppendix) // needed as sourceDirProperty does not capture dependency
    sources {
        include("sparkplug_spec.adoc")
    }
    outputDirProperty.set(layout.buildDirectory.dir("docs/html"))

    resources {
        from("src/main/asciidoc/assets/images")
        into("assets/images")
    }

    outputOptions {
        setBackends(listOf("html5"))
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
        }
    }

    options = mapOf(
        "header_footer" to "true"
    )
    attributes = mapOf(
        "source-highlighter" to "highlight.js",
        "toc" to "true",
        "docinfo2" to "true",
        "linkcss" to "false",
        "project-version" to project.version,
        "imagesdir" to "assets/images"
    )

    failureLevel = Severity.WARN
}

val renameHtml by tasks.registering(Copy::class) {
    group = "spec"

    dependsOn(asciidoctorHtml) // needed as outputDirProperty does not propagate dependency
    from(asciidoctorHtml.get().outputDirProperty.file("sparkplug_spec.html")) {
        rename { "index.html" }
    }
    into(asciidoctorHtml.get().outputDirProperty)
}

tasks.build {
    dependsOn(asciidoctorPdf, asciidoctorHtml, renameHtml)
}


/* ******************** xsl transformation ******************** */

val xsltAudit by tasks.registering(XslTransform::class) {
    group = "tck"

    inputFile.set(asciidoctorDocbook.get().outputDirProperty.file("sparkplug_spec.xml"))
    dependsOn(asciidoctorDocbook) // needed as outputDirProperty does not propagate dependency
    xslFile.set(layout.projectDirectory.file("src/main/xsl/tck-audit.xsl"))
    parameters.put("currentDate", java.time.Instant.now().toString())
    parameters.put("revision", project.version.toString())
    outputFile.set(layout.buildDirectory.file("tck-audit.xml"))
}

val xsltNormativeStatements by tasks.registering(XslTransform::class) {
    group = "spec"

    inputFile.set(xsltAudit.get().outputFile)
    xslFile.set(layout.projectDirectory.file("src/main/xsl/normative-statements.xsl"))
    outputFile.set(layout.buildDirectory.file("normative-statements.adoc"))
}

abstract class XslTransform : DefaultTask() {

    @get:InputFile
    val inputFile = project.objects.fileProperty()

    @get:InputFile
    val xslFile = project.objects.fileProperty()

    @get:Input
    val parameters = project.objects.mapProperty<String, String>()

    @get:OutputFile
    val outputFile = project.objects.fileProperty()

    @TaskAction
    protected fun run() {
        val inputFileStream = Files.newInputStream(inputFile.get().asFile.toPath(), StandardOpenOption.READ)
        val xslFileStream = Files.newInputStream(xslFile.get().asFile.toPath(), StandardOpenOption.READ)
        val outputFileStream = Files.newOutputStream(outputFile.get().asFile.toPath(), StandardOpenOption.CREATE)

        val transformerFactory = TransformerFactory.newInstance()
        val template = transformerFactory.newTemplates(SAXSource(InputSource(xslFileStream)))
        val transformer = template.newTransformer()

        parameters.get().forEach { (name, value) ->
            transformer.setParameter(name, value)
        }

        transformer.transform(SAXSource(InputSource(inputFileStream)), StreamResult(outputFileStream))
        transformer.reset()
    }
}

val createNormativeAppendix by tasks.registering {
    group = "spec"

    val normativeAppendixHeader = layout.projectDirectory.file("src/main/asciidoc/chapters/Sparkplug_Appendix_B.adoc")
    inputs.file(normativeAppendixHeader)
    val normativeStatementsAdoc = xsltNormativeStatements.get().outputFile
    inputs.file(normativeStatementsAdoc)
    val normativeAppendixFile = layout.buildDirectory.file("Sparkplug_Appendix_B.adoc")
    outputs.file(normativeAppendixFile)

    doLast {
        normativeAppendixFile.get().asFile.apply {
            writeText(normativeAppendixHeader.asFile.readText())
            appendText(normativeStatementsAdoc.get().asFile.readText())
        }
    }
}

val combineSpecSourceWithNormativeAppendix by tasks.registering(Sync::class) {
    group = "spec"

    from("src/main/asciidoc") { exclude("chapters/Sparkplug_Appendix_B.adoc") }
    from(createNormativeAppendix) { into("chapters") }
    into(layout.buildDirectory.dir("spec"))
}

/* ******************** artifacts ******************** */

val tckAuditXml: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named("tck-audit"))
    }
}

artifacts {
    add(tckAuditXml.name, xsltAudit)
}
