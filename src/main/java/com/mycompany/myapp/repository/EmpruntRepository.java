package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Emprunt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Emprunt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmpruntRepository extends MongoRepository<Emprunt, String> {
    List<Emprunt> findAllByLivreNameContains(String livre);
}
