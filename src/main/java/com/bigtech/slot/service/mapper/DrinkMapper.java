package com.bigtech.slot.service.mapper;

import com.bigtech.slot.domain.Drink;
import com.bigtech.slot.service.dto.DrinkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Drink} and its DTO {@link DrinkDTO}.
 */
@Mapper(componentModel = "spring")
public interface DrinkMapper extends EntityMapper<DrinkDTO, Drink> {}
