package com.bigtech.slot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DrinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Drink getDrinkSample1() {
        return new Drink().id(1L).nome("nome1").marca("marca1");
    }

    public static Drink getDrinkSample2() {
        return new Drink().id(2L).nome("nome2").marca("marca2");
    }

    public static Drink getDrinkRandomSampleGenerator() {
        return new Drink().id(longCount.incrementAndGet()).nome(UUID.randomUUID().toString()).marca(UUID.randomUUID().toString());
    }
}
