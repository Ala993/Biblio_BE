package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Emprunt;
import com.mycompany.myapp.domain.Livre;
import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.repository.EmpruntRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Emprunt}.
 */
@Service
public class EmpruntService {

    private final Logger log = LoggerFactory.getLogger(EmpruntService.class);

    private final EmpruntRepository empruntRepository;

    private final StudentService studentService;

    private final LivreService livreService;

    public EmpruntService(EmpruntRepository empruntRepository, StudentService studentService, LivreService livreService) {
        this.empruntRepository = empruntRepository;
        this.studentService = studentService;
        this.livreService = livreService;
    }

    /**
     * Save a emprunt.
     *
     * @param emprunt the entity to save.
     * @return the persisted entity.
     */
    public Emprunt save(Emprunt emprunt) {
        log.debug("Request to save Emprunt : {}", emprunt);
        Emprunt savedEmprunt = empruntRepository.save(emprunt);
        Student student =  studentService.findOne(emprunt.getStudent().getId()).get();
        List<Emprunt> empruntList = student.getEmprunts();
        if(empruntList == null) empruntList = new ArrayList<>();
        empruntList.add(savedEmprunt);
        student.setEmprunts(empruntList);
        studentService.save(student);
        Livre livre = livreService.findOne(emprunt.getLivre().getId()).get();

        return savedEmprunt;
    }

    /**
     * Partially update a emprunt.
     *
     * @param emprunt the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Emprunt> partialUpdate(Emprunt emprunt) {
        log.debug("Request to partially update Emprunt : {}", emprunt);

        return empruntRepository
            .findById(emprunt.getId())
            .map(existingEmprunt -> {
                if (emprunt.getStart() != null) {
                    existingEmprunt.setStart(emprunt.getStart());
                }
                if (emprunt.getEnd() != null) {
                    existingEmprunt.setEnd(emprunt.getEnd());
                }

                return existingEmprunt;
            })
            .map(empruntRepository::save);
    }

    /**
     * Get all the emprunts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Emprunt> findAll(Pageable pageable) {
        log.debug("Request to get all Emprunts");
        return empruntRepository.findAll(pageable);
    }

    /**
     * Get one emprunt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Emprunt> findOne(String id) {
        log.debug("Request to get Emprunt : {}", id);
        return empruntRepository.findById(id);
    }

    /**
     * Delete the emprunt by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Emprunt : {}", id);
        empruntRepository.deleteById(id);
    }
}