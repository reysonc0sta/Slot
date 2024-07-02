package com.bigtech.slot.web.rest;

import static com.bigtech.slot.domain.DrinkAsserts.*;
import static com.bigtech.slot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bigtech.slot.IntegrationTest;
import com.bigtech.slot.domain.Drink;
import com.bigtech.slot.repository.DrinkRepository;
import com.bigtech.slot.service.dto.DrinkDTO;
import com.bigtech.slot.service.mapper.DrinkMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DrinkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DrinkResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_MARCA = "AAAAAAAAAA";
    private static final String UPDATED_MARCA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/drinks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DrinkRepository drinkRepository;

    @Autowired
    private DrinkMapper drinkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDrinkMockMvc;

    private Drink drink;

    private Drink insertedDrink;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drink createEntity(EntityManager em) {
        Drink drink = new Drink().nome(DEFAULT_NOME).marca(DEFAULT_MARCA);
        return drink;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drink createUpdatedEntity(EntityManager em) {
        Drink drink = new Drink().nome(UPDATED_NOME).marca(UPDATED_MARCA);
        return drink;
    }

    @BeforeEach
    public void initTest() {
        drink = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDrink != null) {
            drinkRepository.delete(insertedDrink);
            insertedDrink = null;
        }
    }

    @Test
    @Transactional
    void createDrink() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);
        var returnedDrinkDTO = om.readValue(
            restDrinkMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DrinkDTO.class
        );

        // Validate the Drink in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDrink = drinkMapper.toEntity(returnedDrinkDTO);
        assertDrinkUpdatableFieldsEquals(returnedDrink, getPersistedDrink(returnedDrink));

        insertedDrink = returnedDrink;
    }

    @Test
    @Transactional
    void createDrinkWithExistingId() throws Exception {
        // Create the Drink with an existing ID
        drink.setId(1L);
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        drink.setNome(null);

        // Create the Drink, which fails.
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        restDrinkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDrinks() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(drink.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA)));
    }

    @Test
    @Transactional
    void getDrink() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get the drink
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL_ID, drink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(drink.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.marca").value(DEFAULT_MARCA));
    }

    @Test
    @Transactional
    void getDrinksByIdFiltering() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        Long id = drink.getId();

        defaultDrinkFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDrinkFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDrinkFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDrinksByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where nome equals to
        defaultDrinkFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllDrinksByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where nome in
        defaultDrinkFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllDrinksByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where nome is not null
        defaultDrinkFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllDrinksByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where nome contains
        defaultDrinkFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllDrinksByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where nome does not contain
        defaultDrinkFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllDrinksByMarcaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where marca equals to
        defaultDrinkFiltering("marca.equals=" + DEFAULT_MARCA, "marca.equals=" + UPDATED_MARCA);
    }

    @Test
    @Transactional
    void getAllDrinksByMarcaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where marca in
        defaultDrinkFiltering("marca.in=" + DEFAULT_MARCA + "," + UPDATED_MARCA, "marca.in=" + UPDATED_MARCA);
    }

    @Test
    @Transactional
    void getAllDrinksByMarcaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where marca is not null
        defaultDrinkFiltering("marca.specified=true", "marca.specified=false");
    }

    @Test
    @Transactional
    void getAllDrinksByMarcaContainsSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where marca contains
        defaultDrinkFiltering("marca.contains=" + DEFAULT_MARCA, "marca.contains=" + UPDATED_MARCA);
    }

    @Test
    @Transactional
    void getAllDrinksByMarcaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        // Get all the drinkList where marca does not contain
        defaultDrinkFiltering("marca.doesNotContain=" + UPDATED_MARCA, "marca.doesNotContain=" + DEFAULT_MARCA);
    }

    private void defaultDrinkFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDrinkShouldBeFound(shouldBeFound);
        defaultDrinkShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDrinkShouldBeFound(String filter) throws Exception {
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(drink.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].marca").value(hasItem(DEFAULT_MARCA)));

        // Check, that the count call also returns 1
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDrinkShouldNotBeFound(String filter) throws Exception {
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDrinkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDrink() throws Exception {
        // Get the drink
        restDrinkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDrink() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drink
        Drink updatedDrink = drinkRepository.findById(drink.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDrink are not directly saved in db
        em.detach(updatedDrink);
        updatedDrink.nome(UPDATED_NOME).marca(UPDATED_MARCA);
        DrinkDTO drinkDTO = drinkMapper.toDto(updatedDrink);

        restDrinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, drinkDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO))
            )
            .andExpect(status().isOk());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDrinkToMatchAllProperties(updatedDrink);
    }

    @Test
    @Transactional
    void putNonExistingDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, drinkDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(drinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(drinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDrinkWithPatch() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drink using partial update
        Drink partialUpdatedDrink = new Drink();
        partialUpdatedDrink.setId(drink.getId());

        partialUpdatedDrink.nome(UPDATED_NOME);

        restDrinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDrink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDrink))
            )
            .andExpect(status().isOk());

        // Validate the Drink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrinkUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDrink, drink), getPersistedDrink(drink));
    }

    @Test
    @Transactional
    void fullUpdateDrinkWithPatch() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drink using partial update
        Drink partialUpdatedDrink = new Drink();
        partialUpdatedDrink.setId(drink.getId());

        partialUpdatedDrink.nome(UPDATED_NOME).marca(UPDATED_MARCA);

        restDrinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDrink.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDrink))
            )
            .andExpect(status().isOk());

        // Validate the Drink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrinkUpdatableFieldsEquals(partialUpdatedDrink, getPersistedDrink(partialUpdatedDrink));
    }

    @Test
    @Transactional
    void patchNonExistingDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, drinkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(drinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(drinkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDrink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drink.setId(longCount.incrementAndGet());

        // Create the Drink
        DrinkDTO drinkDTO = drinkMapper.toDto(drink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDrinkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(drinkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Drink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDrink() throws Exception {
        // Initialize the database
        insertedDrink = drinkRepository.saveAndFlush(drink);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the drink
        restDrinkMockMvc
            .perform(delete(ENTITY_API_URL_ID, drink.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return drinkRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Drink getPersistedDrink(Drink drink) {
        return drinkRepository.findById(drink.getId()).orElseThrow();
    }

    protected void assertPersistedDrinkToMatchAllProperties(Drink expectedDrink) {
        assertDrinkAllPropertiesEquals(expectedDrink, getPersistedDrink(expectedDrink));
    }

    protected void assertPersistedDrinkToMatchUpdatableProperties(Drink expectedDrink) {
        assertDrinkAllUpdatablePropertiesEquals(expectedDrink, getPersistedDrink(expectedDrink));
    }
}
