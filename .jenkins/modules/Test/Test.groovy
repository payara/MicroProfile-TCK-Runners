#!groovy
MPLPostStep('always') {
    echo "Cleaning up workspace from parent"
    cleanWs()
}

def MVN_OPTIONS= CFG.mvnOptions ?: ""
withMaven(jdk: CFG.jdk, options: [artifactsPublisher(disabled: true), junitPublisher(keepLongStdio: true)]) {
    sh """mvn verify ${MVN_OPTIONS}"""
}