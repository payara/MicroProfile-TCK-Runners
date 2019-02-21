# MicroProfile OpenAPI TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile OpenAPI API, which depends on the Config API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.