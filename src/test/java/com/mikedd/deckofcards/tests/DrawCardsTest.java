package com.mikedd.deckofcards.tests;

import com.mikedd.actions.DeckActions;
import com.mikedd.matchers.Validations;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DrawCardsTest {
    String deckId = "";

    @BeforeEach
    public void initializeDeck(){
        deckId =  DeckActions.newDeck();
    }


    @Test
    public void testDrawOneCardNewDeck(){
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=1");
        response.prettyPrint();
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", iterableWithSize(1))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(51));


        Validations.validateCard(response.body(), "cards[0]");
    }

    // Edge cases: draw more cards than there is in a deck - 53 cards out of 52 deck , 2 cards out of 1, 1 card out of 0
    // Unknown deck
    // Unknown parameter(s)
    // Wrong parameter type for count
}
