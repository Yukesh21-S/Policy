package com.PolicyManagement.password;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,20}).*$";

    /**
     * Validates a password against defined criteria.
     *
     * @param password The password to validate.
     * @return true if the password meets the criteria; false otherwise.
     */
    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        return password.matches(PASSWORD_PATTERN);
    }
}
