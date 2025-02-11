import org.gradle.internal.impldep.org.gradleinternal.buildinit.plugins.internal.maven.Dependency

pluginManagement {
    repositories {

        maven { url=uri("https://maven.aliyun.com/repository/google") }
        maven { url=uri("https://maven.aliyun.com/repository/public") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven ("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven { url=uri("https://maven.aliyun.com/repository/google") }
        maven { url=uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}

rootProject.name = "QZWX_APP"
include(":app")
include(":Diary")

