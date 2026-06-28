package com.iie.prog5121poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    @Test
    void testCheckUserName_Valid() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void testCheckUserName_Invalid() {
        assertFalse(login.checkUserName("kyle!!!!!!"));
    }

    @Test
    void testCheckPasswordComplexity_Valid() {
        assertTrue(login.checkPasswordComplexity("Ch&sec@ke99!"));
    }

    @Test
    void testCheckPasswordComplexity_Invalid() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    void testCheckCellPhoneNumber_Valid() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void testCheckCellPhoneNumber_Invalid() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    void testGetUserNameMessage_Valid() {
        assertEquals(
                "Username successfully captured.",
                login.getUserNameMessage("kyl_1")
        );
    }

    @Test
    void testGetUserNameMessage_Invalid() {
        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.getUserNameMessage("kyle!!!!!!")
        );
    }

    @Test
    void testGetPasswordMessage_Valid() {
        assertEquals(
                "Password successfully captured.",
                login.getPasswordMessage("Ch&sec@ke99!")
        );
    }

    @Test
    void testGetPasswordMessage_Invalid() {
        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.getPasswordMessage("password")
        );
    }

    @Test
    void testGetCellPhoneMessage_Valid() {
        assertEquals(
                "Cell number successfully captured.",
                login.getCellPhoneMessage("+27838968976")
        );
    }

    @Test
    void testGetCellPhoneMessage_Invalid() {
        assertEquals(
                "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                login.getCellPhoneMessage("08966553")
        );
    }

    @Test
    void testRegisterUser_Success() {
        String result = login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec@ke99!",
                "+27838968976"
        );

        assertEquals("User has been registered successfully.", result);
    }

    @Test
    void testRegisterUser_InvalidUsername() {
        String result = login.registerUser(
                "Kyle",
                "Smith",
                "kyle!!!!!!",
                "Ch&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                result
        );
    }

    @Test
    void testRegisterUser_InvalidPassword() {
        String result = login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "password",
                "+27838968976"
        );

        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                result
        );
    }

    @Test
    void testRegisterUser_InvalidCellPhone() {
        String result = login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec@ke99!",
                "08966553"
        );

        assertEquals(
                "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                result
        );
    }

    @Test
    void testLoginUser_Success() {
        login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.loginUser("kyl_1", "Ch&sec@ke99!"));
    }

    @Test
    void testLoginUser_Failure() {
        login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec@ke99!",
                "+27838968976"
        );

        assertFalse(login.loginUser("wrong_1", "wrongPass1!"));
    }

    @Test
    void testReturnLoginStatus_Success() {
        login.registerUser(
                "Kyle",
                "Smith",
                "kyl_1",
                "Ch&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(true)
        );
    }

    @Test
    void testReturnLoginStatus_Failure() {
        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus(false)
        );
    }
}
