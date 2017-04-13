# MicroProfile Config TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation
 - weld-arquillian-extension - a template extension to use if the runner project provide the implementation via an arquillian extension

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Config API, which depends on CDI 1.1 API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

## Providing the implementation to the TCK runner

There are 2 ways to provide the implementation:

 1. as a custom Arquillian container adapter that can connect to the runtime that contains the implementation ([an official guide how to do it](http://arquillian.org/guides/developing_a_container_adapter/))
 2. as an Arquillian extension that deploys the required classes and files together with the deployment archive (bundled with the test application) (an example how to do it is in a [sample config implementation](https://github.com/struberg/javaConfig/tree/master/impl/src/test) by Mark Struberg - a service loader file and 2 simple classes in test scope)

The option 2 (extension) is probably much simpler, although doing the option 1 (container adapter) could be a base for a proper Arquillian container adapter for Payara Micro and MicroProfile, which would be useful also for users and wider adoption of our implementation.

### Running the tests with a custom Arquillian container

1. add configuration for the arquillian container adapter into the tck-runner maven project (adapter dependencies,optional arquillian configuration)
2. run tests in the tck-runner project via maven: `cd tck-runner && mvn test`

### Running the tests with an Arquillian extension

1. setup Arquillian to test against a CDI container (such as Weld or OpenWebBeans container) - add appropriate dependencies into the maven project
2. provide an Arquillian extension, either as a dependency, or directly in the test classpath

The tck-runner project contains configuration for running the TCK with Weld adapter. To use it, you can run the TCK with the `weld` profile. An example Arquillian extension is in the weld-arquillian-extension, which is a required dependency for the `weld` profile - you need to build it first.

## Implementing the MicroProfile Config spec

The specification only relies on Java 8 and CDI 1.1 (1.2)