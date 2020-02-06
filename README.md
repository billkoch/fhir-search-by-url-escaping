# FHIR Search By URL Escaping
This is a barebones application to demonstrate an issue I ran into attempting to use HAPI FHIR's `IGenericClient.search().byUrl()` feature.

## Background
While using Apache Camel's FHIR Component to search for a `Patient` by an `Identifier`, I ran into a `java.net.URISyntaxException` when the `Identifier`'s value contained any leading spaces:
```
Caused by: java.lang.IllegalArgumentException: Illegal character in query at index 72: http://hapi.fhir.org/baseR4/Patient?identifier=http%3A//acme.org/mrns%7C aa47e1e1-998f-4f27-b7bb-21144edac693
        at java.base/java.net.URI.create(URI.java:883) ~[na:na]
        at org.apache.http.client.methods.HttpGet.<init>(HttpGet.java:66) ~[httpclient-4.5.10.jar:4.5.10]
        at ca.uhn.fhir.rest.client.apache.ApacheHttpClient.constructRequestBase(ApacheHttpClient.java:76) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.apache.ApacheHttpClient.createHttpRequest(ApacheHttpClient.java:109) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.apache.ApacheHttpClient.createHttpRequest(ApacheHttpClient.java:92) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.apache.BaseHttpClient.createGetRequest(BaseHttpClient.java:106) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.impl.BaseHttpClientInvocation.createHttpRequest(BaseHttpClientInvocation.java:77) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.method.HttpGetClientInvocation.asHttpRequest(HttpGetClientInvocation.java:98) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.impl.BaseClient.invokeClient(BaseClient.java:265) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.impl.GenericClient$BaseClientExecutable.invoke(GenericClient.java:434) ~[hapi-fhir-client-4.1.0.jar:na]
        at ca.uhn.fhir.rest.client.impl.GenericClient$SearchInternal.execute(GenericClient.java:1841) ~[hapi-fhir-client-4.1.0.jar:na]
        at com.example.Application.run(Application.java:54) ~[main/:na]
        at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
        ... 5 common frames omitted
Caused by: java.net.URISyntaxException: Illegal character in query at index 72: http://hapi.fhir.org/baseR4/Patient?identifier=http%3A//acme.org/mrns%7C aa47e1e1-998f-4f27-b7bb-21144edac693
        at java.base/java.net.URI$Parser.fail(URI.java:2915) ~[na:na]
        at java.base/java.net.URI$Parser.checkChars(URI.java:3086) ~[na:na]
        at java.base/java.net.URI$Parser.parseHierarchical(URI.java:3174) ~[na:na]
        at java.base/java.net.URI$Parser.parse(URI.java:3116) ~[na:na]
        at java.base/java.net.URI.<init>(URI.java:600) ~[na:na]
        at java.base/java.net.URI.create(URI.java:881) ~[na:na]
```

## What Does The Application Do?
This application will create a new Patient (via FHIR's transaction bundle) that has an Identifier whose value contains a leading space. After creating the Patient it will attempt a Patient search by URL, using the Identifier of the newly created patient. 

## Running The Application
```
./gradlew bootRun
```
> :bulb: You'll need to install Java 11 to run the application. Consider using [SDKMAN!](https://sdkman.io/install) to manage JDKs on your machine.

## Branch Descriptions
This repository has two branches:
1. [bug](https://github.com/billkoch/fhir-search-by-url-escaping/tree/bug) which demonstrates the issue
1. [hacky-fix](https://github.com/billkoch/fhir-search-by-url-escaping/tree/hacky-fix) which leverages the [PercentParser used in HAPI FHIR's UrlUtil](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-base/src/main/java/ca/uhn/fhir/util/UrlUtil.java#L156) to encode the `Identifier`'s value

## Other Points Of Interest
1. `ca.uhn.fhir.rest.client.impl.GenericClient` contains a [case statement which selectively URL encodes only certain characters](https://github.com/jamesagnew/hapi-fhir/blob/master/hapi-fhir-client/src/main/java/ca/uhn/fhir/rest/client/impl/GenericClient.java#L2344)
