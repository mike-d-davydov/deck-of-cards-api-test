# deck-of-cards-api-test

Java API tests for a subset of functionality of [deckofcardsapi.com](http://deckofcardsapi.com) service.

## Scope and other considerations
  
  These tests are testing public API of [deckofcardsapi.com](http://deckofcardsapi.com) service according to its documentation.
  
  Please note, that aspects of implementation, such as headers returned by API, particular error codes are either not
   tested at all (headers), or tested to minimal extent with accordance with common sense 
   (e.g., in response for incorrectly provided parameters of certain endpoints we may reasonably expect 
   *HTTP 400* error as per HTTP protocol specification).
