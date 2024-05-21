package org.epicycloide_back.epicycloide_back;

import org.epicycloide_back.epicycloide_back.model.Epicycloid;
import org.epicycloide_back.epicycloide_back.model.Point;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EpicycloidTest {
    @Test
    void testGetCoordinates() {
        Epicycloid epicycloid = new Epicycloid();
        epicycloid.setRadius(1.0);
        epicycloid.setFrequency(1.0);

        ArrayList<Point> coordinates = epicycloid.getCoordinates(1);

        assertEquals(1, coordinates.size());
        assertEquals(1.0, coordinates.get(0).getX());
        assertEquals(0.0, coordinates.get(0).getY());
    }

    @Test
    void testGetCoordinatesWithNegativeFrequency() {
        Epicycloid epicycloid = new Epicycloid();
        epicycloid.setRadius(1.0);
        epicycloid.setFrequency(-1.0);

        ArrayList<Point> coordinates = epicycloid.getCoordinates(1);

        assertEquals(1, coordinates.size());


        assertEquals(1.0, coordinates.get(0).getX());
        assertEquals(0.0, coordinates.get(0).getY());
    }
}
