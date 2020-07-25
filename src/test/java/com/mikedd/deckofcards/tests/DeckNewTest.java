package com.mikedd.deckofcards.tests;

import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Test;

import static co.unruly.matchers.Java8Matchers.where;
import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.get;
import static io.restassured.path.json.JsonPath.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeckNewTest {

    static String baseUrl = getBaseUrl();

    @Test
    public void testNewDeckWithNoParams() {
        ///api/deck/new/
        assertThat(baseUrl, equalTo("https://deckofcardsapi.com"));

        Response response = get(baseUrl + "/api/deck/new");
        ResponseBody body = response.getBody();
        String deckId = body.jsonPath().getString("deck_id");
        assertThat(response.statusCode(), is(200));
        assertThat(deckId, where(this::isValidDeckId));
        assertThat(body.path("success"),  equalTo(true));
        response
                .then()
                .statusCode(200)
                .body("remaining", equalTo(52))
                .body("shuffled", equalTo(false));
    }

    private boolean isValidDeckId(String deckId) {
        return deckId.length() == 12 && deckId.matches("^\\w+$");
    }


}
