package com.bigtech.slot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bigtech.slot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DrinkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrinkDTO.class);
        DrinkDTO drinkDTO1 = new DrinkDTO();
        drinkDTO1.setId(1L);
        DrinkDTO drinkDTO2 = new DrinkDTO();
        assertThat(drinkDTO1).isNotEqualTo(drinkDTO2);
        drinkDTO2.setId(drinkDTO1.getId());
        assertThat(drinkDTO1).isEqualTo(drinkDTO2);
        drinkDTO2.setId(2L);
        assertThat(drinkDTO1).isNotEqualTo(drinkDTO2);
        drinkDTO1.setId(null);
        assertThat(drinkDTO1).isNotEqualTo(drinkDTO2);
    }
}
