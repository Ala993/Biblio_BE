package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Emprunt;
import com.mycompany.myapp.repository.EmpruntRepository;
import com.mycompany.myapp.service.EmpruntService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Emprunt}.
 */
@RestController
@RequestMapping("/api")
public class EmpruntResource {

    private final Logger log = LoggerFactory.getLogger(EmpruntResource.class);

    private static final String ENTITY_NAME = "emprunt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmpruntService empruntService;

    private final EmpruntRepository empruntRepository;

    public EmpruntResource(EmpruntService empruntService, EmpruntRepository empruntRepository) {
        this.empruntService = empruntService;
        this.empruntRepository = empruntRepository;
    }

    /**
     * {@code POST  /emprunts} : Create a new emprunt.
     *
     * @param emprunt the emprunt to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new emprunt, or with status {@code 400 (Bad Request)} if the emprunt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/emprunts")
    public ResponseEntity<Emprunt> createEmprunt(@RequestBody Emprunt emprunt) throws URISyntaxException {
        log.debug("REST request to save Emprunt : {}", emprunt);
        if (emprunt.getId() != null) {
            throw new BadRequestAlertException("A new emprunt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Emprunt result = empruntService.save(emprunt);
        return ResponseEntity
            .created(new URI("/api/emprunts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /emprunts/:id} : Updates an existing emprunt.
     *
     * @param id the id of the emprunt to save.
     * @param emprunt the emprunt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated emprunt,
     * or with status {@code 400 (Bad Request)} if the emprunt is not valid,
     * or with status {@code 500 (Internal Server Error)} if the emprunt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/emprunts/{id}")
    public ResponseEntity<Emprunt> updateEmprunt(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Emprunt emprunt
    ) throws URISyntaxException {
        log.debug("REST request to update Emprunt : {}, {}", id, emprunt);
        if (emprunt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, emprunt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!empruntRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Emprunt result = empruntService.save(emprunt);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, emprunt.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /emprunts/:id} : Partial updates given fields of an existing emprunt, field will ignore if it is null
     *
     * @param id the id of the emprunt to save.
     * @param emprunt the emprunt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated emprunt,
     * or with status {@code 400 (Bad Request)} if the emprunt is not valid,
     * or with status {@code 404 (Not Found)} if the emprunt is not found,
     * or with status {@code 500 (Internal Server Error)} if the emprunt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/emprunts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Emprunt> partialUpdateEmprunt(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Emprunt emprunt
    ) throws URISyntaxException {
        log.debug("REST request to partial update Emprunt partially : {}, {}", id, emprunt);
        if (emprunt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, emprunt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!empruntRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Emprunt> result = empruntService.partialUpdate(emprunt);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, emprunt.getId())
        );
    }

    /**
     * {@code GET  /emprunts} : get all the emprunts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of emprunts in body.
     */
    @GetMapping("/emprunts")
    public ResponseEntity<List<Emprunt>> getAllEmprunts(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Emprunts");
        Page<Emprunt> page = empruntService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /emprunts/:id} : get the "id" emprunt.
     *
     * @param id the id of the emprunt to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the emprunt, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/emprunts/{id}")
    public ResponseEntity<Emprunt> getEmprunt(@PathVariable String id) {
        log.debug("REST request to get Emprunt : {}", id);
        Optional<Emprunt> emprunt = empruntService.findOne(id);
        return ResponseUtil.wrapOrNotFound(emprunt);
    }

    /**
     * {@code DELETE  /emprunts/:id} : delete the "id" emprunt.
     *
     * @param id the id of the emprunt to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/emprunts/{id}")
    public ResponseEntity<Void> deleteEmprunt(@PathVariable String id) {
        log.debug("REST request to delete Emprunt : {}", id);
        empruntService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
    @GetMapping("/emprunts-by-criteria/{livre}")
    public ResponseEntity<List<Emprunt>> getAllEmprunts(@PathVariable String livre) {
        log.debug("REST request to get a list of Emprunts by criteria");
        List<Emprunt> page = empruntService.findAllByCriteria(livre);
        return ResponseEntity.ok().body(page);
    }
}
