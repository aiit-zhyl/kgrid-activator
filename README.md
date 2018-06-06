# KGrid Activator
[![CircleCI](https://circleci.com/gh/kgrid/kgrid-activator/tree/master.svg?style=shield)](https://circleci.com/gh/kgrid/kgrid-activator/tree/master)
[![latest release](https://img.shields.io/badge/release%20notes-1.x-yellow.svg)](https://github.com/mockito/mockito/blob/release/!.x/doc/release-notes/official.md)

As a key component of Knowledge Grid, an activator allows knowledge objects to be executable against collected data. 
Activators provide a horizontally scalable means to put Knowledge Objects to work in the real world. 
Activators may run payload code in one or more forms or languages, may reason over assertions in payloads, may deliver 
payloads to other applications, and may also serve as access points to services provided by others. When Knowledge Objects 
are moved from the Object Library to an Activator, they automatically become services that can process health
instance data and generate messages of advice.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
For building and running the application you need:

- [Git](https://git-scm.com/downloads)
- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

### Clone
To get started you can simply clone this repository using git:
```
git clone https://github.com/kgrid/kgrid-activator.git
cd kgrid-activator
```

### Quick start
This quick start will run the activator and load two example knowledge objects for testing.  This objects are located
in the _shelf_ directory at the root of the project. By default application will start up and PORT 8080.
```
mvn clean package
java -jar target/kgrid-activator*.jar
```
Example of how to set KO shelf (_where to look for the KOs_)
```
java -jar target/kgrid-activator*.jar --kgrid.shelf.cdostore.filesystem.location=/tmp/shelf
```
Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```
mvn clean spring-boot:run
```

Once Running access the [Activators Health Endpoint](http://localhost:8080/health).  All _statuses_ reported should be **UP**

```$xslt
"status": "UP",
    "shelf": {
        "status": "UP",
    
    ...
    
    "activationService": {
            "status": "UP",
    
    ...
    
   "diskSpace": {
           "status": "UP",     
```

## Running the tests

#### Automated tests 
Unit and Integration tests can be executed via
```
mvn clean test
mvn clean verify
```

#### End to End Testing

Sample shelf in place the following tests can be executed against the running activator

View a Knowledge Object

```
curl http://localhost:8080/99999/newko
```

View a Knowledge Object Version

```
curl http://localhost:8080/99999/newko/v0.0.1
```

Run the welcome endpoint on the 99999/newko/v0.0.1 knowledge object
```
curl -X POST -H "Content-Type:application/json"  -d "{\"name\": \"Fred Flintstone\"}" http://localhost:8080/99999/newko/v0.0.1/welcome

```


## Running the tests



## Additional Information