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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;

public class DrawCardsTest {
    String deckId = "";

    @BeforeEach
    public void initializeDeck(){
        deckId =  DeckActions.newDeck();
    }


    @Test
    public void testDrawOneCardNewDeck(){
        // https:/deckofcardsapi.com/deck/<<deck_id>>/draw/?count=2


        Response response = get(basePath + "/deck/$" + deckId + "/draw?count=1");
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", hasLength((1)))
                .body("deckId", equalTo(deckId))
                .body("remaining", equalTo(51));

        Validations.validateCard(response.body(), "cards[0]");

//        Object value = response

    }
}
