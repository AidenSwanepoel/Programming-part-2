package com.iie.prog5121poe;

import java.util.regex.Pattern;

// This class handles registering a new user and logging them in.
public class Login {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String cellPhoneNumber;

    // The cell number must start with + followed by exactly 9 digits (South African format)
    private static final Pattern SA_CELL_PATTERN = Pattern.compile("^\\+27\\d{9}$");

    // Checks if the username has an underscore and is 5 characters or fewer
    public boolean checkUserName(String userName) {
        return userName != null
                && userName.contains("_")
                && userName.length() <= 5;
    }

    // Checks that the password is at least 8 characters and has an uppercase letter,
    // a digit, and a special character - I use boolean flags and loop through each character
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase   = false;
        boolean hasDigit       = false;
        boolean hasSpecialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetter(c) && !Character.isDigit(c)) {
                hasSpecialChar = true;
            }
        }

        return hasUppercase && hasDigit && hasSpecialChar;
    }

    // Checks that the cell number is in international format starting with +27
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && SA_CELL_PATTERN.matcher(cellPhoneNumber).matches();
    }

    // Runs all three checks in order and returns the first error it finds.
    // If everything is valid, it saves the details and returns a success message.
    public String registerUser(String firstName,
                               String lastName,
                               String userName,
                               String password,
                               String cellPhoneNumber) {

        if (!checkUserName(userName)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.firstName       = firstName;
        this.lastName        = lastName;
        this.userName        = userName;
        this.password        = password;
        this.cellPhoneNumber = cellPhoneNumber;

        return "User has been registered successfully.";
    }

    // Checks if what the user typed matches what was saved during registration
    public boolean loginUser(String enteredUserName, String enteredPassword) {
        if (this.userName == null || this.password == null) {
            return false;
        }
        return this.userName.equals(enteredUserName)
                && this.password.equals(enteredPassword);
    }

    // Returns a welcome message on success or a generic error on failure
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // These three methods return the inline feedback messages shown as the user registers
    public String getUserNameMessage(String userName) {
        if (checkUserName(userName)) {
            return "Username successfully captured.";
        }
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }

    public String getPasswordMessage(String password) {
        if (checkPasswordComplexity(password)) {
            return "Password successfully captured.";
        }
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    public String getCellPhoneMessage(String cellPhoneNumber) {
        if (checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }

    // Getter methods so other classes can read these values
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public String getUserName()        { return userName; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }
}
