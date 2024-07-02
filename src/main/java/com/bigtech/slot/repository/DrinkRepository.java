package com.bigtech.slot.repository;

import com.bigtech.slot.domain.Drink;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Drink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long>, JpaSpecificationExecutor<Drink> {}
