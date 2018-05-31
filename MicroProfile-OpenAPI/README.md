# MicroProfile OpenAPI TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile OpenAPI API, which depends on the Config API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

## Run against Payara Server

There is a profile for running against remote Payara Server called payara-server-remote. To run tests against Payara Server, do the following:
    
    Start Payara Server on localhost and default admin port 4848
    Run tests in the tck-runner project via maven: cd tck-runner && mvn clean -Ppayara-server-remote test


## Run against Payara Micro

There is a profile for running against Payara Micro called payara-micro-managed. To run tests against Payara Micro, do the following:

    Run tests in the tck-runner project via maven, replacing ${version} with the version of Payara Micro you wish to test against: cd tck-runner && mvn clean -Ppayara-micro-managed test -Dpayara.version=${version}

## Run against Payara Embedded

There is a profile for running against Payara Embedded All called payara-embedded. To run tests against Payara Embedded All, do the following:

    Run tests in the tck-runner project via maven, replacing ${version} with the version of Payara Embedded All you wish to test against: cd tck-runner && mvn clean -Ppayara-embedded test -Dpayara.version=${version}
