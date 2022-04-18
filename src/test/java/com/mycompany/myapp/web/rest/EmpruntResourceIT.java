package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Emprunt;
import com.mycompany.myapp.repository.EmpruntRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link EmpruntResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EmpruntResourceIT {

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/emprunts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private MockMvc restEmpruntMockMvc;

    private Emprunt emprunt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Emprunt createEntity() {
        Emprunt emprunt = new Emprunt().start(DEFAULT_START).end(DEFAULT_END);
        return emprunt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Emprunt createUpdatedEntity() {
        Emprunt emprunt = new Emprunt().start(UPDATED_START).end(UPDATED_END);
        return emprunt;
    }

    @BeforeEach
    public void initTest() {
        empruntRepository.deleteAll();
        emprunt = createEntity();
    }

    @Test
    void createEmprunt() throws Exception {
        int databaseSizeBeforeCreate = empruntRepository.findAll().size();
        // Create the Emprunt
        restEmpruntMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emprunt)))
            .andExpect(status().isCreated());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeCreate + 1);
        Emprunt testEmprunt = empruntList.get(empruntList.size() - 1);
        assertThat(testEmprunt.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testEmprunt.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    void createEmpruntWithExistingId() throws Exception {
        // Create the Emprunt with an existing ID
        emprunt.setId("existing_id");

        int databaseSizeBeforeCreate = empruntRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmpruntMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emprunt)))
            .andExpect(status().isBadRequest());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEmprunts() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        // Get all the empruntList
        restEmpruntMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emprunt.getId())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    void getEmprunt() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        // Get the emprunt
        restEmpruntMockMvc
            .perform(get(ENTITY_API_URL_ID, emprunt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(emprunt.getId()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    void getNonExistingEmprunt() throws Exception {
        // Get the emprunt
        restEmpruntMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewEmprunt() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();

        // Update the emprunt
        Emprunt updatedEmprunt = empruntRepository.findById(emprunt.getId()).get();
        updatedEmprunt.start(UPDATED_START).end(UPDATED_END);

        restEmpruntMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmprunt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmprunt))
            )
            .andExpect(status().isOk());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
        Emprunt testEmprunt = empruntList.get(empruntList.size() - 1);
        assertThat(testEmprunt.getStart()).isEqualTo(UPDATED_START);
        assertThat(testEmprunt.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    void putNonExistingEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emprunt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emprunt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emprunt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emprunt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEmpruntWithPatch() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();

        // Update the emprunt using partial update
        Emprunt partialUpdatedEmprunt = new Emprunt();
        partialUpdatedEmprunt.setId(emprunt.getId());

        partialUpdatedEmprunt.start(UPDATED_START).end(UPDATED_END);

        restEmpruntMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmprunt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmprunt))
            )
            .andExpect(status().isOk());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
        Emprunt testEmprunt = empruntList.get(empruntList.size() - 1);
        assertThat(testEmprunt.getStart()).isEqualTo(UPDATED_START);
        assertThat(testEmprunt.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    void fullUpdateEmpruntWithPatch() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();

        // Update the emprunt using partial update
        Emprunt partialUpdatedEmprunt = new Emprunt();
        partialUpdatedEmprunt.setId(emprunt.getId());

        partialUpdatedEmprunt.start(UPDATED_START).end(UPDATED_END);

        restEmpruntMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmprunt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmprunt))
            )
            .andExpect(status().isOk());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
        Emprunt testEmprunt = empruntList.get(empruntList.size() - 1);
        assertThat(testEmprunt.getStart()).isEqualTo(UPDATED_START);
        assertThat(testEmprunt.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    void patchNonExistingEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emprunt.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emprunt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emprunt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEmprunt() throws Exception {
        int databaseSizeBeforeUpdate = empruntRepository.findAll().size();
        emprunt.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmpruntMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(emprunt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Emprunt in the database
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEmprunt() throws Exception {
        // Initialize the database
        empruntRepository.save(emprunt);

        int databaseSizeBeforeDelete = empruntRepository.findAll().size();

        // Delete the emprunt
        restEmpruntMockMvc
            .perform(delete(ENTITY_API_URL_ID, emprunt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Emprunt> empruntList = empruntRepository.findAll();
        assertThat(empruntList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
