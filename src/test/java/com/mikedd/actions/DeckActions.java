package com.mikedd.actions;

import io.restassured.response.Response;

import java.util.logging.Logger;

import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.get;

/**
 * Helper class that uses deck id API for certain test setup, teardown and verification operations
 */
public class DeckActions {
    private final static Logger LOGGER = Logger.getLogger(DeckActions.class.getSimpleName());
    static String baseUrl = getBaseUrl();

    /**
     * Create a new deck with no jokers
     *
     * @return deck id
     */
    public static String newDeck() {
        return newDeck(false);
    }

    /**
     * Creates new deck, returns deck id
     *
     * @param includeJokers whether to include jokers in deck
     * @return deck id
     */
    public static String newDeck(boolean includeJokers) {
        LOGGER.info("Serving new deck" + (includeJokers ? " including jokers" : ""));
        Response response;
        if (includeJokers) {
            response = get(baseUrl + "/deck/new?jokers_enabled=true");
        } else {
            response = get(baseUrl + "/deck/new");
        }

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to get new deck of cards:" + response.getStatusLine());
        }
        return response.body().path("deck_id");
    }

    /**
     * Returns, how many are there in a deck
     *
     * @param deckId target deck id
     * @return cards remaining
     */
    public static int remainingCards(String deckId) {
        Response response = get(baseUrl + "/deck/" + deckId);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to get new deck of cards:" + response.getStatusLine());
        }
        return response.body().path("remaining");
    }

    /**
     * Draws [numberOfCardsToDraw] cards, returns cards drawn
     *
     * @param deckId              deck id to draw cards from
     * @param numberOfCardsToDraw number of cards to draw
     * @return list of card JSON objects
     */
    public static Iterable<Object> drawCards(String deckId, int numberOfCardsToDraw) {
        Response response = get(getBaseUrl() + "/deck/" + deckId + "/draw?count=" + numberOfCardsToDraw);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to draw cards:" + response.getStatusLine());
        }

        return response.body().path("cards");
    }
}
