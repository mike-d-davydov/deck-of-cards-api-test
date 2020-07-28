package com.mikedd.matchers;

import com.google.common.collect.ImmutableSet;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;
import java.util.logging.Logger;

import static co.unruly.matchers.Java8Matchers.where;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Validations {
    final static Set<String> SUITS = ImmutableSet.of("HEARTS", "DIAMONDS", "SPADES", "CLUBS");
    final static Set<String> VALUES = ImmutableSet.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING", "ACE");
    final static UrlValidator urlValidator = new UrlValidator();
    private final static Logger LOGGER = Logger.getLogger(Validations.class.getSimpleName());

    // TODO: consider extracting to separate class, along with other functions related to app model

    /**
     * Get one-letter suit code from full suit name, e.g., HEARTS -> H
     *
     * @param suit card suit name, e.g. DIAMONDS (CASE-SENSITIVE - all UPPERCASE expected!)
     * @return 1-letter code (D)
     */
    public static String getSuitCode(String suit) {
        return SUITS.stream().filter(s -> s.equals(suit))
                .findFirst()
                .orElseThrow(() -> new Error("Invalid suit:" + suit)).substring(0, 1);

    }

    /**
     * Converts card value name, e.g. "JACK" into 1-letter code, e.g. "J"
     *
     * @param value card value name. (CASE-SENSITIVE - all UPPERCASE expected!)
     * @return 1-letter code, e.g. 0 for 10
     */
    public static String getValueCode(String value) {
        // TODO: consider less hacky way of getting value of '10'
        return VALUES.stream().filter(s -> s.equals(value))
                .findFirst()
                .orElseThrow(() -> new Error("Invalid value:" + value))
                .substring(0, 1).replace("1", "0");
    }

    /**
     * Converts card value and suit into code (e.g., KING of HEARTS to "KH")
     *
     * @param value string - card value, e.g, "10"
     * @param suit  string - card suit, e.g. "HEARTS"
     * @return card code, e.g. "0H" for 10 of HEARTS
     */
    public static String getCardCode(String value, String suit) {
        return getValueCode(value) + getSuitCode(suit);
    }


    /**
     * Predicate, checking if string is valid deck id (that is, looks like one)
     * At the moment, it only checks that it consists of numbers and letters
     *
     * @param deckId deck_id to verify
     * @return boolean verification result
     */
    public static boolean isValidDeckId(String deckId) {
        // TODO: consider adding more sophisticated checks
        return deckId.length() == 12 && deckId.matches("^\\w+$");
    }


    /**
     * Predicate, checking if string is valid image URL
     * At the moment, it only checks that it's valid URL
     *
     * @param mayBeUrl string to be checked
     * @return boolean verification result
     */
    public static boolean isImageURL(String mayBeUrl) {
        // TODO: We may want to add more checks in the future
        return urlValidator.isValid(mayBeUrl);
    }


    /**
     * Check that body contains a valid card object accessible by [cardPath]
     *
     * @param body     - restAssured response body
     * @param cardPath - jsonPath path to card, e.g. "cards[0]"
     */
    public static void validateCard(ResponseBody body, String cardPath) {
        /*  Data example
         *
         *         {
         *             "image": "https:/deckofcardsapi.com/static/img/KH.png",
         *             "value": "KING",
         *             "suit": "HEARTS",
         *             "code": "KH"
         *         },
         */
        final String image = body.path(cardPath + ".image");
        final String suit = body.path(cardPath + ".suit");
        final String value = body.path(cardPath + ".value");
        String code = body.path(cardPath + ".code");

        assertThat(image, where(Validations::isImageURL));
        // This way we check value, suit and code all at once
        assertThat(code, equalTo(getCardCode(value, suit)));
    }

    /**
     * Validate error response matches expected HTTP code and body matches regex pattern
     *
     * @param response     restAssured Response object
     * @param expectedCode expected HTTP code, e.g. 400
     * @param pattern      regular expression used to verify response body
     */
    public static void validateErrorResponse(Response response, int expectedCode, String pattern) {
        LOGGER.info("Validating response - expecting HTTP code: " + expectedCode + " and body matching regex pattern [" + pattern + "]");
        LOGGER.info("Response is:" + response.asString());
        assertThat(response.statusCode(), is(expectedCode));
        assertThat(response.body().asString(), matchesPattern(pattern));
    }
}
