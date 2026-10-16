package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for the methods in Main.java.
 * Place in: src/test/java/com/mycompany/main/MainTest.java
 */
public class MainTest {

    private Scanner originalInput;
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        originalInput = Main.input;
        originalOut = System.out;

        // capture console output so test runs stay clean
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));

        // reset shared state
        Main.messages.clear();
        Main.messageCounter = 0;
        Main.registeredUsername = null;
        Main.registeredPassword = null;
        Main.registeredCellPhone = null;
        Main.username = null;
    }

    @AfterEach
    void tearDown() throws IOException {
        Main.input = originalInput;
        System.setOut(originalOut);
        Main.messages.clear();
        Main.messageCounter = 0;
        Files.deleteIfExists(Path.of("messages.json"));
    }

    // Helper: feed fake keyboard input to the program
    private void provideInput(String text) {
        Main.input = new Scanner(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    // ===================== checkUserName =====================

    @Test
    void checkUserName_validUsername_returnsTrue() {
        assertTrue(Main.checkUserName("kyl_1"));
        assertTrue(Main.checkUserName("_abcd"));
        assertTrue(Main.checkUserName("abcd_"));
    }

    @Test
    void checkUserName_noUnderscore_returnsFalse() {
        assertFalse(Main.checkUserName("kyle1"));
    }

    @Test
    void checkUserName_tooShort_returnsFalse() {
        assertFalse(Main.checkUserName("ky_1"));
    }

    @Test
    void checkUserName_tooLong_returnsFalse() {
        assertFalse(Main.checkUserName("kyle_1"));
    }

    @Test
    void checkUserName_emptyString_returnsFalse() {
        assertFalse(Main.checkUserName(""));
    }

    // ===================== checkPasswordComplexity =====================

    @Test
    void checkPasswordComplexity_validPassword_returnsTrue() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertTrue(Main.checkPasswordComplexity("Passw0rd!"));
    }

    @Test
    void checkPasswordComplexity_tooShort_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("Pa0!aaa")); // 7 chars
    }

    @Test
    void checkPasswordComplexity_noCapital_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("passw0rd!"));
    }

    @Test
    void checkPasswordComplexity_noLowercase_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("PASSW0RD!"));
    }

    @Test
    void checkPasswordComplexity_noNumber_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("Password!"));
    }

    @Test
    void checkPasswordComplexity_noSpecialCharacter_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("Passw0rd1"));
    }

    // ===================== checkCellPhoneNumber =====================

    @Test
    void checkCellPhoneNumber_validNumber_returnsTrue() {
        assertTrue(Main.checkCellPhoneNumber("+27831234567"));
    }

    @Test
    void checkCellPhoneNumber_missingCountryCode_returnsFalse() {
        assertFalse(Main.checkCellPhoneNumber("0831234567"));
    }

    @Test
    void checkCellPhoneNumber_wrongCountryCode_returnsFalse() {
        assertFalse(Main.checkCellPhoneNumber("+44831234567"));
    }

    @Test
    void checkCellPhoneNumber_tooFewDigits_returnsFalse() {
        assertFalse(Main.checkCellPhoneNumber("+2783123456"));
    }

    @Test
    void checkCellPhoneNumber_tooManyDigits_returnsFalse() {
        assertFalse(Main.checkCellPhoneNumber("+278312345678"));
    }

    @Test
    void checkCellPhoneNumber_containsLetters_returnsFalse() {
        assertFalse(Main.checkCellPhoneNumber("+2783123456a"));
    }

    // ===================== registerUser =====================

    @Test
    void registerUser_retriesUntilAllInputsValid_thenStoresDetails() {
        provideInput(
                "abc\n"            // bad username
                + "ab_cd\n"        // good username
                + "weak\n"         // bad password
                + "Passw0rd!\n"    // good password
                + "0831234567\n"   // bad phone
                + "+27831234567\n" // good phone
        );

        Main.registerUser();

        assertEquals("ab_cd", Main.registeredUsername);
        assertEquals("Passw0rd!", Main.registeredPassword);
        assertEquals("+27831234567", Main.registeredCellPhone);
        assertTrue(capturedOut.toString().contains("User registered successfully"));
    }

    // ===================== loginUser =====================

    @Test
    void loginUser_correctDetails_returnsTrue() {
        Main.registeredUsername = "ab_cd";
        Main.registeredPassword = "Passw0rd!";

        assertTrue(Main.loginUser("ab_cd", "Passw0rd!"));
    }

    @Test
    void loginUser_wrongDetailsThenCorrect_reprompsAndReturnsTrue() {
        Main.registeredUsername = "ab_cd";
        Main.registeredPassword = "Passw0rd!";
        provideInput("ab_cd\nPassw0rd!\n"); // retry input

        assertTrue(Main.loginUser("wrong", "wrong"));
        assertTrue(capturedOut.toString().contains("Username or password incorrect"));
    }

    // ===================== returnLoginStatus =====================

    @Test
    void returnLoginStatus_success_returnsWelcomeMessage() {
        Main.username = "ab_cd";

        String result = Main.returnLoginStatus(true);

        assertTrue(result.startsWith("Welcome"));
        assertTrue(result.endsWith("ab_cd"));
    }

    @Test
    void returnLoginStatus_failure_returnsErrorMessage() {
        String result = Main.returnLoginStatus(false);

        assertTrue(result.contains("Username or password incorrect"));
    }

    // ===================== askForNumber =====================

    @Test
    void askForNumber_validNumber_returnsIt() {
        provideInput("5\n");
        assertEquals(5, Main.askForNumber("How many? "));
    }

    @Test
    void askForNumber_trimsWhitespace() {
        provideInput("  7  \n");
        assertEquals(7, Main.askForNumber("How many? "));
    }

    @Test
    void askForNumber_invalidInputsThenValid_keepsAsking() {
        provideInput("abc\n0\n-3\n2.5\n4\n");
        assertEquals(4, Main.askForNumber("How many? "));
        assertTrue(capturedOut.toString()
                .contains("Please enter a whole number greater than 0."));
    }

    // ===================== createMessageId =====================

    @Test
    void createMessageId_isTenDigits() {
        for (int i = 0; i < 50; i++) {
            String id = Main.createMessageId();
            assertEquals(10, id.length());
            assertTrue(id.matches("[0-9]{10}"), "ID should only contain digits: " + id);
        }
    }

    // ===================== createMessageHash =====================

    @Test
    void createMessageHash_multiWordMessage_usesFirstAndLastWord() {
        String hash = Main.createMessageHash("0123456789", 1, "Hi tonight");
        assertEquals("01:1:HITONIGHT", hash);
    }

    @Test
    void createMessageHash_longerMessage_ignoresMiddleWords() {
        String hash = Main.createMessageHash("9876543210", 3, "Hello there my friend");
        assertEquals("98:3:HELLOFRIEND", hash);
    }

    @Test
    void createMessageHash_singleWord_repeatsWord() {
        String hash = Main.createMessageHash("1234567890", 2, "Hello");
        assertEquals("12:2:HELLOHELLO", hash);
    }

    @Test
    void createMessageHash_extraWhitespace_isIgnored() {
        String hash = Main.createMessageHash("1234567890", 5, "   Hello    world   ");
        assertEquals("12:5:HELLOWORLD", hash);
    }

    @Test
    void createMessageHash_resultIsUpperCase() {
        String hash = Main.createMessageHash("1234567890", 1, "abc xyz");
        assertEquals(hash.toUpperCase(), hash);
    }

    // ===================== escapeJson =====================

    @Test
    void escapeJson_escapesQuotes() {
        assertEquals("say \\\"hi\\\"", Main.escapeJson("say \"hi\""));
    }

    @Test
    void escapeJson_escapesBackslashes() {
        assertEquals("C:\\\\temp", Main.escapeJson("C:\\temp"));
    }

    @Test
    void escapeJson_plainText_isUnchanged() {
        assertEquals("hello world", Main.escapeJson("hello world"));
    }

    // ===================== sendMessages =====================

    @Test
    void sendMessages_oneMessage_isStoredWithCorrectDetails() {
        provideInput("1\n+27831234567\nHello world\n");

        Main.sendMessages();

        assertEquals(1, Main.messages.size());
        Main.Message m = Main.messages.get(0);
        assertEquals(1, m.messageNumber);
        assertEquals("+27831234567", m.recipient);
        assertEquals("Hello world", m.messageText);
        assertEquals(10, m.messageId.length());
        assertEquals(m.messageId.substring(0, 2) + ":1:HELLOWORLD", m.messageHash);
    }

    @Test
    void sendMessages_multipleMessages_countersIncrease() {
        provideInput("2\n"
                + "+27831234567\nFirst message\n"
                + "+27821112222\nSecond one here\n");

        Main.sendMessages();

        assertEquals(2, Main.messages.size());
        assertEquals(1, Main.messages.get(0).messageNumber);
        assertEquals(2, Main.messages.get(1).messageNumber);
        assertEquals(2, Main.messageCounter);
    }

    @Test
    void sendMessages_invalidRecipientAndEmptyMessage_reprompts() {
        provideInput("1\n"
                + "0831234567\n"        // invalid recipient
                + "+27831234567\n"       // valid recipient
                + "   \n"                // blank message
                + "Valid message\n");    // valid message

        Main.sendMessages();

        assertEquals(1, Main.messages.size());
        assertEquals("+27831234567", Main.messages.get(0).recipient);
        assertEquals("Valid message", Main.messages.get(0).messageText);
    }

    @Test
    void sendMessages_messageOver250Characters_isRejected() {
        String tooLong = "a".repeat(251);
        String maxLength = "b".repeat(250);
        provideInput("1\n+27831234567\n" + tooLong + "\n" + maxLength + "\n");

        Main.sendMessages();

        assertEquals(1, Main.messages.size());
        assertEquals(maxLength, Main.messages.get(0).messageText);
    }

    // ===================== saveMessagesToJson =====================

    @Test
    void saveMessagesToJson_writesMessagesToFile() throws IOException {
        Main.messages.add(new Main.Message(
                "0123456789", 1, "+27831234567", "Hello world", "01:1:HELLOWORLD"));

        Main.saveMessagesToJson();

        Path file = Path.of("messages.json");
        assertTrue(Files.exists(file));
        String json = Files.readString(file);

        assertTrue(json.startsWith("["));
        assertTrue(json.contains("\"messageId\": \"0123456789\""));
        assertTrue(json.contains("\"messageNumber\": 1"));
        assertTrue(json.contains("\"messageHash\": \"01:1:HELLOWORLD\""));
        assertTrue(json.contains("\"recipient\": \"+27831234567\""));
        assertTrue(json.contains("\"message\": \"Hello world\""));
        assertTrue(capturedOut.toString().contains("Messages saved to messages.json"));
    }

    @Test
    void saveMessagesToJson_multipleMessages_separatedByComma() throws IOException {
        Main.messages.add(new Main.Message("1111111111", 1, "+27831234567", "One", "11:1:ONEONE"));
        Main.messages.add(new Main.Message("2222222222", 2, "+27821112222", "Two", "22:2:TWOTWO"));

        Main.saveMessagesToJson();

        String json = Files.readString(Path.of("messages.json"));
        assertTrue(json.contains("},\n  {"), "Objects should be separated by a comma");
        assertTrue(json.contains("\"messageId\": \"1111111111\""));
        assertTrue(json.contains("\"messageId\": \"2222222222\""));
    }

    @Test
    void saveMessagesToJson_escapesSpecialCharacters() throws IOException {
        Main.messages.add(new Main.Message(
                "1234567890", 1, "+27831234567", "He said \"hi\"", "12:1:HEHI"));

        Main.saveMessagesToJson();

        String json = Files.readString(Path.of("messages.json"));
        assertTrue(json.contains("He said \\\"hi\\\""));
    }

    @Test
    void saveMessagesToJson_noMessages_writesEmptyArray() throws IOException {
        Main.saveMessagesToJson();

        String json = Files.readString(Path.of("messages.json"));
        assertEquals("[\n\n]\n", json);
    }
}
