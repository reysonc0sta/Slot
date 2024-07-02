package com.bigtech.slot.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DrinkCriteriaTest {

    @Test
    void newDrinkCriteriaHasAllFiltersNullTest() {
        var drinkCriteria = new DrinkCriteria();
        assertThat(drinkCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void drinkCriteriaFluentMethodsCreatesFiltersTest() {
        var drinkCriteria = new DrinkCriteria();

        setAllFilters(drinkCriteria);

        assertThat(drinkCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void drinkCriteriaCopyCreatesNullFilterTest() {
        var drinkCriteria = new DrinkCriteria();
        var copy = drinkCriteria.copy();

        assertThat(drinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(drinkCriteria)
        );
    }

    @Test
    void drinkCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var drinkCriteria = new DrinkCriteria();
        setAllFilters(drinkCriteria);

        var copy = drinkCriteria.copy();

        assertThat(drinkCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(drinkCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var drinkCriteria = new DrinkCriteria();

        assertThat(drinkCriteria).hasToString("DrinkCriteria{}");
    }

    private static void setAllFilters(DrinkCriteria drinkCriteria) {
        drinkCriteria.id();
        drinkCriteria.nome();
        drinkCriteria.marca();
        drinkCriteria.distinct();
    }

    private static Condition<DrinkCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getMarca()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DrinkCriteria> copyFiltersAre(DrinkCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getMarca(), copy.getMarca()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
