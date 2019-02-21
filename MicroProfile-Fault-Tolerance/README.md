# MicroProfile Fault Tolerance TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Fault Tolerance API, which depends on CDI 1.1 API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

**NOTES:**

* The illegalConfig and invalidParameters tests are disabled due to the tests a) expecting failures earlier than Payara throws them, and b) looking for the wrong exception.
* Several timeout tests are disabled due to jenkins failures. These can be run with the `unstable` profile.