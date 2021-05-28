rootProject.name = "sparkplug"

listOf("spec", "tck").forEach { module ->
    include("${rootProject.name}-$module")
    project(":${rootProject.name}-$module").projectDir = file(module)
}