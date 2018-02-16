# MicroProfile Fault Tolerance TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Fault Tolerance API, which depends on CDI 1.1 API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

# Run against Payara Server

There is a profile for running against remote Payara Server called `payara-server-remote`. To run tests against Payara Server, do the following:

1. Start Payara Server on localhost and default admin port 4848
2. Run tests in the tck-runner project via maven: `cd tck-runner && mvn clean -Ppayara-server-remote test`

The illegalConfig and invalidParameters tests are disabled due them not being runnable with a remote profile.

# Run against Payara Micro

There is a profile for running against Payara Micro called `payara-micro-managed`. To run tests against Payara Micro, do the following:

1. Run tests in the tck-runner project via maven, replacing ${version} with the version of Payara Micro you wish to test against: `cd tck-runner && mvn clean -Ppayara-micro-managed test -Dpayara.version=${version}`

The illegalConfig and invalidParameters tests are disabled due to the tests a) expecting failures earlier than Payara throws them, and b) looking for the wrong exception.
The Asynchronous Bulkhead and Asynchronous Bulkhead Retry are currently disabled due to issues with Payara Micro.

# Run against Payara Embedded

There is a profile for running against Payara Embedded All called `payara-embedded`. To run tests against Payara Embedded All, do the following:

1. Run tests in the tck-runner project via maven, replacing ${version} with the version of Payara Embedded All you wish to test against: `cd tck-runner && mvn clean -Ppayara-embedded test -Dpayara.version=${version}`

The illegalConfig and invalidParameters tests are disabled due to the tests a) expecting failures earlier than Payara throws them, and b) looking for the wrong exception.