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

import java.util.ArrayList;

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
        return radius;
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
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public Double getPhase() {
        return phase;
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

                    x += rolling.getRadius() * Math.cos(t + rolling.getPhase());
                    y += rolling.getRadius() * Math.sin(t + rolling.getPhase());

                } else {

                    x += rolling.getRadius() * Math.cos(frequencySum / baseFrequency * t + rolling.getPhase());
                    y += rolling.getRadius() * Math.sin(frequencySum / baseFrequency * t + rolling.getPhase());

                }

                rolling = rolling.getRolling();
                fixed = rolling;
            }

            Point point = new Point(x, y, t);
            coordinates.add(point);

        }

        return coordinates;

    }

    public static Epicycloid transform(ArrayList<Point> points, int precision) {

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] pointsArray = points.stream()
                .map(point -> new Complex(point.getX(), point.getY()))
                .toArray(Complex[]::new);

        Complex[] fftResult = new Complex[0];

        try {
            fftResult = fft.transform(pointsArray, TransformType.FORWARD);

//        System.out.println(fftResult.length);

//        for (Complex result : fftResult) {
//            System.out.println("Re: " + result.getReal() + ", Im: " + result.getImaginary());
//        }

            System.out.println("Re: " + fftResult[0].getReal() + ", Im: " + fftResult[0].getImaginary());
            System.out.println("Re: " + fftResult[1].getReal() + ", Im: " + fftResult[1].getImaginary());
            System.out.println("Re: " + fftResult[2].getReal() + ", Im: " + fftResult[2].getImaginary());
            System.out.println("Re: " + fftResult[3].getReal() + ", Im: " + fftResult[3].getImaginary());
            System.out.println("Re: " + fftResult[4].getReal() + ", Im: " + fftResult[4].getImaginary());
            System.out.println("Re: " + fftResult[5].getReal() + ", Im: " + fftResult[5].getImaginary());
            System.out.println("Re: " + fftResult[6].getReal() + ", Im: " + fftResult[6].getImaginary());
            System.out.println("Re: " + fftResult[7].getReal() + ", Im: " + fftResult[7].getImaginary());


            Epicycloid epicycloid = new Epicycloid();
            Epicycloid rolling = null;

            int maxLength = Math.min(precision * 2, fftResult.length);

            // Affichage des résultats
            for (int k = 1; k < maxLength; ++k) {
                double freq = (k + 1) % 2 == 0 ? (k / 2) + 1 : -(k + 1) / 2;
                double radius = Math.sqrt(
                        Math.pow(fftResult[k].getReal(), 2) + Math.pow(fftResult[k].getImaginary(), 2)
                );

                double phase = fftResult[k].getArgument();

//            System.out.printf("Fréquence: %.2f, Amplitude: %.2f, Phase: %.2f°\n", freq, radius, phase);

                Epicycloid newEpicycloid = new Epicycloid(freq, radius, phase);

                if (k == 1) {
                    epicycloid = newEpicycloid;
                    rolling = epicycloid;
                } else {
                    rolling.setRolling(newEpicycloid);
                    rolling = newEpicycloid;
                }
            }

            return epicycloid;

        } catch (MathIllegalArgumentException exception) {
            points.add(new Point(0, 0, 0));
            return transform(points, precision);
        }
    }

    @Override
    public String toString() {
        return "Epicycloid [id=" + id + ", name=" + name + ", radius=" + radius + ", frequency=" + frequency + ", rolling=" + rolling;
    }
}
