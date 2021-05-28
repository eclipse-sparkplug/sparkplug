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
        //add license
    }
    github {
        org = "eclipse"
        repo = "sparkplug"
        pages()
        issues()
    }
}


/* ******************** asciidoctor ******************** */

tasks.register("asciidoctorPdf", AsciidoctorTask::class) {
    group = "documentation"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("src/main/asciidoc"))
    sources {
        include("*.adoc", "chapters/*.adoc")
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
                "pagenums" to "true",
                "numbered" to "true",
                "docinfo" to "true",
                "experimental" to "false",
                "toc" to "true",
                "project-version" to project.version,
                "imagesdir" to "assets/images"
        ))
    }

}

tasks.register("asciidoctorHtml", AsciidoctorTask::class) {
    group = "documentation"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("src/main/asciidoc"))
    sources {
        include("*.adoc", "chapters/*.adoc")
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
                "linkcss" to "false",
                "project-version" to project.version,
                "imagesdir" to "assets/images"
        ))
    }
}

tasks.register("asciidoctorDocbook", AsciidoctorTask::class) {
    group = "documentation"

    baseDirFollowsSourceDir()
    sourceDirProperty.set(file("src/main/asciidoc"))
    sources {
        include("*.adoc", "chapters/*.adoc")
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
}


/* ******************** xslt transformation ******************** */

tasks.register("xsltXalan") {
    group = "tck"
    dependsOn("asciidoctorDocbook")

    val inputFile = buildDir.resolve("docs/docbook/sparkplug_spec.xml")
    inputs.file(inputFile)

    val xslFile = projectDir.resolve("src/main/xsl/tck-audit.xsl")

    val outputFolder = buildDir.resolve("tck-audit")
    val outputFile = outputFolder.resolve("tck-audit.xml")
    outputs.file(outputFile)

    val parameters = mapOf(
            "currentDate" to java.time.Instant.now().toString(),
            "revision" to project.version.toString()
    )

    doLast {
        outputFolder.mkdirs()
        transform(inputFile, xslFile, outputFile, parameters)
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
}


/* ******************** additional ******************** */

tasks.named("build") {
    dependsOn("asciidoctorDocbook", "asciidoctorHtml", "asciidoctorPdf", "xsltXalan")
}