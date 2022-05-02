package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Loaner;
import com.mycompany.myapp.repository.LoanerRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Loaner}.
 */
@Service
public class LoanerService {

    private final Logger log = LoggerFactory.getLogger(LoanerService.class);

    private final LoanerRepository loanerRepository;

    public LoanerService(LoanerRepository loanerRepository) {
        this.loanerRepository = loanerRepository;
    }

    /**
     * Save a loaner.
     *
     * @param loaner the entity to save.
     * @return the persisted entity.
     */
    public Loaner save(Loaner loaner) {
        log.debug("Request to save Loaner : {}", loaner);
        return loanerRepository.save(loaner);
    }

    /**
     * Partially update a loaner.
     *
     * @param loaner the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Loaner> partialUpdate(Loaner loaner) {
        log.debug("Request to partially update Loaner : {}", loaner);

        return loanerRepository
            .findById(loaner.getId())
            .map(existingLoaner -> {
                if (loaner.getFirstName() != null) {
                    existingLoaner.setFirstName(loaner.getFirstName());
                }
                if (loaner.getLastName() != null) {
                    existingLoaner.setLastName(loaner.getLastName());
                }
                if (loaner.getEmail() != null) {
                    existingLoaner.setEmail(loaner.getEmail());
                }
                if (loaner.getIdNumber() != null) {
                    existingLoaner.setIdNumber(loaner.getIdNumber());
                }

                return existingLoaner;
            })
            .map(loanerRepository::save);
    }

    /**
     * Get all the loaners.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Loaner> findAll(Pageable pageable) {
        log.debug("Request to get all Loaners");
        return loanerRepository.findAll(pageable);
    }

    /**
     * Get one loaner by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Loaner> findOne(String id) {
        log.debug("Request to get Loaner : {}", id);
        return loanerRepository.findById(id);
    }

    /**
     * Delete the loaner by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Loaner : {}", id);
        loanerRepository.deleteById(id);
    }
}
