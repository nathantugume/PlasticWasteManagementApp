package com.ugwebstudio.plasticwastemanagementapp.classes;

public class PhoneFormatter {

    /**
     * Formats a phone number to ensure it starts with '256' instead of '0'.
     * If it already starts with '256', it returns the number unchanged.
     *
     * @param number The phone number to format.
     * @return A formatted phone number with the '256' prefix.
     */
    public static String formatPhoneNumber(String number) {
        if (number.startsWith("0")) {
            return "256" + number.substring(1);
        } else if (number.startsWith("256")) {
            return number;
        }
        return number; // Optionally handle other cases or throw an error if the format is unexpected
    }
}
