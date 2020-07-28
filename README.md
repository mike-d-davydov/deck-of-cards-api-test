# deck-of-cards-api-test

Java API tests for a subset of functionality of [deckofcardsapi.com](http://deckofcardsapi.com) service.

## Scope 
  
  These tests are testing public API of [deckofcardsapi.com](http://deckofcardsapi.com) service according to its documentation.
  
  Please note, that aspects of implementation, such as headers returned by and/or required to be sent to API endpoints, particular error codes - are either not
   being tested at all (headers), or are tested to minimal extent in accordance with common sense 
   (e.g., in response for incorrectly provided parameters of certain endpoints we may reasonably expect 
   *HTTP 400* error as per HTTP protocol specification).

 Other test coverage limitations are mentioned in relevant test documentation.
    
## Design considerations

Because of limited/demo scope of the project, following design decisions / assumptions were made:
* No any dependency injection or Enterprise frameworks (Guice, Spring etc.) are used to keep code simple
* In a larger-scope project, API tests should use robust reporting / logging framework, such as Allure. Here we use SureFire + Java Logging API.
* All libraries used by tests are located in "test" folder because we don't plan to export them or re-use from other projects.
* We use a lot of "utility" classes with static methods to keep things simple (and because no dependency injection framework is being used). 

## Failing and Broken tests

  Some tests, implemented with "common sense" considerations in mind are failing at the moment of implementation,
  others can't be properly implemented without knowing some application internals (e.g., tests for POST requests) 

  * Tests that are currently failing (by the time of implementation) because if of perceived application bugs are tagged as "failing".
  * Tests that are currently failing (by the time of implementation) because of missing information (e.g., POST requests) are tagged as "broken".

## Running tests

### Preconditions

You will need properly setup JAVA 9 JDK and Maven 3+. All commands below should be run from project base folder.

### Running passing tests

By default, only tests that were passing when implemented (regression suite) are being run:

```
mvn test 
```   

You will want to run tests this way from CI pipeline

### Running tests including failing ones

Maven profile "include-failing" controls running failing tests:

```
mvn test -P inlclude-failing 
``` 

### Running all tests including failing and broken ones

Maven profile "all-tests" controls allow running all tests, including failing and broken:

```
mvn test -P all-tests 
``` 

### Running against localhost

By default, tests are being run against production environment (https://deckofcardsapi.com)
To run them against different environments (e.g., localhost:8080), you can use (add as needed) relevant profiles.

Example:
```
mvn test -P local
```
will run tests against 'localhost:8080'
