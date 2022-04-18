package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Livre;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Livre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivreRepository extends MongoRepository<Livre, String> {

    List<Livre> findAllByIsBorrowed(Boolean isBorrowed);
}
