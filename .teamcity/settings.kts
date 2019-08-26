import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

version = "2019.1"

project {
    buildType(AndroidGradleBuild)
}

object AndroidGradleBuild : BuildType({
    name = "Android Gradle Build"

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    steps {
        script {
            name = "Download Android SDK"
            scriptContent = """
                #!/bin/bash -e
                
                wget https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip
                
                # unpack archive
                unzip sdk-tools-linux-4333796.zip
                
                rm sdk-tools-linux-4333796.zip
                
                mkdir android-sdk
                mv tools android-sdk/tools
                
                echo "sdk.dir=$(pwd)/android-sdk/" >> local.properties
            """.trimIndent()
        }
        gradle {}
    }

    triggers {
        vcs {}
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%github.commit_status_publisher_token%"
                }
            }
        }
        pullRequests {
            provider = github {
                authType = vcsRoot()
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})