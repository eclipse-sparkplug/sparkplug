plugins {
    id("com.github.sgtsilvio.gradle.metadata")
    id("org.asciidoctor.jvm.base")
}

repositories {
    mavenCentral()
}

/* ******************** metadata ******************** */

group = "org.eclipse.sparkplug"
description = "Sparkplug 3.0.0-SNAPSHOT Specification"

metadata {
    moduleName = "org.eclipse.sparkplug.specification"
    readableName = "Sparkplug $version Specification"

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


tasks.register("asciidoctorPdf", org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
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


    configure<org.asciidoctor.gradle.jvm.AsciidoctorJExtension> {
        modules {
            diagram.use()
            pdf.use()
            pdf.setVersion("1.5.4")
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

tasks.register("asciidoctorHtml", org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
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

    configure<org.asciidoctor.gradle.jvm.AsciidoctorJExtension> {
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

tasks.register("asciidoctorDocbook", org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
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

    configure<org.asciidoctor.gradle.jvm.AsciidoctorJExtension> {
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

val xxx by configurations.creating {

}

dependencies {
    //xxx("xalan:xalan:2.7.2")
}

tasks.register("xsltXalan") {
    //dependsOn("asciidoctorDocbook")

    val outputFolder = buildDir.resolve("tck-audit")
    val outputFile = outputFolder.resolve("tck-audit.xml")
    outputs.file(outputFile)

    val inputFile = buildDir.resolve("docs/docbook/sparkplug_spec.xml")
    inputs.file(inputFile)

    val transformerFactory2 = javax.xml.transform.TransformerFactory.newInstance()
    println(transformerFactory2)
    doLast {
        outputFolder.mkdirs()

        val transformerFactory = javax.xml.transform.TransformerFactory.newInstance()
        println(transformerFactory)

        val inputXslStream = java.nio.file.Files.newInputStream(projectDir.resolve("src/main/xsl/tck-audit.xsl").toPath(),
                java.nio.file.StandardOpenOption.READ)
        val inputXslSource = org.xml.sax.InputSource(inputXslStream)
        val saxXslSource = javax.xml.transform.sax.SAXSource(inputXslSource)

        val inputStream = java.nio.file.Files.newInputStream(inputFile.toPath(), java.nio.file.StandardOpenOption.READ)
        val inputSource = org.xml.sax.InputSource(inputStream)
        val saxSource = javax.xml.transform.sax.SAXSource(inputSource)

        val outputFileStream = java.nio.file.Files.newOutputStream(outputFile.toPath(), java.nio.file.StandardOpenOption.CREATE)
        val streamResult = javax.xml.transform.stream.StreamResult(outputFileStream)


        val template = transformerFactory.newTemplates(saxXslSource)

        val transformer = template.newTransformer()
        transformer.setParameter("currentDate", java.time.Instant.now().toString())
        transformer.setParameter("revision", project.version.toString())

        transformer.transform(saxSource, streamResult)

        inputXslStream.close()
        inputStream.close()
        outputFileStream.close()
        //println(projectDir.resolve("src/main/xsl/tck-audit.xsl").absoluteFile.readText())
        //println(inputFile.absoluteFile.readText())
        //println(outputFile.absoluteFile.readText())

    }
    /*classpath = xxx

    main = "com.sun.org.apache.xalan.xslt.Process"
    args = listOf(
            "-IN", inputFile.absolutePath.toString(),//project.buildDir.resolve("")
            "-XSL", projectDir.resolve("src/main/xsl/tck-audit.xsl").absolutePath.toString(),
            "-OUT", outputFile.absolutePath,
            "-PARAM", "currentDate", java.time.Instant.now().toString(),
            "-PARAM", "revision", project.version.toString()
    )

    /*org.apache.xalan.xslt.Process.main(arrayOf(
            "-IN", inputFile.absolutePath.toString(),//project.buildDir.resolve("")
            "-XSL", projectDir.resolve("src/main/xsl/tck-audit.xsl").absolutePath.toString(),
            "-OUT", outputFile.absolutePath,
            "-PARAM", "currentDate", java.time.Instant.now().toString(),
            "-PARAM", "revision", project.version.toString()
    ))*/*/
}

tasks.named("build") {
    dependsOn("asciidoctorDocbook", "asciidoctorHtml", "asciidoctorPdf", "xsltXalan")
}