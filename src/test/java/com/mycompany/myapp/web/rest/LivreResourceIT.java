package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Livre;
import com.mycompany.myapp.repository.LivreRepository;
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
 * Integration tests for the {@link LivreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LivreResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_BORROWED = false;
    private static final Boolean UPDATED_IS_BORROWED = true;

    private static final String ENTITY_API_URL = "/api/livres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private MockMvc restLivreMockMvc;

    private Livre livre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createEntity() {
        Livre livre = new Livre().name(DEFAULT_NAME).author(DEFAULT_AUTHOR).isBorrowed(DEFAULT_IS_BORROWED);
        return livre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createUpdatedEntity() {
        Livre livre = new Livre().name(UPDATED_NAME).author(UPDATED_AUTHOR).isBorrowed(UPDATED_IS_BORROWED);
        return livre;
    }

    @BeforeEach
    public void initTest() {
        livreRepository.deleteAll();
        livre = createEntity();
    }

    @Test
    void createLivre() throws Exception {
        int databaseSizeBeforeCreate = livreRepository.findAll().size();
        // Create the Livre
        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isCreated());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeCreate + 1);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLivre.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testLivre.getIsBorrowed()).isEqualTo(DEFAULT_IS_BORROWED);
    }

    @Test
    void createLivreWithExistingId() throws Exception {
        // Create the Livre with an existing ID
        livre.setId("existing_id");

        int databaseSizeBeforeCreate = livreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLivres() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        // Get all the livreList
        restLivreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livre.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].isBorrowed").value(hasItem(DEFAULT_IS_BORROWED.booleanValue())));
    }

    @Test
    void getLivre() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        // Get the livre
        restLivreMockMvc
            .perform(get(ENTITY_API_URL_ID, livre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livre.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.isBorrowed").value(DEFAULT_IS_BORROWED.booleanValue()));
    }

    @Test
    void getNonExistingLivre() throws Exception {
        // Get the livre
        restLivreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewLivre() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre
        Livre updatedLivre = livreRepository.findById(livre.getId()).get();
        updatedLivre.name(UPDATED_NAME).author(UPDATED_AUTHOR).isBorrowed(UPDATED_IS_BORROWED);

        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLivre.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLivre.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testLivre.getIsBorrowed()).isEqualTo(UPDATED_IS_BORROWED);
    }

    @Test
    void putNonExistingLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livre.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre.author(UPDATED_AUTHOR).isBorrowed(UPDATED_IS_BORROWED);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLivre.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testLivre.getIsBorrowed()).isEqualTo(UPDATED_IS_BORROWED);
    }

    @Test
    void fullUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        int databaseSizeBeforeUpdate = livreRepository.findAll().size();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre.name(UPDATED_NAME).author(UPDATED_AUTHOR).isBorrowed(UPDATED_IS_BORROWED);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
        Livre testLivre = livreList.get(livreList.size() - 1);
        assertThat(testLivre.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLivre.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testLivre.getIsBorrowed()).isEqualTo(UPDATED_IS_BORROWED);
    }

    @Test
    void patchNonExistingLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livre.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLivre() throws Exception {
        int databaseSizeBeforeUpdate = livreRepository.findAll().size();
        livre.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLivre() throws Exception {
        // Initialize the database
        livreRepository.save(livre);

        int databaseSizeBeforeDelete = livreRepository.findAll().size();

        // Delete the livre
        restLivreMockMvc
            .perform(delete(ENTITY_API_URL_ID, livre.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Livre> livreList = livreRepository.findAll();
        assertThat(livreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
