import org.asciidoctor.gradle.jvm.AsciidoctorJExtension
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

plugins {
    id("com.github.sgtsilvio.gradle.metadata")
    id("org.asciidoctor.jvm.base")
}

repositories {
    mavenCentral()
}


/* ******************** metadata ******************** */

group = "org.eclipse.sparkplug"
description = "Sparkplug ${project.version} Specification"

metadata {
    moduleName.set("org.eclipse.sparkplug.specification")
    readableName.set("Sparkplug ${project.version} Specification")

    organization {
        name.set("Eclipse Foundation")
        url.set("https://sparkplug.eclipse.org/")
    }
    license {
        shortName.set("EPL-2.0")
        readableName.set("Eclipse Public License - v 2.0")
        url.set("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt")
    }
    github {
        org.set("eclipse")
        repo.set("sparkplug")
        pages()
        issues()
    }
}


/* ******************** asciidoctor ******************** */

val asciidoctorPdf by tasks.registering(AsciidoctorTask::class) {
    group = "spec"
    dependsOn(createNormativeAppendix)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/spec"))
    sources {
        include("sparkplug_spec.adoc")
    }
    outputDirProperty.set(layout.buildDirectory.dir("docs/pdf"))

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
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

    failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
}

val asciidoctorHtml by tasks.registering(AsciidoctorTask::class) {
    group = "spec"
    dependsOn(createNormativeAppendix)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/spec"))
    sources {
        include("sparkplug_spec.adoc")
    }
    outputDirProperty.set(layout.buildDirectory.dir("docs/html"))

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
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

    failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
}

val asciidoctorDocbook by tasks.registering(AsciidoctorTask::class) {
    group = "spec"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("src/main/asciidoc"))
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

    failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
}


/* ******************** xslt transformation ******************** */

val xsltAudit by tasks.registering(XslTransform::class) {
    group = "tck"

    inputFile.set(asciidoctorDocbook.get().outputDirProperty.file("sparkplug_spec.xml"))
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


/* ******************** additional ******************** */

val createNormativeAppendix by tasks.register("createNormativeAppendix") {
    group = "spec"
    dependsOn(copySpecSourceIntoBuild, xsltNormativeStatements)

    inputs.files(copySpecSourceIntoBuild.get().outputs.files)
    inputs.file(xsltNormativeStatements.get().outputs.files.singleFile)

    val origAppendixFile = file(buildDir.resolve("spec/chapters/Sparkplug_Appendix_B.adoc"))
    val createdStatements = xsltNormativeStatements.get().outputs.files.singleFile

    val outputFolder = buildDir.resolve("spec/chapters")
    val outputFile = outputFolder.resolve("Sparkplug_Appendix_B.adoc")

    outputs.file(outputFile)

    doLast {
        outputFolder.mkdirs()

        file(outputFile).writeText(origAppendixFile.readText())
        file(outputFile).appendText(createdStatements.readText())
    }
}

val copySpecSourceIntoBuild by tasks.registering(Copy::class) {
    group = "spec"

    from("src/main/asciidoc")
    into(buildDir.resolve("spec"))
}


val renameHtml by tasks.registering(Copy::class) {
    group = "spec"
    dependsOn("asciidoctorHtml")

    from(buildDir.resolve("docs/html/sparkplug_spec.html")) {
        rename { "index.html" }
    }
    into(buildDir.resolve("docs/html"))
}



tasks.build {
    dependsOn(asciidoctorPdf, asciidoctorHtml, renameHtml)
}
