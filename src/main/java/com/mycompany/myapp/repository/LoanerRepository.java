package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Loaner;
import com.mycompany.myapp.enums.LoanerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Loaner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoanerRepository extends MongoRepository<Loaner, String> {

    Loaner findByLoanerType (LoanerType loanerType);

    Page<Loaner> findAllByLoanerType (Pageable pageable, LoanerType loanerType);
}
