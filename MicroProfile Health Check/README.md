# MicroProfile Config TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation
 - weld-arquillian-extension - a template extension to use if the runner project provide the implementation via an arquillian extension

# Quick start

To run tests against Payara Server, see [Run against Payara Server](#Run against Payara Server) below.

There are no instructions to run against Payara Micro yet. This requires an arquillian adapter for Payara Micro which isn't ready yet.

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Config API, which depends on CDI 1.1 API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

## Providing the implementation to the TCK runner

There are 2 ways to provide the implementation:

 1. as an Arquillian container adapter that can connect to the runtime that contains the implementation ([an official guide how to do it](http://arquillian.org/guides/developing_a_container_adapter/))
 2. as an Arquillian extension that deploys the required classes and files together with the deployment archive (bundled with the test application) (an example how to do it is in a [sample config implementation](https://github.com/struberg/javaConfig/tree/master/impl/src/test) by Mark Struberg - a service loader file and 2 simple classes in test scope)

The option 2 (extension) is probably much simpler, although doing the option 1 (container adapter) could be a base for a proper Arquillian container adapter for Payara Micro and MicroProfile, which would be useful also for users and wider adoption of our implementation.

## Running the tests

How to run the tests:
 - tests can be executed with the tck-runner project - go to the tck-runner project's directory and run `mvn test`

The tck-runner project executes the tests in the microprofile-config-tck artifact. The tests will fail, because no implementation is provided yet. One of the 2 following methods can be used to provide an implementation.

(general info about running the TCK can be found in the MP config repo: [running_the_tck.asciidoc](https://github.com/eclipse/microprofile-config/blob/master/tck/running_the_tck.asciidoc) )

### Running the tests with an Arquillian container

1. add configuration for the arquillian container adapter into the tck-runner maven project (adapter dependencies,optional arquillian configuration) - it is necesary to implement a container adapter for the implementation if it's not available yet
2. run tests in the tck-runner project via maven: `cd tck-runner && mvn test`

#### Run against Payara Server

There is already a profile for running against remote Payara Server called `payara-server-remote`. To run tests against Payara Server, do the following:


1. Start Payara Server on localhost and default admin port 4848
2. Run tests in the tck-runner project via maven: `cd tck-runner && mvn -Ppayara-server-remote test`

### Running the tests with an Arquillian extension

1. setup Arquillian to test against a CDI container (such as Weld or OpenWebBeans container) - add appropriate dependencies into the maven project
2. provide an Arquillian extension, either as a dependency, or directly in the test classpath

The tck-runner project contains configuration for running the TCK with Weld container adapter (a standalone weld, which provides CDI API). To use it, you can run the TCK with the `weld` profile. 
An example Arquillian extension is in the weld-arquillian-extension, which is a required dependency for the `weld` profile - you need to build it first.

In short:

1. mvn -f weld-arquillian-extension/pom.xml install
2. mvn -f tck-runner/pom.xml -Pweld test

The tests will again fail until the project weld-arquillian-extension provides a working implementation classes and files via the `PayaraArchiveProcessor.class` .

## Implementing the MicroProfile Config spec

The specification only relies on Java 8 and CDI 1.1 (1.2), so the implementation only needs to provide CDI and MicroProfile Config APIs.

See the README of [microprofile-config](https://github.com/eclipse/microprofile-config) to get more details about MicroProfile Config. Especially the [list of implementations](https://github.com/eclipse/microprofile-config#implementaions) could be interesting for implementers. It's not a list of MicroProfile Config impls, but rather a list of third-party configuration frameworks which can be used by the MP Config implementation to provide a meaningful solution.

A sample implementation (only the necessary minimum to pass the TCK) can be found at in [Mark Struberg's repo](https://github.com/struberg/javaConfig/tree/master/impl) (under APLv2 license). If only this implementation is copied to Payara MicroProfile, it should pass the TCK. However, we might want to enhance it to fit the internals, or rewrite it completely and build it upon a third-party library that provides configuration. However, Mark Struberg's implementation is quite handy as an example how to implement the CDI functionality - there are some tricks worth observing.