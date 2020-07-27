package com.mikedd.actions;

import io.restassured.response.Response;

import static com.mikedd.env.Environment.getBaseUrl;
import static io.restassured.RestAssured.get;

public class DeckActions {
    static String baseUrl = getBaseUrl();

    public static String newDeck(){
        return newDeck(false);
    }

    public static String newDeck(boolean includeJokers){
        Response response;
        if (includeJokers){
            response = get(baseUrl + "/deck/new?jokers_enabled=true");
        } else {
            response = get(baseUrl + "/deck/new");
        }

        if (response.getStatusCode()!=200){
            throw new RuntimeException("Failed to get new deck of cards:" + response.getStatusLine());
        }
        return response.body().path("deck_id");
    }

    public static int remainingCards(String deckId){
        Response response = get(baseUrl + "/deck/" + deckId);
        if (response.getStatusCode()!=200){
            throw new RuntimeException("Failed to get new deck of cards:" + response.getStatusLine());
        }
        return response.body().path("remaining");
    }
}
