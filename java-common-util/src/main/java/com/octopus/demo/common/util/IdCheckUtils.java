package com.octopus.demo.common.util;

/**
 * Checksum validation utilities.
 */
public final class IdCheckUtils {

    private IdCheckUtils() {}

    /**
     * Standard Luhn (mod 10) check. Starting from the rightmost digit and moving left
     * (index 1 = second-to-last digit), every other digit is doubled; if the doubled
     * value exceeds 9 it is reduced by 9. The sum of all digits is divisible by 10 for
     * a valid number.
     *
     * <p>Contract: returns {@code false} without throwing for {@code null}, empty
     * strings, strings containing non-digit characters, or length &lt; 2. Input is
     * validated strictly; no preprocessing (e.g. stripping spaces) is performed.
     *
     * @param digits the digit string to validate (e.g. a payment card number)
     * @return true if the string is a valid Luhn-checked number
     */
    public static boolean luhn(String digits) {
        if (digits == null || digits.length() < 2) {
            return false;
        }
        int sum = 0;
        boolean doubleIt = false; // rightmost digit is index 0, not doubled
        for (int i = digits.length() - 1; i >= 0; i--) {
            char c = digits.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            int d = c - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }
}
