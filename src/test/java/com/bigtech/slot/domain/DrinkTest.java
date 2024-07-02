package com.bigtech.slot.domain;

import static com.bigtech.slot.domain.DrinkTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bigtech.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DrinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Drink.class);
        Drink drink1 = getDrinkSample1();
        Drink drink2 = new Drink();
        assertThat(drink1).isNotEqualTo(drink2);

        drink2.setId(drink1.getId());
        assertThat(drink1).isEqualTo(drink2);

        drink2 = getDrinkSample2();
        assertThat(drink1).isNotEqualTo(drink2);
    }
}
