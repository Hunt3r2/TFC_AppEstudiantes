pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.mongodb.com/releases") } // Repositorio de MongoDB
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.mongodb.com/releases") } // Repositorio de MongoDB
    }
}

rootProject.name = "ProyectoAppFinanzas"
include(":app") // Asegúrate de incluir tus módulos aquí