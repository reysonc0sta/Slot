package com.bigtech.slot.service;

import com.bigtech.slot.domain.*; // for static metamodels
import com.bigtech.slot.domain.Drink;
import com.bigtech.slot.repository.DrinkRepository;
import com.bigtech.slot.service.criteria.DrinkCriteria;
import com.bigtech.slot.service.dto.DrinkDTO;
import com.bigtech.slot.service.mapper.DrinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Drink} entities in the database.
 * The main input is a {@link DrinkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DrinkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DrinkQueryService extends QueryService<Drink> {

    private static final Logger log = LoggerFactory.getLogger(DrinkQueryService.class);

    private final DrinkRepository drinkRepository;

    private final DrinkMapper drinkMapper;

    public DrinkQueryService(DrinkRepository drinkRepository, DrinkMapper drinkMapper) {
        this.drinkRepository = drinkRepository;
        this.drinkMapper = drinkMapper;
    }

    /**
     * Return a {@link Page} of {@link DrinkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DrinkDTO> findByCriteria(DrinkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Drink> specification = createSpecification(criteria);
        return drinkRepository.findAll(specification, page).map(drinkMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DrinkCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Drink> specification = createSpecification(criteria);
        return drinkRepository.count(specification);
    }

    /**
     * Function to convert {@link DrinkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Drink> createSpecification(DrinkCriteria criteria) {
        Specification<Drink> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Drink_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Drink_.nome));
            }
            if (criteria.getMarca() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMarca(), Drink_.marca));
            }
        }
        return specification;
    }
}
