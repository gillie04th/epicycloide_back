package org.epicycloide_back.epicycloide_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.TransformType;
import org.epicycloide_back.epicycloide_back.validation.GreaterThan;
import org.epicycloide_back.epicycloide_back.util.FractionConverter;

import java.util.ArrayList;
import java.util.Arrays;

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

        double[][] data = new double[points.size()][2];
        int index = 0;
        for (Point point : points) {
            data[index++] = new double[]{point.getX(), point.getY()};
        }

        double[] flatPoints = new double[data.length * 2];
        int index2 = 0;
        for (double[] point : data) {
            flatPoints[index2++] = point[0];
            flatPoints[index2++] = point[1];
        }

        int newSize = 1 << (int) (Math.ceil(Math.log10(flatPoints.length) / Math.log10(2)));
        if (newSize > flatPoints.length) {
            flatPoints = Arrays.copyOf(flatPoints, newSize);
        }

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] fftResult = fft.transform(flatPoints, TransformType.FORWARD);

        for (Complex result : fftResult) {
            System.out.println("Re: " + result.getReal() + ", Im: " + result.getImaginary());
        }

        // Calcul de la fréquence d'échantillonnage
        double fs = precision; // Exemple de fréquence d'échantillonnage

        Epicycloid epicycloid = new Epicycloid();
        Epicycloid rolling = null;

        // Affichage des résultats
        for (int k = 0; k < precision; ++k) {
            double freq = k * fs / fftResult.length;
            double mag = fftResult[k].abs(); // Amplitude
            double phase = fftResult[k].getArgument(); // Phase en radians

            System.out.printf("Fréquence: %.2f Hz, Amplitude: %.2f, Phase: %.2f rad\n", freq, mag, phase);

            Epicycloid newEpicycloid = new Epicycloid(freq, mag, phase);

            if (k == 0) {
                epicycloid = newEpicycloid;
                rolling = epicycloid;
            } else {
                rolling.setRolling(newEpicycloid);
                rolling = newEpicycloid;
            }
        }


        return epicycloid;
    }

    @Override
    public String toString() {
        return "Epicycloid [id=" + id + ", name=" + name + ", radius=" + radius + ", frequency=" + frequency + ", rolling=" + rolling;
    }
}
