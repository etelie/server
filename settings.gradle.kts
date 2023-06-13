rootProject.name = "com.etelie.server"

val tegralVersion: String by settings

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("tegralLibs") {
            from("guru.zoroark.tegral:tegral-catalog:$tegralVersion")
        }
    }
}
