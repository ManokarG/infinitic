rootProject.name = "io.infinitic"

include("infinitic-avro")
include("infinitic-taskManager-common")
include("infinitic-taskManager-worker")
include("infinitic-taskManager-client")
include("infinitic-taskManager-engine")
include("infinitic-taskManager-engine-pulsar")
include("infinitic-taskManager-tests")
include("infinitic-rest-api")
include("infinitic-workflowManager-engine")
include("infinitic-workflowManager-engine-pulsar")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        maven(url = "https://dl.bintray.com/gradle/gradle-plugins")
    }
}
