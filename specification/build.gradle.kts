import org.asciidoctor.gradle.jvm.AsciidoctorJExtension
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.xml.sax.InputSource
import java.nio.file.Files.newInputStream
import java.nio.file.Files.newOutputStream
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

val xsltAudit by tasks.registering {
    group = "tck"
    dependsOn(asciidoctorDocbook)

    val inputFile = buildDir.resolve("docs/docbook/sparkplug_spec.xml")
    val xslFile = projectDir.resolve("src/main/xsl/tck-audit.xsl")

    inputs.file(inputFile)
    inputs.file(xslFile)

    val outputFolder = buildDir.resolve("tck-audit")
    val outputFile = outputFolder.resolve("tck-audit.xml")

    outputs.file(outputFile)

    val parameters = mapOf(
        "currentDate" to java.time.Instant.now().toString(),
        "revision" to project.version.toString()
    )

    doLast {
        outputFolder.mkdirs()
        outputFile.delete()
        transform(inputFile, xslFile, outputFile, parameters)
    }
}

val xsltNormativeStatements by tasks.registering {
    group = "spec"
    dependsOn(xsltAudit)

    val inputFile = xsltAudit.get().outputs.files.singleFile
    val xslFile = projectDir.resolve("src/main/xsl/normative-statements.xsl")

    inputs.file(inputFile)
    inputs.file(xslFile)

    val outputFolder = buildDir.resolve("normative-statements")
    val outputFile = outputFolder.resolve("appendix.adoc")

    outputs.file(outputFile)

    doLast {
        outputFolder.mkdirs()
        outputFile.delete()
        transform(inputFile, xslFile, outputFile, mapOf())
    }
}

fun transform(inputFile: File, xslFile: File, outputFile: File, parameters: Map<String, String>) {
    val transformerFactory = TransformerFactory.newInstance()

    //XML input file
    val inputStream = newInputStream(
        inputFile.toPath(),
        StandardOpenOption.READ
    )
    val inputSource = InputSource(inputStream)
    val saxSource = SAXSource(inputSource)

    //XSL stylesheet
    val inputXslStream = newInputStream(
        xslFile.toPath(),
        StandardOpenOption.READ
    )
    val inputXslSource = InputSource(inputXslStream)
    val saxXslSource = SAXSource(inputXslSource)

    //XML output file
    val outputFileStream = newOutputStream(
        outputFile.toPath(),
        StandardOpenOption.CREATE
    )
    val streamResult = StreamResult(outputFileStream)

    //create transformer
    val template = transformerFactory.newTemplates(saxXslSource)
    val transformer = template.newTransformer()

    //set parameters for transformation
    parameters.forEach { (name, value) ->
        transformer.setParameter(name, value)
    }

    transformer.transform(saxSource, streamResult)
    transformer.reset()
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
