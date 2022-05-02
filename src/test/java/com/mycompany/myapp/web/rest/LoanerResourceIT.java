package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Loaner;
import com.mycompany.myapp.repository.LoanerRepository;
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
 * Integration tests for the {@link LoanerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LoanerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ID_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ID_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/loaners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private LoanerRepository loanerRepository;

    @Autowired
    private MockMvc restLoanerMockMvc;

    private Loaner loaner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loaner createEntity() {
        Loaner loaner = new Loaner()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .idNumber(DEFAULT_ID_NUMBER);
        return loaner;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loaner createUpdatedEntity() {
        Loaner loaner = new Loaner()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .idNumber(UPDATED_ID_NUMBER);
        return loaner;
    }

    @BeforeEach
    public void initTest() {
        loanerRepository.deleteAll();
        loaner = createEntity();
    }

    @Test
    void createLoaner() throws Exception {
        int databaseSizeBeforeCreate = loanerRepository.findAll().size();
        // Create the Loaner
        restLoanerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(loaner)))
            .andExpect(status().isCreated());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeCreate + 1);
        Loaner testLoaner = loanerList.get(loanerList.size() - 1);
        assertThat(testLoaner.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testLoaner.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testLoaner.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testLoaner.getIdNumber()).isEqualTo(DEFAULT_ID_NUMBER);
    }

    @Test
    void createLoanerWithExistingId() throws Exception {
        // Create the Loaner with an existing ID
        loaner.setId("existing_id");

        int databaseSizeBeforeCreate = loanerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoanerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(loaner)))
            .andExpect(status().isBadRequest());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLoaners() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        // Get all the loanerList
        restLoanerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loaner.getId())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].idNumber").value(hasItem(DEFAULT_ID_NUMBER)));
    }

    @Test
    void getLoaner() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        // Get the loaner
        restLoanerMockMvc
            .perform(get(ENTITY_API_URL_ID, loaner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(loaner.getId()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.idNumber").value(DEFAULT_ID_NUMBER));
    }

    @Test
    void getNonExistingLoaner() throws Exception {
        // Get the loaner
        restLoanerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewLoaner() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();

        // Update the loaner
        Loaner updatedLoaner = loanerRepository.findById(loaner.getId()).get();
        updatedLoaner.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).idNumber(UPDATED_ID_NUMBER);

        restLoanerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLoaner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLoaner))
            )
            .andExpect(status().isOk());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
        Loaner testLoaner = loanerList.get(loanerList.size() - 1);
        assertThat(testLoaner.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testLoaner.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testLoaner.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testLoaner.getIdNumber()).isEqualTo(UPDATED_ID_NUMBER);
    }

    @Test
    void putNonExistingLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, loaner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(loaner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(loaner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(loaner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLoanerWithPatch() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();

        // Update the loaner using partial update
        Loaner partialUpdatedLoaner = new Loaner();
        partialUpdatedLoaner.setId(loaner.getId());

        restLoanerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoaner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLoaner))
            )
            .andExpect(status().isOk());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
        Loaner testLoaner = loanerList.get(loanerList.size() - 1);
        assertThat(testLoaner.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testLoaner.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testLoaner.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testLoaner.getIdNumber()).isEqualTo(DEFAULT_ID_NUMBER);
    }

    @Test
    void fullUpdateLoanerWithPatch() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();

        // Update the loaner using partial update
        Loaner partialUpdatedLoaner = new Loaner();
        partialUpdatedLoaner.setId(loaner.getId());

        partialUpdatedLoaner.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).idNumber(UPDATED_ID_NUMBER);

        restLoanerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoaner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLoaner))
            )
            .andExpect(status().isOk());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
        Loaner testLoaner = loanerList.get(loanerList.size() - 1);
        assertThat(testLoaner.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testLoaner.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testLoaner.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testLoaner.getIdNumber()).isEqualTo(UPDATED_ID_NUMBER);
    }

    @Test
    void patchNonExistingLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, loaner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(loaner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(loaner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLoaner() throws Exception {
        int databaseSizeBeforeUpdate = loanerRepository.findAll().size();
        loaner.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(loaner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loaner in the database
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLoaner() throws Exception {
        // Initialize the database
        loanerRepository.save(loaner);

        int databaseSizeBeforeDelete = loanerRepository.findAll().size();

        // Delete the loaner
        restLoanerMockMvc
            .perform(delete(ENTITY_API_URL_ID, loaner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Loaner> loanerList = loanerRepository.findAll();
        assertThat(loanerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
