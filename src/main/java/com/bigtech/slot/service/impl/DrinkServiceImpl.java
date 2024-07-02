package com.bigtech.slot.service.impl;

import com.bigtech.slot.domain.Drink;
import com.bigtech.slot.repository.DrinkRepository;
import com.bigtech.slot.service.DrinkService;
import com.bigtech.slot.service.dto.DrinkDTO;
import com.bigtech.slot.service.mapper.DrinkMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bigtech.slot.domain.Drink}.
 */
@Service
@Transactional
public class DrinkServiceImpl implements DrinkService {

    private static final Logger log = LoggerFactory.getLogger(DrinkServiceImpl.class);

    private final DrinkRepository drinkRepository;

    private final DrinkMapper drinkMapper;

    public DrinkServiceImpl(DrinkRepository drinkRepository, DrinkMapper drinkMapper) {
        this.drinkRepository = drinkRepository;
        this.drinkMapper = drinkMapper;
    }

    @Override
    public DrinkDTO save(DrinkDTO drinkDTO) {
        log.debug("Request to save Drink : {}", drinkDTO);
        Drink drink = drinkMapper.toEntity(drinkDTO);
        drink = drinkRepository.save(drink);
        return drinkMapper.toDto(drink);
    }

    @Override
    public DrinkDTO update(DrinkDTO drinkDTO) {
        log.debug("Request to update Drink : {}", drinkDTO);
        Drink drink = drinkMapper.toEntity(drinkDTO);
        drink = drinkRepository.save(drink);
        return drinkMapper.toDto(drink);
    }

    @Override
    public Optional<DrinkDTO> partialUpdate(DrinkDTO drinkDTO) {
        log.debug("Request to partially update Drink : {}", drinkDTO);

        return drinkRepository
            .findById(drinkDTO.getId())
            .map(existingDrink -> {
                drinkMapper.partialUpdate(existingDrink, drinkDTO);

                return existingDrink;
            })
            .map(drinkRepository::save)
            .map(drinkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DrinkDTO> findOne(Long id) {
        log.debug("Request to get Drink : {}", id);
        return drinkRepository.findById(id).map(drinkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Drink : {}", id);
        drinkRepository.deleteById(id);
    }
}
