package org.epicycloide_back.epicycloide_back.util;

import java.math.BigDecimal;

public class FractionConverter {

    public static Long[] decimalToFraction(double number) {

        double negligibleRatio = 0.01;

        for (int i = 1; ; i++) {
            double tem = number / (1D / i);
            if (Math.abs(tem - Math.round(tem)) < negligibleRatio) {
                long numerator = (long) Math.round(tem);

                return new Long[]{numerator, (long) i};
            }
        }

    }
}