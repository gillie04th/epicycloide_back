package org.epicycloide_back.epicycloide_back.controller;

import jakarta.validation.Valid;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.model.Point;
import org.epicycloide_back.epicycloide_back.service.IEpicycloidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

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
    public void insertEpicycloid(@RequestBody @Valid Epicycloid epicycloid) throws Exception {

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

    @PostMapping("/transform/{precision}")
    @ResponseBody
    public Epicycloid getTransformation(@RequestBody ArrayList<Point> points, @PathVariable int precision) {

//        points.clear();addPoints(points);

        precision = precision % 2 == 1 ? precision -1 : precision;

        Complex[] fourierCoefficients = transform(points, precision);

        Epicycloid epicycloid = null;
        Epicycloid rolling = null;

        for(int i = 0; i < fourierCoefficients.length; i++) {

            System.out.println(fourierCoefficients[i]);

            double frequency = i % 2 == 0 ? i/2 + 1 : -i / 2 - 1;
            double radius = Math.sqrt(Math.pow(fourierCoefficients[i].getReal(), 2) + Math.pow(fourierCoefficients[i].getImaginary(), 2));
            double phase = fourierCoefficients[i].getArgument();

            if(epicycloid == null) {
                epicycloid = new Epicycloid(frequency, radius, phase);
                rolling = epicycloid;
            } else {
                rolling.setRolling(new Epicycloid(frequency, radius, phase));
                rolling = rolling.getRolling();
            }
        }

        return epicycloid;

    }

    public static Complex[] transform(ArrayList<Point> points, int precision) {

        Complex lastPoint = null;

        Complex[] complexPoints = new Complex[points.size()];

//        for (int i = 0; i < points.size(); i++) {
//            lastPoint = new Complex(
//                    lastPoint != null ? lastPoint.getReal() + points.get(i).getX() : points.get(i).getX(),
//                    lastPoint != null ? lastPoint.getImaginary() + points.get(i).getY() : points.get(i).getY()
//            );
//            complexPoints[i] = lastPoint;
//        }

        for (int i = 0; i < points.size(); i++) {
            lastPoint = new Complex(points.get(i).getX(), points.get(i).getY());
            complexPoints[i] = lastPoint;
        }

        int order = precision / 2;
        Complex[] fftResult = new Complex[precision];

        for (int n = -order; n <= order; n++) {

            Complex sum = Complex.ZERO;
            double x = 0, y = 0;

            for (int k = 1; k <= complexPoints.length; k++) {

                Complex exponent = new Complex(-Math.cos(2.0 * n * k * Math.PI / complexPoints.length), -Math.sin(2.0 * n * k * Math.PI / complexPoints.length));
                sum = sum.add(complexPoints[k - 1].multiply(exponent).multiply((double) (1.0 / complexPoints.length)));

            }

            int index = n > 0 ? n * 2 - 2 : (n < 0 ? -n * 2 -1 : 0);

            if(n != 0) {
                System.out.println("index: " + index + ", n: " + n + ", sum: " + sum);
                fftResult[index] = sum;
            }
        }

        return fftResult;
    }

    public static void addPoints(ArrayList<Point> points) {
        points.add(new Point(87.58, 2.92, 0));
        points.add(new Point(-5.37, 1.22, 0));
        points.add(new Point(-4.5, 1.66, 0));
        points.add(new Point(-3.28, 1.93, 0));
        points.add(new Point(-1.69, 2.03, 0));
        points.add(new Point(-0.11, 1.3, 0));
        points.add(new Point(0.22, 2.17, 0));
        points.add(new Point(0.5, 2.73, 0));
        points.add(new Point(0.74, 2.99, 0));
        points.add(new Point(0.94, 3.82, 0));
        points.add(new Point(0.43, 3.15, 0));
        points.add(new Point(-0.06, 2.9, 0));
        points.add(new Point(-0.52, 3.06, 0));
        points.add(new Point(-0.51, 4.07, 0));
        points.add(new Point(-0.47, 6.94, 0));
        points.add(new Point(-0.38, 8.84, 0));
        points.add(new Point(-0.23, 9.77, 0));
        points.add(new Point(-0.08, 6.01, 0));
        points.add(new Point(-0.08, 6.01, 0));
        points.add(new Point(-0.08, 6.01, 0));
        points.add(new Point(-0.08, 6.01, 0));
        points.add(new Point(-1.95, -2.1, 0));
        points.add(new Point(-1.95, -2.1, 0));
        points.add(new Point(-1.95, -2.1, 0));
        points.add(new Point(-1.95, -2.1, 0));
        points.add(new Point(-8.94, -11.49, 0));
        points.add(new Point(-5.47, -11.47, 0));
        points.add(new Point(-1.97, -11.39, 0));
        points.add(new Point(1.54, -11.24, 0));
        points.add(new Point(1.38, -5.79, 0));
        points.add(new Point(0.55, -5.08, 0));
        points.add(new Point(-0.29, -3.97, 0));
        points.add(new Point(-1.13, -2.46, 0));
        points.add(new Point(-2.38, -1.23, 0));
        points.add(new Point(-3.71, -0.05, 0));
        points.add(new Point(-5.34, 1.17, 0));
        points.add(new Point(-7.24, 2.42, 0));
        points.add(new Point(-9.82, 3.96, 0));
        points.add(new Point(-8.14, 4.01, 0));
        points.add(new Point(-6.32, 3.97, 0));
        points.add(new Point(-4.35, 3.86, 0));
        points.add(new Point(-2.65, 3.5, 0));
        points.add(new Point(-0.8, 2.78, 0));
        points.add(new Point(1.08, 2.67, 0));
        points.add(new Point(3, 3.18, 0));
        points.add(new Point(3.42, 3.84, 0));
        points.add(new Point(2.57, 4.38, 0));
        points.add(new Point(1.62, 4.7, 0));
        points.add(new Point(0.56, 4.8, 0));
        points.add(new Point(1.24, 4.51, 0));
        points.add(new Point(4.32, 7.02, 0));
        points.add(new Point(8.33, 10.82, 0));
        points.add(new Point(13.25, 15.92, 0));
        points.add(new Point(4.05, 4.98, 0));
        points.add(new Point(3.44, 4.61, 0));
        points.add(new Point(2.47, 3.72, 0));
        points.add(new Point(1.13, 2.32, 0));
        points.add(new Point(0.52, 1.84, 0));
        points.add(new Point(0.81, 2.41, 0));
        points.add(new Point(0.98, 2.67, 0));
        points.add(new Point(1.05, 2.61, 0));
        points.add(new Point(1.4, 3.7, 0));
        points.add(new Point(0.51, 3.49, 0));
        points.add(new Point(-0.34, 5.12, 0));
        points.add(new Point(-1.15, 8.59, 0));
        points.add(new Point(-1.76, 10.87, 0));
        points.add(new Point(-2.07, 10.23, 0));
        points.add(new Point(-2.22, 8.89, 0));
        points.add(new Point(-2.21, 6.83, 0));
        points.add(new Point(-1.35, 2.38, 0));
        points.add(new Point(-1.16, -0.83, 0));
        points.add(new Point(-1.13, -4.42, 0));
        points.add(new Point(-1.26, -8.38, 0));
        points.add(new Point(-2.18, -14.58, 0));
        points.add(new Point(-2.45, -13.1, 0));
        points.add(new Point(-2.54, -10.72, 0));
        points.add(new Point(-2.44, -7.45, 0));
        points.add(new Point(-1.17, -3.16, 0));
        points.add(new Point(-0.96, -3.28, 0));
        points.add(new Point(-0.65, -3.01, 0));
        points.add(new Point(-0.24, -2.36, 0));
        points.add(new Point(-0.24, -2.31, 0));
        points.add(new Point(-0.69, -2.61, 0));
        points.add(new Point(-1.08, -2.74, 0));
        points.add(new Point(-1.42, -2.7, 0));
        points.add(new Point(-0.86, -1.43, 0));
        points.add(new Point(-0.86, -1.43, 0));
        points.add(new Point(-0.86, -1.43, 0));
        points.add(new Point(-0.86, -1.43, 0));
        points.add(new Point(-0.1, 1.34, 0));
        points.add(new Point(-0.1, 1.34, 0));
        points.add(new Point(-0.1, 1.34, 0));
        points.add(new Point(-0.1, 1.34, 0));
        points.add(new Point(0.03, 2.68, 0));
        points.add(new Point(0.39, 3.19, 0));
        points.add(new Point(0.7, 3.38, 0));
        points.add(new Point(0.95, 3.24, 0));
        points.add(new Point(2.84, 11.67, 0));
        points.add(new Point(3.04, 17.62, 0));
        points.add(new Point(2.64, 19.6, 0));
        points.add(new Point(1.62, 17.6, 0));
        points.add(new Point(1.43, 13.77, 0));
        points.add(new Point(2.73, 12.48, 0));
        points.add(new Point(4.32, 12.24, 0));
        points.add(new Point(6.22, 13.03, 0));
        points.add(new Point(15.45, 22.62, 0));
        points.add(new Point(20.57, 20.73, 0));
        points.add(new Point(23.11, 16.65, 0));
        points.add(new Point(23.08, 10.36, 0));
        points.add(new Point(3.4, 1.04, 0));
        points.add(new Point(3.71, 1.23, 0));
        points.add(new Point(3.57, 1.26, 0));
        points.add(new Point(2.99, 1.13, 0));
        points.add(new Point(7.94, 1.79, 0));
        points.add(new Point(10.07, 0.24, 0));
        points.add(new Point(10.96, -1.26, 0));
        points.add(new Point(10.59, -2.69, 0));
        points.add(new Point(18.85, -5.02, 0));
        points.add(new Point(18.19, -2.04, 0));
        points.add(new Point(17.61, 0.95, 0));
        points.add(new Point(17.12, 3.93, 0));
        points.add(new Point(7.4, 2.25, 0));
        points.add(new Point(5.7, 1.13, 0));
        points.add(new Point(6.49, 0.3, 0));
        points.add(new Point(9.78, -0.25, 0));
        points.add(new Point(11.23, -0.77, 0));
        points.add(new Point(10.11, -1.51, 0));
        points.add(new Point(9.17, -2.28, 0));
        points.add(new Point(8.41, -3.09, 0));
        points.add(new Point(1.03, -0.23, 0));
        points.add(new Point(0.76, 0.43, 0));
        points.add(new Point(0.58, 1.21, 0));
        points.add(new Point(0.5, 2.1, 0));
        points.add(new Point(1.35, 3.52, 0));
        points.add(new Point(2.39, 3.04, 0));
        points.add(new Point(2.99, 2.14, 0));
        points.add(new Point(3.15, 0.81, 0));
        points.add(new Point(4.06, -0.89, 0));
        points.add(new Point(5.17, -2.54, 0));
        points.add(new Point(5.97, -4, 0));
        points.add(new Point(6.46, -5.28, 0));
        points.add(new Point(7.15, -5.64, 0));
        points.add(new Point(7.49, -4.71, 0));
        points.add(new Point(6.84, -3.24, 0));
        points.add(new Point(5.21, -1.23, 0));
        points.add(new Point(4.3, -0.78, 0));
        points.add(new Point(4.76, -2.12, 0));
        points.add(new Point(4.8, -3.22, 0));
        points.add(new Point(4.4, -4.07, 0));
        points.add(new Point(4.14, -5.83, 0));
        points.add(new Point(4.54, -9.23, 0));
        points.add(new Point(5.35, -13.6, 0));
        points.add(new Point(6.57, -18.92, 0));
        points.add(new Point(1.23, -3.67, 0));
        points.add(new Point(1.23, -3.67, 0));
        points.add(new Point(1.23, -3.67, 0));
        points.add(new Point(1.23, -3.67, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(0.1, -7.44, 0));
        points.add(new Point(1.89, -2.82, 0));
        points.add(new Point(1.89, -2.82, 0));
        points.add(new Point(1.89, -2.82, 0));
        points.add(new Point(1.89, -2.82, 0));
        points.add(new Point(3.31, -4.7, 0));
        points.add(new Point(3.42, -4.47, 0));
        points.add(new Point(3.13, -3.74, 0));
        points.add(new Point(2.43, -2.5, 0));
        points.add(new Point(2.79, -2.29, 0));
        points.add(new Point(2.25, -1.14, 0));
        points.add(new Point(2.66, -0.26, 0));
        points.add(new Point(4, 0.36, 0));
        points.add(new Point(3.79, 0.4, 0));
        points.add(new Point(1.83, -0.02, 0));
        points.add(new Point(0.24, -0.53, 0));
        points.add(new Point(-0.98, -1.13, 0));
        points.add(new Point(-1.31, -0.87, 0));
        points.add(new Point(-2, -0.79, 0));
        points.add(new Point(-2.44, -0.64, 0));
        points.add(new Point(-2.61, -0.4, 0));
        points.add(new Point(-5.42, 0.14, 0));
        points.add(new Point(-5.42, 1.52, 0));
        points.add(new Point(-5.59, 2.96, 0));
        points.add(new Point(-5.91, 4.47, 0));
        points.add(new Point(-2.05, 1.7, 0));
        points.add(new Point(-1.82, 1.39, 0));
        points.add(new Point(-1.38, 0.94, 0));
        points.add(new Point(-0.75, 0.34, 0));
        points.add(new Point(-0.28, -1.5, 0));
        points.add(new Point(-0.1, -4.1, 0));
        points.add(new Point(0.08, -6.06, 0));
        points.add(new Point(0.27, -7.4, 0));
        points.add(new Point(-0.06, -19.58, 0));
        points.add(new Point(-2.41, -15.57, 0));
        points.add(new Point(-5.07, -12.71, 0));
        points.add(new Point(-8.02, -10.98, 0));
        points.add(new Point(-11.65, -9.35, 0));
        points.add(new Point(-13.85, -6.17, 0));
        points.add(new Point(-13.75, -2.29, 0));
        points.add(new Point(-11.35, 2.3, 0));
        points.add(new Point(-1.33, 0.82, 0));
        points.add(new Point(-1.77, 1.28, 0));
        points.add(new Point(-1.97, 1.56, 0));
        points.add(new Point(-1.95, 1.67, 0));
        points.add(new Point(-1.93, 1.66, 0));
        points.add(new Point(-1.9, 1.55, 0));
        points.add(new Point(-1.66, 1.25, 0));
        points.add(new Point(-1.2, 0.79, 0));
        points.add(new Point(-1.1, 0.75, 0));
        points.add(new Point(-0.25, 0.78, 0));
        points.add(new Point(0.67, 1.11, 0));
        points.add(new Point(1.67, 1.76, 0));
        points.add(new Point(1.36, 1.57, 0));
        points.add(new Point(0.86, 1.48, 0));
        points.add(new Point(0.37, 1.41, 0));
        points.add(new Point(-0.11, 1.36, 0));
        points.add(new Point(-0.23, 1.92, 0));
        points.add(new Point(0.95, 0.49, 0));
        points.add(new Point(2.52, -0.99, 0));
        points.add(new Point(4.48, -2.53, 0));
        points.add(new Point(11.29, -3.47, 0));
        points.add(new Point(10.97, 2.13, 0));
        points.add(new Point(9.15, 7.05, 0));
        points.add(new Point(5.85, 11.29, 0));
        points.add(new Point(1.53, 7.09, 0));
        points.add(new Point(0.47, 7.14, 0));
        points.add(new Point(-0.6, 8.01, 0));
        points.add(new Point(-1.68, 9.69, 0));
        points.add(new Point(-1, 5.18, 0));
        points.add(new Point(-0.96, 5.47, 0));
        points.add(new Point(-0.8, 5.1, 0));
        points.add(new Point(-0.54, 4.08, 0));
        points.add(new Point(-1.02, 6.69, 0));
        points.add(new Point(-1.51, 5.87, 0));
        points.add(new Point(-2.06, 5.21, 0));
        points.add(new Point(-2.66, 4.73, 0));
        points.add(new Point(-1.5, 2.62, 0));
        points.add(new Point(-1.96, 3.9, 0));
        points.add(new Point(-2.17, 4.65, 0));
        points.add(new Point(-2.13, 4.88, 0));
        points.add(new Point(-2.73, 6.22, 0));
        points.add(new Point(-2.18, 3.94, 0));
        points.add(new Point(-2.2, 2.51, 0));
        points.add(new Point(-2.79, 1.91, 0));
        points.add(new Point(-3.44, 1.82, 0));
        points.add(new Point(-3.99, 1.5, 0));
        points.add(new Point(-6.25, 1.71, 0));
        points.add(new Point(-10.21, 2.45, 0));
        points.add(new Point(-2.22, 0.52, 0));
        points.add(new Point(-2.22, 0.52, 0));
        points.add(new Point(-2.22, 0.52, 0));
        points.add(new Point(-2.22, 0.52, 0));
        points.add(new Point(-3.51, -1.44, 0));
        points.add(new Point(-3.51, -1.44, 0));
        points.add(new Point(-3.51, -1.44, 0));
        points.add(new Point(-3.51, -1.44, 0));
        points.add(new Point(-9.83, -4.21, 0));
        points.add(new Point(-6.14, -3.22, 0));
        points.add(new Point(-3.52, -2.92, 0));
        points.add(new Point(-1.99, -3.31, 0));
        points.add(new Point(-0.82, -1.72, 0));
        points.add(new Point(-1.11, -2.06, 0));
        points.add(new Point(-1.25, -2.13, 0));
        points.add(new Point(-1.25, -1.95, 0));
        points.add(new Point(-0.73, -1.08, 0));
        points.add(new Point(-0.73, -1.08, 0));
        points.add(new Point(-0.73, -1.08, 0));
        points.add(new Point(-0.73, -1.08, 0));
        points.add(new Point(0.63, -1.23, 0));
        points.add(new Point(0.63, -1.23, 0));
        points.add(new Point(0.63, -1.23, 0));
        points.add(new Point(0.63, -1.23, 0));
        points.add(new Point(1.51, -2.73, 0));
        points.add(new Point(2.25, -3.79, 0));
        points.add(new Point(2.68, -4.34, 0));
        points.add(new Point(2.81, -4.4, 0));
        points.add(new Point(5.9, -9.41, 0));
        points.add(new Point(6.24, -10.57, 0));
        points.add(new Point(5.85, -10.44, 0));
        points.add(new Point(4.72, -9.04, 0));
        points.add(new Point(2.56, -6.74, 0));
        points.add(new Point(1.27, -7.47, 0));
        points.add(new Point(0.02, -8.58, 0));
        points.add(new Point(-1.2, -10.07, 0));
        points.add(new Point(-1.93, -12.82, 0));
        points.add(new Point(-0.93, -9.84, 0));
        points.add(new Point(-0.03, -8.95, 0));
        points.add(new Point(0.76, -10.14, 0));
        points.add(new Point(0.7, -7.8, 0));
        points.add(new Point(0.17, -4.73, 0));
        points.add(new Point(-0.38, -2.63, 0));
        points.add(new Point(-0.96, -1.5, 0));
        points.add(new Point(-1.19, -0.94, 0));
        points.add(new Point(-1.35, -0.36, 0));
        points.add(new Point(-1.9, 0.21, 0));
        points.add(new Point(-2.85, 0.75, 0));
        points.add(new Point(-11.31, 4.79, 0));
        points.add(new Point(-12.4, 7.3, 0));
        points.add(new Point(-10.47, 7.81, 0));
        points.add(new Point(-5.52, 6.33, 0));
        points.add(new Point(-0.81, 2.24, 0));
        points.add(new Point(-0.12, 1.59, 0));
        points.add(new Point(0.67, 1.45, 0));
        points.add(new Point(1.54, 1.79, 0));
        points.add(new Point(1.43, 1.28, 0));
        points.add(new Point(1.75, 1.2, 0));
        points.add(new Point(1.84, 0.98, 0));
        points.add(new Point(1.71, 0.63, 0));
        points.add(new Point(2.1, 0.69, 0));
        points.add(new Point(1.12, 0.98, 0));
        points.add(new Point(0.4, 1.78, 0));
        points.add(new Point(-0.04, 3.08, 0));
        points.add(new Point(-0.73, 12.03, 0));
        points.add(new Point(-1.06, 9.24, 0));
        points.add(new Point(-1.4, 6.55, 0));
        points.add(new Point(-1.75, 3.97, 0));
        points.add(new Point(-2.72, 3.44, 0));
        points.add(new Point(-2.61, 2.66, 0));
        points.add(new Point(-2.13, 1.55, 0));
        points.add(new Point(-1.27, 0.11, 0));
        points.add(new Point(-0.72, -2.67, 0));
        points.add(new Point(-0.92, -7.05, 0));
        points.add(new Point(-1.17, -11.87, 0));
        points.add(new Point(-1.48, -17.14, 0));
        points.add(new Point(-0.36, -4.31, 0));
        points.add(new Point(-0.36, -4.31, 0));
        points.add(new Point(-0.36, -4.31, 0));
        points.add(new Point(-0.36, -4.31, 0));
        points.add(new Point(1.08, -1.32, 0));
        points.add(new Point(1.08, -1.32, 0));
        points.add(new Point(1.08, -1.32, 0));
        points.add(new Point(1.08, -1.32, 0));
        points.add(new Point(4.53, -5.95, 0));
        points.add(new Point(3.89, -5.86, 0));
        points.add(new Point(2.72, -4.92, 0));
        points.add(new Point(1.02, -3.11, 0));
        points.add(new Point(-1.29, -1.96, 0));
        points.add(new Point(-4.5, -1.25, 0));
        points.add(new Point(-8.63, -0.67, 0));
        points.add(new Point(-13.69, -0.23, 0));
        points.add(new Point(-16.32, 0.56, 0));
        points.add(new Point(-8.91, 2.33, 0));
        points.add(new Point(-2.75, 4.89, 0));
        points.add(new Point(2.15, 8.23, 0));
        points.add(new Point(1.41, 4.27, 0));
        points.add(new Point(0.91, 5.71, 0));
        points.add(new Point(0.61, 9.04, 0));
        points.add(new Point(0.5, 14.27, 0));
        points.add(new Point(0.26, 12.36, 0));
        points.add(new Point(-0.02, 9.89, 0));
        points.add(new Point(-0.3, 7.52, 0));
        points.add(new Point(-0.59, 5.23, 0));
        points.add(new Point(-0.7, 4.05, 0));
        points.add(new Point(-0.84, 5.02, 0));
        points.add(new Point(-0.88, 5.36, 0));
        points.add(new Point(-0.81, 5.05, 0));
        points.add(new Point(-1.06, 5.74, 0));
        points.add(new Point(-1.24, 4.65, 0));
        points.add(new Point(-1.58, 4.02, 0));
        points.add(new Point(-2.06, 3.85, 0));
        points.add(new Point(-1.04, 1.73, 0));
        points.add(new Point(-1.04, 1.73, 0));
        points.add(new Point(-1.04, 1.73, 0));
        points.add(new Point(-1.04, 1.73, 0));
        points.add(new Point(-2.33, -1.19, 0));
        points.add(new Point(-2.33, -1.19, 0));
        points.add(new Point(-2.33, -1.19, 0));
        points.add(new Point(-2.33, -1.19, 0));
        points.add(new Point(-13.08, -6.22, 0));
        points.add(new Point(-12.17, -4.79, 0));
        points.add(new Point(-11.21, -3.34, 0));
        points.add(new Point(-10.21, -1.88, 0));
        points.add(new Point(-12.27, -0.91, 0));
        points.add(new Point(-11.9, 0.03, 0));
        points.add(new Point(-9.91, 0.9, 0));
        points.add(new Point(-6.29, 1.71, 0));
        points.add(new Point(-2.61, 1.25, 0));
        points.add(new Point(-1.47, -0.29, 0));
        points.add(new Point(-0.93, -2.56, 0));
        points.add(new Point(-1, -5.53, 0));
        points.add(new Point(-0.73, -2.79, 0));
        points.add(new Point(-1.38, -3.99, 0));
        points.add(new Point(-1.84, -4.66, 0));
        points.add(new Point(-2.09, -4.8, 0));
        points.add(new Point(-1.3, -2.84, 0));
        points.add(new Point(-1.3, -2.84, 0));
        points.add(new Point(-1.3, -2.84, 0));
        points.add(new Point(-1.3, -2.84, 0));
        points.add(new Point(0.01, -4.38, 0));
        points.add(new Point(0.01, -4.38, 0));
        points.add(new Point(0.01, -4.38, 0));
        points.add(new Point(0.01, -4.38, 0));
        points.add(new Point(0.81, -16.65, 0));
        points.add(new Point(2.46, -13.36, 0));
        points.add(new Point(4.3, -10.8, 0));
        points.add(new Point(6.36, -8.97, 0));
        points.add(new Point(2.94, -4.15, 0));
        points.add(new Point(1.05, -3.62, 0));
        points.add(new Point(-0.85, -2.79, 0));
        points.add(new Point(-2.76, -1.68, 0));
        points.add(new Point(-6.77, -0.66, 0));
        points.add(new Point(-11.18, -0.15, 0));
        points.add(new Point(-11.82, 0.32, 0));
        points.add(new Point(-8.72, 0.77, 0));
        points.add(new Point(0, 0, 0));

    }
}
