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
    moduleName = "org.eclipse.sparkplug.specification"
    readableName = "Sparkplug ${project.version} Specification"

    organization {
        name = "Eclipse Foundation"
        url = "https://sparkplug.eclipse.org/"
    }
    license {
        shortName = "EPL-2.0"
        readableName = "Eclipse Public License - v 2.0"
        url = "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt"
    }
    github {
        org = "eclipse"
        repo = "sparkplug"
        pages()
        issues()
    }
}


/* ******************** asciidoctor ******************** */

val asciidoctorPdf = tasks.register("asciidoctorPdf", AsciidoctorTask::class) {
    group = "spec"
    dependsOn(createNormativeAppendix)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/spec"))
    sources {
        include("sparkplug_spec.adoc")
    }
    setOutputDir(buildDir.resolve("docs/pdf"))

    outputOptions {
        setBackends(listOf("pdf"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
            pdf.use()
            pdf.setVersion(project.property("plugin.asciidoctor.pdf.version"))
        }

        setOptions(mapOf(
                "doctype" to "book",
                "header_footer" to "true",
                "template_engine" to "slim",
                "compact" to "false"
        ))

        setAttributes(mapOf(
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
        ))
    }
    asciidoctorj {
        failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
    }
}

val asciidoctorHtml = tasks.register("asciidoctorHtml", AsciidoctorTask::class) {
    group = "spec"
    dependsOn(createNormativeAppendix)

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/spec"))
    sources {
        include("sparkplug_spec.adoc")
    }
    setOutputDir(buildDir.resolve("docs/html"))

    outputOptions {
        setBackends(listOf("html5"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
        }

        setOptions(mapOf(
                "header_footer" to "true"
        ))
        setAttributes(mapOf(
                "source-highlighter" to "highlight.js",
                "toc" to "true",
                "docinfo2" to "true",
                "linkcss" to "false",
                "project-version" to project.version,
                "imagesdir" to "assets/images"
        ))
    }
    asciidoctorj {
        failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
    }
}

val asciidoctorDocbook = tasks.register("asciidoctorDocbook", AsciidoctorTask::class) {
    group = "spec"
    dependsOn("copySpecSourceIntoBuild")

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("build/spec"))
    sources {
        include("sparkplug_spec.adoc")
    }
    setOutputDir(buildDir.resolve("docs/docbook"))
    outputs.file(buildDir.resolve("docs/docbook/sparkplug_spec.xml"))

    outputOptions {
        setBackends(listOf("docbook"))
    }

    resources {
        from("src/main/asciidoc/assets/images")
        into("./assets/images")
    }

    configure<AsciidoctorJExtension> {
        modules {
            diagram.use()
        }

        setOptions(mapOf(
                "doctype" to "article",
                "header_footer" to "true"
        ))

        setAttributes(mapOf(
                "project-version" to version,
                "imagesdir" to "assets/images"
        ))
    }
    asciidoctorj {
        failureLevel = org.asciidoctor.gradle.base.log.Severity.WARN
    }
}


/* ******************** xslt transformation ******************** */

val xsltAudit = tasks.register("xsltAudit") {
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

val normativeStatements = tasks.register("xsltNormativeStatements") {
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
            StandardOpenOption.READ)
    val inputSource = InputSource(inputStream)
    val saxSource = SAXSource(inputSource)

    //XSL stylesheet
    val inputXslStream = newInputStream(
            xslFile.toPath(),
            StandardOpenOption.READ)
    val inputXslSource = InputSource(inputXslStream)
    val saxXslSource = SAXSource(inputXslSource)

    //XML output file
    val outputFileStream = newOutputStream(
            outputFile.toPath(),
            StandardOpenOption.CREATE)
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

val createNormativeAppendix = tasks.register("createNormativeAppendix") {
    group = "spec"
    dependsOn(copySpec, normativeStatements)

    inputs.files(copySpec.get().outputs.files)
    inputs.file(normativeStatements.get().outputs.files.singleFile)

    val origAppendixFile = file(buildDir.resolve("spec/chapters/Sparkplug_Appendix_B.adoc"))
    val createdStatements = normativeStatements.get().outputs.files.singleFile

    val outputFolder = buildDir.resolve("spec/chapters")
    val outputFile = outputFolder.resolve("Sparkplug_Appendix_B.adoc")

    outputs.file(outputFile)

    doLast {
        outputFolder.mkdirs()

        file(outputFile).writeText(origAppendixFile.readText())
        file(outputFile).appendText(createdStatements.readText())
    }
}

val copySpec = tasks.register("copySpecSourceIntoBuild", Copy::class) {
    group = "spec"

    from("src/main/asciidoc")
    into(buildDir.resolve("spec"))
}


val renameHtml = tasks.register("renameHtml", Copy::class) {
    group = "spec"
    dependsOn("asciidoctorHtml")

    from(buildDir.resolve("docs/html/sparkplug_spec.html")) {
        rename { "index.html" }
    }
    into(buildDir.resolve("docs/html"))
}



tasks.named("build") {
    dependsOn(asciidoctorPdf, asciidoctorHtml, renameHtml)
}
