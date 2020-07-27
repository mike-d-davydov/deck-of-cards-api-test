package com.mikedd.deckofcards.tests;

import com.google.common.collect.ImmutableMap;
import com.mikedd.matchers.Validations;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static co.unruly.matchers.Java8Matchers.where;
import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.util.Collections.EMPTY_MAP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import java.util.logging.Logger;

// assumes the current class is called MyLogger

/**
 * These tests cover "/deck/new" endpoint scenarios.
 * Please note, that "/deck/new/shuffle" is out of scope.
 */

public class DeckNewTest {
    private final static Logger LOGGER = Logger.getLogger(DeckNewTest.class.getSimpleName());

    public static void validateNewDeckResponse(Response response, boolean jokersEnabled) {
        LOGGER.info("** Verifying new deck response (jockersEnabled:" + jokersEnabled + ") **" );
        LOGGER.info("Status code:" + response.statusCode());
        LOGGER.info("Status line:" + response.statusLine());
        LOGGER.info("Response:" + response.body().asString());

        assertThat(response.statusCode(), oneOf(200));
        String deckId = response.body().path("deck_id");
        assertThat(deckId, where(Validations::isValidDeckId));
        assertThat(response.body().path("success"), equalTo(true));
        int expectedRemaining = jokersEnabled ? 54 : 52;
        assertThat(response.body().path("remaining"), equalTo(expectedRemaining));
        assertThat(response.body().path("shuffled"), equalTo(false));
        LOGGER.info("** Verification: done");
    }

    public static void validateErrorResponse(Response response, int expectedCode, String pattern) {
        assertThat(response.statusCode(), is(expectedCode));
        // TODO: will this work? Let's see
        assertThat(response.body().asString(), matchesPattern(pattern));
    }

    // Happy path scenarios (GET)

    @Test
    public void testGetNewDeckWithNoParams() {
        validateNewDeckResponse(
                get(getBaseUrl() + "/deck/new/"),
                false
        );
    }

    @Test
    public void testGetNewDeckWithJokersEnabled() {
        validateNewDeckResponse(
                get(getBaseUrl() + "/deck/new?jokers_enabled=true"),
                true
        );
    }

    @Test
    public void testGetNewDeckWithJokersExplicitlyDisabled() {
        validateNewDeckResponse(
                get(getBaseUrl() + "/deck/new?jokers_enabled=false"),
                false
        );
    }

    /*
    Happy path scenarios: POST endpoint
    */

    // TODO: Tests are currently failing. POST API is not well documented
    //  Need to find right way of posting:
    //   - should we post form data, use URI encoding, or post JSON data?
    //   - are any specific headers are required?
    //   - do post requests actually work? It well may be that there is some kind of misconfiguration in Django


    /**
     * prepares POST request from parameters
     * NEED WORK (see TODO above)
     *
     * @param params parameters
     * @return request spec
     */
    private RequestSpecification givenPost(Map<String, Object> params){
        RequestSpecification reqSpec = given().contentType("multipart/form-data");
        for (Map.Entry<String,Object> entry:params.entrySet()){
            reqSpec.multiPart(entry.getKey(), entry.getValue() );
        }
        return reqSpec;
    }

    @Test
    @Disabled("Test is failing with HTTP 301")
    public void testPostNewDeckWithNoParams() {
        validateNewDeckResponse(
                givenPost(EMPTY_MAP)
                        .post(getBaseUrl() + "/deck/new"),
                false
        );
    }


    @Test
    @Disabled("Test is failing with HTTP 403: CSRF verification failed. Request aborted.")
    public void testPostNewDeckWithJokersEnabled() {
        validateNewDeckResponse(
                givenPost(ImmutableMap.of("jokers_enabled", true))
                        .post(getBaseUrl() + "/deck/new/"),
                true
        );
    }

    @Test
    @Disabled("Test is failing with HTTP 403: CSRF verification failed. Request aborted.")
    public void testPostNewDeckWithJokersExplicitlyDisabled() {
        validateNewDeckResponse(
                givenPost(ImmutableMap.of("jokers_enabled", false))
                        .post(getBaseUrl() + "/deck/new/"),
                false
        );
    }

    /* Edge case cases */

    // Invalid data:
    //    1) Jokers enabled - invalid value type
    //    2) Unknown parameter


}
