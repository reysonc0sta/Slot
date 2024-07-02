package com.bigtech.slot.service;

import com.bigtech.slot.service.dto.DrinkDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.bigtech.slot.domain.Drink}.
 */
public interface DrinkService {
    /**
     * Save a drink.
     *
     * @param drinkDTO the entity to save.
     * @return the persisted entity.
     */
    DrinkDTO save(DrinkDTO drinkDTO);

    /**
     * Updates a drink.
     *
     * @param drinkDTO the entity to update.
     * @return the persisted entity.
     */
    DrinkDTO update(DrinkDTO drinkDTO);

    /**
     * Partially updates a drink.
     *
     * @param drinkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DrinkDTO> partialUpdate(DrinkDTO drinkDTO);

    /**
     * Get the "id" drink.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DrinkDTO> findOne(Long id);

    /**
     * Delete the "id" drink.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
