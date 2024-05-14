package org.epicycloide_back.epicycloide_back.model;

public class Point {

    private double x;

    private double y;

    private double t;

    public Point() {}

    public Point(double x, double y, double t) {
        this.x = x;
        this.y = y;
        this.t = t;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setX(double x) { this.x = x; }

    public void setY(double y) { this.y = y; }

    public double getT() { return t; }

    public void setT(double t) { this.t = t; }

}
