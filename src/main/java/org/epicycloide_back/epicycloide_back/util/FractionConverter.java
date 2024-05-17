package org.epicycloide_back.epicycloide_back.util;

public class FractionConverter {

    static long gcd(long a, long b) {
        if (a == 0)
            return b;
        else if (b == 0)
            return a;
        if (a < b)
            return gcd(a, b % a);
        else
            return gcd(b, a % b);
    }

    public static Long[] decimalToFraction(double number) {

        // Fetch integral value of the decimal
        double intVal = Math.floor(number);

        // Fetch fractional part of the decimal
        double fVal = number - intVal;

        // Consider precision value to convert fractional part to integral equivalent
        final long pVal = 1000000000;

        // Calculate GCD of integral equivalent of fractional part and precision value
        long gcdVal = gcd(Math.round(
                fVal * pVal), pVal);

        // Calculate num and deno
        long num = Math.round(fVal * pVal) / gcdVal;
        long deno = pVal / gcdVal;

        return new Long[]{num, deno};
    }
}