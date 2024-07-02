package com.bigtech.slot.web.rest;

import com.bigtech.slot.repository.DrinkRepository;
import com.bigtech.slot.service.DrinkQueryService;
import com.bigtech.slot.service.DrinkService;
import com.bigtech.slot.service.criteria.DrinkCriteria;
import com.bigtech.slot.service.dto.DrinkDTO;
import com.bigtech.slot.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bigtech.slot.domain.Drink}.
 */
@RestController
@RequestMapping("/api/drinks")
public class DrinkResource {

    private static final Logger log = LoggerFactory.getLogger(DrinkResource.class);

    private static final String ENTITY_NAME = "drink";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DrinkService drinkService;

    private final DrinkRepository drinkRepository;

    private final DrinkQueryService drinkQueryService;

    public DrinkResource(DrinkService drinkService, DrinkRepository drinkRepository, DrinkQueryService drinkQueryService) {
        this.drinkService = drinkService;
        this.drinkRepository = drinkRepository;
        this.drinkQueryService = drinkQueryService;
    }

    /**
     * {@code POST  /drinks} : Create a new drink.
     *
     * @param drinkDTO the drinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new drinkDTO, or with status {@code 400 (Bad Request)} if the drink has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DrinkDTO> createDrink(@Valid @RequestBody DrinkDTO drinkDTO) throws URISyntaxException {
        log.debug("REST request to save Drink : {}", drinkDTO);
        if (drinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new drink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        drinkDTO = drinkService.save(drinkDTO);
        return ResponseEntity.created(new URI("/api/drinks/" + drinkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, drinkDTO.getId().toString()))
            .body(drinkDTO);
    }

    /**
     * {@code PUT  /drinks/:id} : Updates an existing drink.
     *
     * @param id the id of the drinkDTO to save.
     * @param drinkDTO the drinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drinkDTO,
     * or with status {@code 400 (Bad Request)} if the drinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the drinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DrinkDTO> updateDrink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DrinkDTO drinkDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Drink : {}, {}", id, drinkDTO);
        if (drinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!drinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        drinkDTO = drinkService.update(drinkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, drinkDTO.getId().toString()))
            .body(drinkDTO);
    }

    /**
     * {@code PATCH  /drinks/:id} : Partial updates given fields of an existing drink, field will ignore if it is null
     *
     * @param id the id of the drinkDTO to save.
     * @param drinkDTO the drinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drinkDTO,
     * or with status {@code 400 (Bad Request)} if the drinkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the drinkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the drinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DrinkDTO> partialUpdateDrink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DrinkDTO drinkDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Drink partially : {}, {}", id, drinkDTO);
        if (drinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!drinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DrinkDTO> result = drinkService.partialUpdate(drinkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, drinkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /drinks} : get all the drinks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of drinks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DrinkDTO>> getAllDrinks(
        DrinkCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Drinks by criteria: {}", criteria);

        Page<DrinkDTO> page = drinkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /drinks/count} : count all the drinks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDrinks(DrinkCriteria criteria) {
        log.debug("REST request to count Drinks by criteria: {}", criteria);
        return ResponseEntity.ok().body(drinkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /drinks/:id} : get the "id" drink.
     *
     * @param id the id of the drinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the drinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DrinkDTO> getDrink(@PathVariable("id") Long id) {
        log.debug("REST request to get Drink : {}", id);
        Optional<DrinkDTO> drinkDTO = drinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(drinkDTO);
    }

    /**
     * {@code DELETE  /drinks/:id} : delete the "id" drink.
     *
     * @param id the id of the drinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrink(@PathVariable("id") Long id) {
        log.debug("REST request to delete Drink : {}", id);
        drinkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
