package quickchat.tests;

// We need to import Login from its package so we can test it here
import quickchat.login.Login;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the Login class - Part 1.
// Each test creates a Login object, calls one method, and checks the result.
// I test both the cases that should pass and the cases that should fail.
// All test data matches the values used in the main application.
public class LoginTest {

    // ── Tests for checkUserName() ─────────────────────────────────────────────

    @Test
    public void testUsernameCorrectlyFormatted() {
        // "kyl_1" has an underscore and is only 5 characters - should return true
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        // "kyle!!!!!!!" is way too long and has no underscore - should return false
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testUsernameNoUnderscore() {
        // Short enough but missing the underscore - should return false
        Login login = new Login("kyle1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testUsernameTooLong() {
        // Has an underscore but is 6 characters which is one too many - should return false
        Login login = new Login("kyl_12", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkUserName());
    }

    // ── Tests for checkPasswordComplexity() ──────────────────────────────────

    @Test
    public void testPasswordMeetsComplexity() {
        // Has uppercase, digits, special characters, and is over 8 characters - should return true
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        // "password" fails on uppercase, digit, and special character - should return false
        Login login = new Login("kyl_1", "password", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordTooShort() {
        // Only 4 characters - fails the length check straight away
        Login login = new Login("kyl_1", "Ab1!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordNoUppercase() {
        // Long enough and has digits and special chars but no uppercase - should return false
        Login login = new Login("kyl_1", "ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.checkPasswordComplexity());
    }

    // ── Tests for checkCellPhoneNumber() ─────────────────────────────────────

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        // Starts with +27 which is a valid international code - should return true
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        // "08966553" has no + sign and no country code - should return false
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553", "Kyle", "Smith");
        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneNoInternationalCode() {
        // Looks like a phone number but is missing the + - should return false
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "0838884567", "Kyle", "Smith");
        assertFalse(login.checkCellPhoneNumber());
    }

    // ── Tests for registerUser() ──────────────────────────────────────────────

    @Test
    public void testRegisterUserUsernameError() {
        // Bad username so it should return the username error message
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals(
            "Username is not correctly formatted; please ensure that your username "
          + "contains an underscore and is no more than five characters in length.",
            login.registerUser()
        );
    }

    @Test
    public void testRegisterUserPasswordError() {
        // Username is fine but password is too weak - should return the password error
        Login login = new Login("kyl_1", "password", "+27838968976", "Kyle", "Smith");
        assertEquals(
            "Password is not correctly formatted; please ensure that the password "
          + "contains at least eight characters, a capital letter, a number, and a special character.",
            login.registerUser()
        );
    }

    @Test
    public void testRegisterUserCellError() {
        // Username and password are valid but the cell number is wrong - should return cell error
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553", "Kyle", "Smith");
        assertEquals(
            "Cell phone number incorrectly formatted or does not contain international code.",
            login.registerUser()
        );
    }

    @Test
    public void testRegisterUserSuccess() {
        // Everything is valid - should return all three success messages
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals(
            "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.",
            login.registerUser()
        );
    }

    // ── Tests for loginUser() ─────────────────────────────────────────────────

    @Test
    public void testLoginSuccessful() {
        // Correct username and password - should return true
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        // Correct username but wrong password - should return false
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.loginUser("kyl_1", "wrongpassword"));
    }

    // ── Tests for returnLoginStatus() ────────────────────────────────────────

    @Test
    public void testReturnLoginStatusSuccess() {
        // Correct details should return the welcome message with the user's name
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals(
            "Welcome Kyle, Smith it is great to see you.",
            login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!")
        );
    }

    @Test
    public void testReturnLoginStatusFailed() {
        // Wrong password should return the generic error message
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals(
            "Username or password incorrect, please try again.",
            login.returnLoginStatus("kyl_1", "wrong")
        );
    }
}
