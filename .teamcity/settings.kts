import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
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