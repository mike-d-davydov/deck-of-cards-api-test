package com.mikedd.deckofcards.tests;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static com.mikedd.env.Environment.getBaseUrl;
import static org.hamcrest.MatcherAssert.assertThat;


public class SelfTest {
    private final static Logger LOGGER = Logger.getLogger(SelfTest.class.getSimpleName());

    @Test
    public void testPropertyFileLoaded() {
        LOGGER.info("BaseURL is: " + getBaseUrl());
        assertThat(getBaseUrl(), Matchers.notNullValue());
    }
}
