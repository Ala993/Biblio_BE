package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Livre;
import com.mycompany.myapp.repository.LivreRepository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Livre}.
 */
@Service
public class LivreService {

    private final Logger log = LoggerFactory.getLogger(LivreService.class);

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    /**
     * Save a livre.
     *
     * @param livre the entity to save.
     * @return the persisted entity.
     */
    public Livre save(Livre livre) {
        log.debug("Request to save Livre : {}", livre);
        return livreRepository.save(livre);
    }

    /**
     * Partially update a livre.
     *
     * @param livre the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Livre> partialUpdate(Livre livre) {
        log.debug("Request to partially update Livre : {}", livre);

        return livreRepository
            .findById(livre.getId())
            .map(existingLivre -> {
                if (livre.getName() != null) {
                    existingLivre.setName(livre.getName());
                }
                if (livre.getAuthor() != null) {
                    existingLivre.setAuthor(livre.getAuthor());
                }
                if (livre.getIsBorrowed() != null) {
                    existingLivre.setIsBorrowed(livre.getIsBorrowed());
                }

                return existingLivre;
            })
            .map(livreRepository::save);
    }

    /**
     * Get all the livres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Livre> findAll(Pageable pageable) {
        log.debug("Request to get all Livres");
        return livreRepository.findAll(pageable);
    }

    /**
     * Get one livre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Livre> findOne(String id) {
        log.debug("Request to get Livre : {}", id);
        return livreRepository.findById(id);
    }

    /**
     * Delete the livre by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Livre : {}", id);
        livreRepository.deleteById(id);
    }


    public List<Livre> findAllNotBorrowed() {
        return livreRepository.findAllByIsBorrowed(false);
    }
}
