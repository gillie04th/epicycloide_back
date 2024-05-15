package org.epicycloide_back.epicycloide_back.controller;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.model.Point;
import org.epicycloide_back.epicycloide_back.service.IEpicycloidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/epicycloid")
public class EpicycloidController {

    @Autowired
    IEpicycloidService epicycloidService;

    @GetMapping("")
    @ResponseBody
    public List<Epicycloid> getAllEpicycloid() {
        return epicycloidService.getAllEpicycloid();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Epicycloid getEpicycloidById(@PathVariable int id) {

        return epicycloidService.getEpicycloidById(id);

    }

    @GetMapping("/name/{name}")
    @ResponseBody
    public Epicycloid getEpicycloidByName(@PathVariable String name) {

        return epicycloidService.getEpicycloidByName(name);

    }

    @GetMapping("/{id}/coordinates/{pointsNumber}")
    @ResponseBody
    public ArrayList<Point> getEpicycloidCoordinatesById(@PathVariable int id, @PathVariable int pointsNumber) {

        return epicycloidService.getEpicycloidById(id).getCoordinates(pointsNumber);

    }

    @PostMapping("/coordinates/{pointsNumber}")
    @ResponseBody
    public ArrayList<Point> getEpicycloidCoordinates(@RequestBody Epicycloid epicycloid, @PathVariable int pointsNumber) {

        return epicycloid.getCoordinates(pointsNumber);

    }

    @PostMapping("")
    @ResponseBody
    public void insertEpicycloid(@RequestBody Epicycloid epicycloid) throws Exception{

        epicycloidService.saveEpicycloid(epicycloid);

    }

    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Epicycloid> updateEpicycloid(@PathVariable int id, @RequestBody Epicycloid updated) {

        Epicycloid existing = epicycloidService.getEpicycloidById(id);

        patchUpdate(existing, updated);

        epicycloidService.saveEpicycloid(existing);

        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteEpicycloidBy(@PathVariable int id) {

        epicycloidService.deleteEpicycloid(id);

    }

    private void patchUpdate(Epicycloid existing, Epicycloid updated) {
        if (existing != null) {
            if (updated.getId() > 0) {
                existing.setId(updated.getId());
            }
            if (updated.getRadius() > 0) {
                existing.setRadius(updated.getRadius());
            }
            if (updated.getName() != null) {
                existing.setName(updated.getName());
            }
            if (updated.getFrequency() > 0) {
                existing.setFrequency(updated.getFrequency());
            }
            if (updated.getRolling() != null) {
                patchUpdate(existing.getRolling(), updated.getRolling());
            }
            if (updated.getFixed() != null) {
                patchUpdate(existing.getFixed(), updated.getFixed());
            }
        }
    }

}
