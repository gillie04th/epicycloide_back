package org.epicycloide_back.epicycloide_back.model;

import jakarta.persistence.*;

@Entity
public class Epicycloid {

    private @Id @GeneratedValue int id;

    private float radius;

    @OneToOne(mappedBy = "fixed", cascade = CascadeType.ALL)
    private Epicycloid rolling;

    @OneToOne(cascade = CascadeType.ALL)
    private Epicycloid fixed;

    private String name;

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
}
