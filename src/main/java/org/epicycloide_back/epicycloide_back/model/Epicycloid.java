package org.epicycloide_back.epicycloide_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
public class Epicycloid {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    private float radius;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "rolling_id")
    private Epicycloid rolling;

    @OneToOne(mappedBy = "rolling", cascade = {CascadeType.ALL})
    @JsonIgnore
    private Epicycloid fixed;

    private String name;

    @Column(nullable = true)
    private float frequency;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
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

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public ArrayList<Point> getCoordinates(int pointsNumber) {

        ArrayList<Point> coordinates = new ArrayList<Point>();

        for (double i = 0; i < pointsNumber; i++) {

            double t = (double) 2 * Math.PI * i / pointsNumber;

            Epicycloid rolling = this;
            double baseFrequency = rolling.getFrequency();

            double x = 0;
            double y = 0;
            double frequencySum = 0;

            while (rolling != null) {

                frequencySum += rolling.getFrequency();

                if(rolling.getFixed() == null) {

                    x += rolling.getRadius() * Math.cos(t);
                    y += rolling.getRadius() * Math.sin(t);

                } else {

                    x += rolling.getRadius() * Math.cos(frequencySum / baseFrequency * t);
                    y += rolling.getRadius() * Math.sin(frequencySum / baseFrequency * t);

                }

                rolling = rolling.getRolling();
            }

            Point point = new Point(x, y, t);
            coordinates.add(point);

        }

        return coordinates;

    }

}
