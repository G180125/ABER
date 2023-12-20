pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ( "https://jitpack.io" )
        mavenCentral() {
            content {
                includeModule("com.vanniktech", "android-image-cropper")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven ( "https://jitpack.io" )
    }
}

rootProject.name = "ABER"
include(":app")
