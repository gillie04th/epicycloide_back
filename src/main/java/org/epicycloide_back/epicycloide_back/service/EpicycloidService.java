package org.epicycloide_back.epicycloide_back.service;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.repository.EpicycloidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EpicycloidService implements IEpicycloidService {

    @Autowired
    private EpicycloidRepository epicycloidRepository;

    @Override
    @Transactional(readOnly = true)
    public Epicycloid getEpicycloidById(int id) {
        return epicycloidRepository.findById(id);
    }

    @Override
    public Epicycloid getEpicycloidByName(String name) {
        return epicycloidRepository.findByName(name);
    }

    @Override
    public List<Epicycloid> getAllEpicycloid() {
        return epicycloidRepository.findAll().stream().filter(e -> e.getRolling() != null && e.getFixed() == null).toList();
    }

    @Override
    public void saveEpicycloid(Epicycloid epicycloid) {
        epicycloidRepository.save(epicycloid);
    }

    @Override
    public void deleteEpicycloid(int id) {
        epicycloidRepository.delete(epicycloidRepository.findById(id));
    }
}
