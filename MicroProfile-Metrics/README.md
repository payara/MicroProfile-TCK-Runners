# MicroProfile Metrics TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Quick start

To run tests against Payara Server, see [Run against Payara Server](#Run against Payara Server) below.

There are no instructions to run against Payara Micro yet. This requires an arquillian adapter for Payara Micro which isn't ready yet.

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Metrics API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

## Providing the implementation to the TCK runner

An Arquillian container adapter that can connect to the runtime that contains the implementation ([an official guide how to do it](http://arquillian.org/guides/developing_a_container_adapter/))

## Running the tests

Tests can be executed with the tck-runner project - go to the tck-runner project's directory and run `mvn test`

The tck-runner project executes the tests in the microprofile-metrics-api-tck artifact. The tests will fail, because no implementation is provided yet.

### Running the tests with an Arquillian container

run tests in the tck-runner project via maven: `cd tck-runner && mvn test`

#### Run against Payara Server

There is already a profile for running against remote Payara Server called `payara-server-remote`. To run tests against Payara Server, do the following:

1. Start Payara Server on localhost and default admin port 4848
2. Run tests in the tck-runner project via maven: `cd tck-runner && mvn -Ppayara-server-remote test`

## Implementing the MicroProfile Metrics spec

The specification only relies on Java 8 and CDI 1.1 (1.2), so the implementation only needs to provide CDI and MicroProfile Metrics APIs.