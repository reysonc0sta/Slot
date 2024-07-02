package com.bigtech.slot.service.mapper;

import static com.bigtech.slot.domain.DrinkAsserts.*;
import static com.bigtech.slot.domain.DrinkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrinkMapperTest {

    private DrinkMapper drinkMapper;

    @BeforeEach
    void setUp() {
        drinkMapper = new DrinkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDrinkSample1();
        var actual = drinkMapper.toEntity(drinkMapper.toDto(expected));
        assertDrinkAllPropertiesEquals(expected, actual);
    }
}
