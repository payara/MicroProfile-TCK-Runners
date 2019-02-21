# MicroProfile Metrics TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Metrics API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

## Providing the implementation to the TCK runner

An Arquillian container adapter that can connect to the runtime that contains the implementation ([an official guide how to do it](http://arquillian.org/guides/developing_a_container_adapter/))

## Implementing the MicroProfile Metrics spec

The specification only relies on Java 8 and CDI 1.1 (1.2), so the implementation only needs to provide CDI and MicroProfile Metrics APIs.