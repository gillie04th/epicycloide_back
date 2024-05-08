package org.epicycloide_back.epicycloide_back.controller;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.service.IEpicycloidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
//        long numericId = Long.parseLong(id);
        return epicycloidService.getEpicycloidById(id);
    }

    @GetMapping("/name/{name}")
    @ResponseBody
    public Epicycloid getEpicycloidByName(@PathVariable String name) {
        return epicycloidService.getEpicycloidByName(name);
    }

    @PostMapping("")
    @ResponseBody
    public void insertEpicycloid(@RequestBody Epicycloid epicycloid) {

        epicycloidService.saveEpicycloid(epicycloid);

    }

    @PutMapping("/{id}")
    @ResponseBody
    public void updateEpicycloid(@PathVariable int id, @RequestBody Epicycloid epicycloid) {

//        long numericId = Long.parseLong(id);
        Epicycloid resource = epicycloidService.getEpicycloidById(id);

        if (resource != null) {
            resource.setFixed(epicycloid.getFixed());
            resource.setRadius(epicycloid.getRadius());
            resource.setRolling(epicycloid.getRolling());
            epicycloidService.saveEpicycloid(resource);
        } else {
            epicycloidService.saveEpicycloid(epicycloid);
        }

    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteEpicycloidBy(@PathVariable int id) {
//        long numericId = Long.parseLong(id);
        epicycloidService.deleteEpicycloid(id);
    }


}
