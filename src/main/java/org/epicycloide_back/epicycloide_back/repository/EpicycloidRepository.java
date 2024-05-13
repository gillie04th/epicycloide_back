package org.epicycloide_back.epicycloide_back.repository;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicycloidRepository extends CrudRepository<Epicycloid, Long> {

    List<Epicycloid> findAll();
    Epicycloid findById(int id);
    Epicycloid findByName(String name);
    void delete(Epicycloid epicycloid);
    Epicycloid save(Epicycloid epicycloid);

}
