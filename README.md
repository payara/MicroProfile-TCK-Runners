# MicroProfile TCK Runners

This is a test suite to run MicroProfile TCKs against the Payara Platform.

## Quick Start

To run all the TCKs against a managed server instance run the following command:

~~~
mvn clean verify -Dpayara.version=5.184
~~~

*Note: For all profiles except embedded the distribution will be downloaded from Maven, so make sure it is available either locally or to be fetched. Using patched distributions is explained in a later section.*

## Profiles

### `payara-server-managed`

The default profile is `payara-server-managed`, and means the distribution will be downloaded from maven and ran manually. Other installations should be shutdown before using the default profile.

### `payara-server-remote`

Runs the TCKs against a remote instance. Any configuration required for the TCKs will need to be ran manually. Consult the individual TCK poms for information on required configuration.

### `payara-micro-managed`

Runs the TCKs against a managed Payara Micro instance. Functions otherwise similarly to the `payara-server-managed` profile.

### `payara-server-embedded`

Runs the TCKs against a managed Payara Server embedded instance. Note that some TCKs have some known issues against when running against embedded.

## Using Patched Distributions

To test either a release candidate or a patched distribution e.g. `5.184.2`, you must add your nexus authentication details to your user or global maven configuration (`~/.m2/settings.xml` and `/opt/maven/conf/settings.xml` respectively).

E.g.

~~~
<servers>
    <server>
      <id>payara-staging</id>
      <username>myuser</username>
      <password>mypassword</password>
    </server>
    <server>
      <id>payara-patches</id>
      <username>myuser</username>
      <password>mypassword</password>
    </server>
</servers>
~~~

For information on encrypting your password, see https://maven.apache.org/guides/mini/guide-encryption.html

## Individual TCKs

To run an individual TCK, run the same command as above but with `-f MicroProfile-Config/tck-runner` to run only an individual TCK pom.

For version and TCK specific information see the related folder information:

 - [MicroProfile Config](MicroProfile-Config/README.md)
 - [MicroProfile Fault Tolerance](MicroProfile-Fault-Tolerance/README.md)
 - [MicroProfile Health](MicroProfile-Health/README.md)
 - [MicroProfile JWT-Auth](MicroProfile-JWT-Auth/README.md)
 - [MicroProfile Metrics](MicroProfile-Metrics/README.md)
 - [MicroProfile OpenAPI](MicroProfile-OpenAPI/README.md)
 - [MicroProfile OpenTracing](MicroProfile-OpenTracing/README.md)
 - [MicroProfile Rest-Client](MicroProfile-Rest-Client/README.md)
