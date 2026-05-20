package quickchat.login;

// I need this import to use Pattern.matches() for checking the cell number format
import java.util.regex.Pattern;

// This class handles registering a new user and logging them in.
// I kept both features here because they both work with the same user details.
public class Login {

    // These store the details entered during registration.
    // Private means only this class can access them directly.
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Constructor - runs when we create a new Login object.
    // We pass all the user's details in at once so the object is ready immediately.
    public Login(String username, String password, String cellPhoneNumber,
                 String firstName, String lastName) {
        this.username        = username;
        this.password        = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName       = firstName;
        this.lastName        = lastName;
    }

    // Checks if the username meets the two rules:
    // it must contain an underscore AND be 5 characters or fewer.
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }

    // Checks if the password is strong enough.
    // Rules: at least 8 characters, one uppercase letter, one digit, one special character.
    // I loop through each character and set a flag to true when I find each required type.
    public boolean checkPasswordComplexity() {

        // Fail straight away if the password is too short
        if (password.length() < 8) {
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

        // All three flags must be true for the password to pass
        return hasUppercase && hasDigit && hasSpecialChar;
    }

    // Checks that the cell number has an international country code (starts with +).
    public boolean checkCellPhoneNumber() {
        // The pattern means: must start with + followed by 1 to 12 digits, nothing else
        String regex = "^\\+\\d{1,12}$";
        return Pattern.matches(regex, cellPhoneNumber);
    }

    // Runs all three checks in order and returns a message for what happened.
    // If a check fails I return the error message for that step straight away.
    // If everything passes I return all three success messages.
    public String registerUser() {

        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username "
                 + "contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password "
                 + "contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.";
    }

    // Checks if what the user typed matches what was stored during registration.
    // Returns true if both the username and password match, false otherwise.
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }

    // Returns the message shown to the user after they try to log in.
    // A successful login shows a welcome message with their name.
    // A failed login shows a generic error (we don't say which field was wrong on purpose).
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Getter methods so other classes can read these values without changing them
    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }
}
