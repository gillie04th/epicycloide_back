package org.epicycloide_back.epicycloide_back.service;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;

import java.util.List;

public interface IEpicycloidService {

    Epicycloid getEpicycloidById(int id);
    Epicycloid getEpicycloidByName(String name);
    List<Epicycloid> getAllEpicycloid();
    void saveEpicycloid(Epicycloid epicycloid);
    void deleteEpicycloid(int id);

}
