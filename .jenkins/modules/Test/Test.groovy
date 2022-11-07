MPLPostStep('always') {
    cleanWs()
}

MPLPostStep('failure') {
    echo "There are test failures, archiving server log"
    archiveArtifacts artifacts: "./${getPayaraDirectoryName}/glassfish/domains/${getDomainName()}/logs/server.log"
}

withMaven(jdk: CFG.jdk, options: [artifactsPublisher(disabled: true)]) {
    sh """mvn -B -V -ff -e clean verify --strict-checksums \
        -Djavax.net.ssl.trustStore=${env.JAVA_HOME}/jre/lib/security/cacerts \
        -Djavax.xml.accessExternalSchema=all -Dpayara.version=${CFG.'build.version'} \
        -Dpayara_domain=${CFG.domain_name} -Dpayara.home="${pwd()}/${getPayaraDirectoryName(CFG.'build.version')}" \
        -Ppayara-server-remote"""
}
