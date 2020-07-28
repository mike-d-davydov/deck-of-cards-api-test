package com.mikedd.deckofcards.tests;

import com.mikedd.actions.DeckActions;
import com.mikedd.matchers.Validations;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;

/**
 * Tests https://deckofcardsapi.com/api/deck/<<deck_id>>/draw/?count=<<number_of_cards>> endpoint
 * Scope:
 * - no piles
 * - no multi-decks (may need to add tests in a different class)
 * - other scope considerations - see README.md
 */
public class DrawCardsTest {
    private final static Logger LOGGER = Logger.getLogger(DrawCardsTest.class.getSimpleName());

    String deckId = "";

    @BeforeEach
    public void initializeDeck() {
        deckId = DeckActions.newDeck();
    }

    @Test
    @DisplayName("GET deck/<<deck_id>>/draw - draw one card from a new deck")
    public void testDrawOneCardNewDeck() {
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=1");
        LOGGER.info("testDrawOneCardNewDeck: Response is: " + response.asString());
        response.prettyPrint();
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", iterableWithSize(1))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(51));


        Validations.validateCard(response.body(), "cards[0]");
    }

    @Test
    @DisplayName("GET deck/<<deck_id>>/draw - draw several cards from new deck")
    public void testDrawSeveralCardsFromNewDeck() {
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=7");
        LOGGER.info("testDrawSeveralCards: Response is: " + response.asString());
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", iterableWithSize(7))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(45));

        // Validate cards drawn

        Set<Object> cards = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Validations.validateCard(response.body(), "cards[" + i + "]");
            // We are using HashSet as means of making sure that all items in iterable are different ones
            cards.add(response.body().path("cards[" + i + "]").toString());
        }
        LOGGER.info("Cards are:" + cards.toString());
        assertThat("All cards drawn are different", cards, iterableWithSize(7));
    }

    // TODO: add case:
    //  - try drawing cards from deck that has already been used

    // Special cases

    @Test
    @DisplayName("GET deck/<<deck_id>>/draw with no parameter - should draw one card")
    public void testDrawCardsUsingNoParameter() {
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw");
        LOGGER.info("testDrawCardsUsingNoParameter response is:" + response.asString());
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", iterableWithSize(1))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(51));

        Validations.validateCard(response.body(), "cards[0]");
    }

    @Test
    @DisplayName("GET deck/<<deck_id>>/draw - draw last card")
    public void testDrawLastCard() {
        DeckActions.drawCards(deckId, 51);
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=1");
        LOGGER.info("testDrawOneCardNewDeck: Response is: " + response.asString());
        response.prettyPrint();
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("cards", iterableWithSize(1))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(0));

        Validations.validateCard(response.body(), "cards[0]");
    }

    @Test
    @DisplayName("GET deck/<<deck_id>>/draw: request more cards that are remaining in a deck")
    public void testDrawMoreCardsThanThereIsInTheDeck() {
        DeckActions.drawCards(deckId, 50);

        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=4");

        LOGGER.info("testDrawMoreCardsThanThereIsInTheDeck: Response is: " + response.asString());
        response.then()
                .statusCode(200)
                .body("success", equalTo(false))
                .body("cards", iterableWithSize(2))
                .body("deck_id", equalTo(deckId))
                .body("remaining", equalTo(0))
                // TODO: not sure if error message is correct - in fact we could not draw just 2 more cards, not 4
                .body("error", equalTo("Not enough cards remaining to draw 4 additional"));

        // Validate cards drawn

        Set<Object> cards = new HashSet<>();
        for (int i = 0; i < 2; i++) {
            Validations.validateCard(response.body(), "cards[" + i + "]");
            // We are using HashSet as means of making sure that all items in iterable are different ones
            cards.add(response.body().path("cards[" + i + "]").toString());
        }
        LOGGER.info("Cards are:" + cards.toString());
        assertThat("All cards drawn are different", cards, iterableWithSize(2));
    }

    // TODO: add case
    //  - draw cards when no cards remaining

    // Edge cases

    @Test
    @Tag("failing")
    @DisplayName("GET deck/<<deck_id>>/draw - card from unknown deck. Currently returns 500, should return 400")
    public void testDrawCardsFromUnknownDeck() {
        Response response = get(getBaseUrl() + "/deck/6l7j5tgbd45q/draw?count=1");
        // TODO: We don't know how valid error response should look like
        Validations.validateErrorResponse(response, 400, ".+");
    }

    @Tag("failing")
    @Test
    @DisplayName("GET deck/<<deck_id>>/draw - use unknown parameter ")
    public void testDrawCardsUsingInvalidParameter() {
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?countess=1");
        LOGGER.info("testDrawCardsUsingInvalidParameter:" + response.asString());
        // TODO: I am not 100% sure what expected result should be. My reasoning is that all invalid parameters
        //  should return in 400 and if they don't, API documentation should clearly state this.
        Validations.validateErrorResponse(response, 400, ".+");
    }

    @Test
    @Tag("failing")
    @DisplayName("GET deck/<<deck_id>>/draw - use invalid type for count parameter")
    public void testDrawCardsUsingInvalidTypeForCountParameter() {
        Response response = get(getBaseUrl() + "/deck/\"+ deckId + \"draw?count=true");
        LOGGER.info("testDrawCardsUsingInvalidTypeForCountParameter:" + response.asString());
        // TODO: We don't know how valid error response should look like
        Validations.validateErrorResponse(response, 400, ".+");
    }

}
