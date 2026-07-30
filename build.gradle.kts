plugins {}

tasks.register<Exec>("buildConnector") {
    workingDir = file("camunda-connector")
    commandLine("mvn.cmd", "clean", "package")
}

tasks.register<Exec>("buildJobWorker") {
    workingDir = file("camunda-job-worker")
    commandLine("gradlew.bat", "bootJar")
}

tasks.register<Exec>("dockerComposeUp") {
    dependsOn("buildConnector", "buildJobWorker")
    commandLine("docker-compose", "up", "-d", "--build")
}