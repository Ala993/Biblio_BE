package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Loaner;
import com.mycompany.myapp.repository.LoanerRepository;
import com.mycompany.myapp.service.LoanerService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Loaner}.
 */
@RestController
@RequestMapping("/api")
public class LoanerResource {

    private final Logger log = LoggerFactory.getLogger(LoanerResource.class);

    private static final String ENTITY_NAME = "loaner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LoanerService loanerService;

    private final LoanerRepository loanerRepository;

    public LoanerResource(LoanerService loanerService, LoanerRepository loanerRepository) {
        this.loanerService = loanerService;
        this.loanerRepository = loanerRepository;
    }

    /**
     * {@code POST  /loaners} : Create a new loaner.
     *
     * @param loaner the loaner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new loaner, or with status {@code 400 (Bad Request)} if the loaner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/loaners")
    public ResponseEntity<Loaner> createLoaner(@RequestBody Loaner loaner) throws URISyntaxException {
        log.debug("REST request to save Loaner : {}", loaner);
        if (loaner.getId() != null) {
            throw new BadRequestAlertException("A new loaner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Loaner result = loanerService.save(loaner);
        return ResponseEntity
            .created(new URI("/api/loaners/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /loaners/:id} : Updates an existing loaner.
     *
     * @param id the id of the loaner to save.
     * @param loaner the loaner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated loaner,
     * or with status {@code 400 (Bad Request)} if the loaner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the loaner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/loaners/{id}")
    public ResponseEntity<Loaner> updateLoaner(@PathVariable(value = "id", required = false) final String id, @RequestBody Loaner loaner)
        throws URISyntaxException {
        log.debug("REST request to update Loaner : {}, {}", id, loaner);
        if (loaner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, loaner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loanerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Loaner result = loanerService.save(loaner);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, loaner.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /loaners/:id} : Partial updates given fields of an existing loaner, field will ignore if it is null
     *
     * @param id the id of the loaner to save.
     * @param loaner the loaner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated loaner,
     * or with status {@code 400 (Bad Request)} if the loaner is not valid,
     * or with status {@code 404 (Not Found)} if the loaner is not found,
     * or with status {@code 500 (Internal Server Error)} if the loaner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/loaners/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Loaner> partialUpdateLoaner(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Loaner loaner
    ) throws URISyntaxException {
        log.debug("REST request to partial update Loaner partially : {}, {}", id, loaner);
        if (loaner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, loaner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loanerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Loaner> result = loanerService.partialUpdate(loaner);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, loaner.getId()));
    }

    /**
     * {@code GET  /loaners} : get all the loaners.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of loaners in body.
     */
    @GetMapping("/loaners")
    public ResponseEntity<List<Loaner>> getAllLoaners(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Loaners");
        Page<Loaner> page = loanerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /loaners/:id} : get the "id" loaner.
     *
     * @param id the id of the loaner to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the loaner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/loaners/{id}")
    public ResponseEntity<Loaner> getLoaner(@PathVariable String id) {
        log.debug("REST request to get Loaner : {}", id);
        Optional<Loaner> loaner = loanerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(loaner);
    }

    /**
     * {@code DELETE  /loaners/:id} : delete the "id" loaner.
     *
     * @param id the id of the loaner to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/loaners/{id}")
    public ResponseEntity<Void> deleteLoaner(@PathVariable String id) {
        log.debug("REST request to delete Loaner : {}", id);
        loanerService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
