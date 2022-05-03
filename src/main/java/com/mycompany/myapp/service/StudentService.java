package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Loaner;
import com.mycompany.myapp.enums.LoanerType;
import com.mycompany.myapp.repository.LoanerRepository;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Student}.
 */
@Service
public class StudentService {

    private final Logger log = LoggerFactory.getLogger(StudentService.class);


    private final LoanerRepository loanerRepository;

    public StudentService( LoanerRepository loanerRepository) {

        this.loanerRepository = loanerRepository;
    }


    public Loaner save(Loaner loaner) {
        log.debug("Request to save loaner : {}", loaner);
        loaner.setLoanerType(LoanerType.STUDENT);
        return loanerRepository.save(loaner);
    }



    /**
     * Get all the students.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Loaner> findAll(Pageable pageable) {
        log.debug("Request to get all Students");
        return loanerRepository.findAllByLoanerType(pageable, LoanerType.STUDENT);
    }

    /**
     * Get one student by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Loaner> findOne(String id) {
        log.debug("Request to get Student : {}", id);
        return loanerRepository.findById(id);
    }

    /**
     * Delete the student by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Student : {}", id);
        loanerRepository.deleteById(id);
    }
}
