plugins {
    id("java")
}


/* ******************** java ******************** */

allprojects {
    plugins.withId("java") {
        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        //plugins.apply("com.github.sgtsilvio.gradle.utf8")
    }
}