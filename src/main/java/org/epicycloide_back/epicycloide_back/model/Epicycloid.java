package org.epicycloide_back.epicycloide_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.epicycloide_back.epicycloide_back.validation.GreaterThan;
import org.epicycloide_back.epicycloide_back.util.FractionConverter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Epicycloid {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    private String name;

    @GreaterThan(limit = 0.0)
    private Double radius;

    @Column(nullable = false)
//    @GreaterThan(limit = 0.0)
    private Double frequency;

    @Column(nullable = true)
    private Double phase;


    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "rolling_id")
    @Valid
    private Epicycloid rolling;

    @OneToOne(mappedBy = "rolling", cascade = {CascadeType.ALL})
    @JsonIgnore
    @Valid
    private Epicycloid fixed;

    public Epicycloid() {
    }

    public Epicycloid(double frequency, double radius, double phase) {
        this.frequency = frequency;
        this.radius = radius;
        this.phase = phase;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Double getRadius() {
        return radius != null ? radius : 0;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Epicycloid getRolling() {
        return rolling;
    }

    public void setRolling(Epicycloid rolling) {
        this.rolling = rolling;
    }

    public Epicycloid getFixed() {
        return fixed;
    }

    public void setFixed(Epicycloid fixed) {
        this.fixed = fixed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFrequency() {
        return frequency != null ? frequency : 0;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public Double getPhase() {
        return phase != null ? phase : 0;
    }

    public void setPhase(Double phase) {
        this.phase = phase;
    }

    public ArrayList<Point> getCoordinates(int pointsNumber) {

        ArrayList<Point> coordinates = new ArrayList<Point>();

        for (double i = 0; i < pointsNumber; i++) {

            Epicycloid rolling = this;
            Epicycloid fixed = null;
            double baseFrequency = rolling.getFrequency();

            double frequencyRatioSum = 1;
            long multiplier = 1;

            Epicycloid epi = rolling;
            Epicycloid epiF = null;

            while (epi != null) {
                if (epi != rolling) {
                    frequencyRatioSum *= epi.getFrequency() / epiF.getFrequency();
                }
                if (epi.getRolling() == null) {
                    multiplier = FractionConverter.decimalToFraction(frequencyRatioSum)[1];
                }
                epiF = epi;
                epi = epi.getRolling();
            }

            double t = (double) multiplier * 2 * Math.PI * i / pointsNumber;

            double frequencySum = 0;
            double x = 0;
            double y = 0;

            while (rolling != null) {

                frequencySum += rolling.getFrequency();

                if (fixed == null) {

                    x += rolling.getRadius() * -Math.cos(rolling.getFrequency() * t + rolling.getPhase());
                    y += rolling.getRadius() * Math.sin(rolling.getFrequency() * t + rolling.getPhase());

                } else {

                    x += rolling.getRadius() * -Math.cos((rolling.getFrequency()) * t + rolling.getPhase());
                    y += rolling.getRadius() * Math.sin((rolling.getFrequency()) * t + rolling.getPhase());
//                    x += rolling.getRadius() * Math.cos(frequencySum / baseFrequency * (t + rolling.getPhase()));
//                    y += rolling.getRadius() * Math.sin(frequencySum / baseFrequency * (t + rolling.getPhase()));

                }

                rolling = rolling.getRolling();
                fixed = rolling;
            }

            Point point = new Point(x, y, t);
            coordinates.add(point);

        }

        return coordinates;

    }

    @Override
    public String toString() {
        return "Epicycloid [id=" + id + ", name=" + name + ", radius=" + radius + ", frequency=" + frequency + ", rolling=" + rolling;
    }
}
