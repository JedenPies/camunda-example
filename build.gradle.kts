plugins {}

tasks.register<Exec>("buildConnector") {
    workingDir = file("camunda-connector")
    commandLine("mvn.cmd", "clean", "package")
}

tasks.register<Copy>("copyElementTemplates") {
    dependsOn("buildConnector")
    from(file("camunda-connector/element-templates/"))
    into(file("vacation-reservation-process/.camunda/element-templates/"))
}

tasks.register<Exec>("buildJobWorker") {
    workingDir = file("camunda-job-worker")
    commandLine("gradlew.bat", "bootJar")
}

tasks.register<Exec>("dockerComposeUp") {
    dependsOn("buildConnector", "buildJobWorker", "copyElementTemplates")
    commandLine("docker-compose", "up", "-d", "--build", "--force-recreate")
}