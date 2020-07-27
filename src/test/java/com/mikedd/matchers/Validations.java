package com.mikedd.matchers;

import com.google.common.collect.ImmutableSet;
import io.restassured.response.ResponseBody;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;

import static co.unruly.matchers.Java8Matchers.where;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class Validations {
    final static Set<String> SUITS = ImmutableSet.of("HEARTS", "DIAMONDS", "SPADES", "CLUBS");
    final static Set<String> VALUES = ImmutableSet.of("2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING", "ACE");
    final static UrlValidator urlValidator = new UrlValidator();


    public static String getSuitCode(String suit) {
        return SUITS.stream().filter(s -> s.equals(suit))
                .findFirst()
                .orElseThrow(() -> new Error("Invalid suit:" + suit)).substring(0,1);

    }

    public static String getValueCode(String value) {
        // TODO: consider less hacky way of getting value of '10'
        return VALUES.stream().filter(s -> s.equals(value))
                .findFirst()
                .orElseThrow(() -> new Error("Invalid value:" + value))
                .substring(0,1).replace("1", "0");
    }

    public static String getCardCode(String value, String suit){
        return getValueCode(value) + getSuitCode(suit);
    }


    public static boolean isValidDeckId(String deckId) {
        return deckId.length() == 12 && deckId.matches("^\\w+$");
    }


    public static boolean isImageURL(String mayBeUrl) {
        // We may want to add more checks in the future
        return urlValidator.isValid(mayBeUrl);
    }


    public static void validateCard(ResponseBody body, String cardPath) {
        /*
         *  {
         *             "image": "https:/deckofcardsapi.com/static/img/KH.png",
         *             "value": "KING",
         *             "suit": "HEARTS",
         *             "code": "KH"
         *         },
         *
         *
         */
        final String image = body.path(cardPath + ".image");
        final String suit = body.path(cardPath + ".suit");
        final String value = body.path(cardPath + ".value");
        String code = body.path(cardPath + ".code");

        assertThat(image, where(Validations::isImageURL));
        // This way we check value, suit and code all at once
        assertThat(code, equalTo(getCardCode(value, suit)));
    }
}
