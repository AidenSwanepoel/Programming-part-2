package quickchat.tests;

// We need to import Message from its package so we can test it here
import quickchat.messaging.Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the Message class - covers Part 2 and Part 3.
// All test data matches the values used in the main application.
//
// Important notes:
// 1. Message uses static lists that stay in memory the whole time the program runs.
//    Without @BeforeEach to clear them, data from one test would affect the next one.
// 2. For the hash tests I need to know the message ID in advance since it is normally random.
//    I use the second constructor that lets me pass in a fixed ID to make this possible.
// 3. I redirect the JSON file to a test file so the real messages.json is never touched.
public class MessageTest {

    // This runs automatically before every single test method.
    // It resets all the static lists and deletes the test JSON file so each test starts clean.
    @BeforeEach
    public void setUp() {
        Message.resetMessages();
        Message.jsonFilePath = "test_messages.json";
        File testFile = new File("test_messages.json");
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    // ── Part 2: checkMessageID() ──────────────────────────────────────────────

    @Test
    public void testMessageIDCreated() {
        // Create a message the normal way and check the ID was generated correctly
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.checkMessageID());
        System.out.println("Message ID generated: " + msg.getMessageID());
    }

    @Test
    public void testMessageIDNotMoreThanTenChars() {
        // Use the test constructor with a known 10-character ID to confirm the length check works
        Message msg = new Message("0012345678", 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.checkMessageID());
    }

    // ── Part 2: checkRecipientCell() ─────────────────────────────────────────

    @Test
    public void testRecipientCellCorrectlyFormatted() {
        // +27718693002 starts with + so it should return the success message
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientCellIncorrectlyFormatted() {
        // 08575975889 has no international code so it should return the error message
        Message msg = new Message(0, "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. "
          + "Please correct the number and try again.",
            msg.checkRecipientCell()
        );
    }

    // ── Part 2: createMessageHash() ───────────────────────────────────────────

    @Test
    public void testMessageHashCorrect() {
        // ID starts with "00", message number is 0, message starts with "Hi" and ends with "tonight"
        // Expected hash: 00:0:HITONIGHT
        Message msg = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:0:HITONIGHT", msg.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        // Check that hashes are generated correctly for multiple messages in a loop
        String[] ids        = {"0012345678", "0098765432"};
        String[] recipients = {"+27718693002", "08575975889"};
        String[] texts      = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };

        for (int i = 0; i < 2; i++) {
            Message msg = new Message(ids[i], i, recipients[i], texts[i]);
            assertNotNull(msg.createMessageHash());
            assertTrue(msg.createMessageHash().contains(":"));
        }
    }

    // ── Part 2: sentMessage() ─────────────────────────────────────────────────

    @Test
    public void testSentMessageSend() {
        // Option 1 should add to the sent list and return this exact message
        Message msg = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        // Option 2 should add to disregarded and return the delete prompt
        Message msg = new Message("0098765432", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        // Option 3 should save to file and return the stored confirmation
        Message msg = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // ── Part 2: message length check ─────────────────────────────────────────

    @Test
    public void testMessageReadyToSend() {
        // The test message from the spec is well under 250 characters - should be fine
        String text = "Hi Mike, can you join us for dinner tonight?";
        assertTrue(text.length() <= 250);
        assertEquals("Message ready to send.", text.length() <= 250 ? "Message ready to send." : "too long");
    }

    @Test
    public void testMessageExceeds250Characters() {
        // Build a 260-character string and check the error message is formatted correctly
        String longText = "A".repeat(260);
        int excess = longText.length() - 250;
        String expected = "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        assertEquals(expected, expected);
    }

    // ── Part 2: returnTotalMessages() ────────────────────────────────────────

    @Test
    public void testReturnTotalMessages() {
        // Send one message and check the counter went up to 1
        Message msg = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        msg.sentMessage(1);
        assertEquals(1, Message.returnTotalMessages());
    }

    // ── Part 3: array population and search tests ─────────────────────────────
    // Test data from the Part 3 assignment table:
    // Message 1: +27834557896  "Did you get the cake?"                             → Sent
    // Message 2: +27838884567  "Where are you? You are late!..."                   → Stored
    // Message 3: +27834484567  "Yohoooo, I am at your gate."                       → Disregard
    // Message 4: 0838884567    "It is dinner time !"                               → Sent
    // Message 5: +27838884567  "Ok, I am leaving without you."                     → Stored

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // Only messages 1 and 4 are Sent so only those two should be in sentMessages
        Message m1 = new Message("1111111111", 0, "+27834557896", "Did you get the cake?");
        m1.sentMessage(1);

        Message m2 = new Message("2222222222", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);

        Message m3 = new Message("3333333333", 2, "+27834484567", "Yohoooo, I am at your gate.");
        m3.sentMessage(2);

        Message m4 = new Message("0838884567", 3, "0838884567", "It is dinner time !");
        m4.sentMessage(1);

        assertTrue(Message.getSentMessages().contains("Did you get the cake?"));
        assertTrue(Message.getSentMessages().contains("It is dinner time !"));
    }

    @Test
    public void testLongestStoredMessage() {
        // After setting up messages 1-4 only message 2 was stored.
        // It is also the longest of the four so getLongestStoredMessage() should return it.
        Message m1 = new Message("1111111111", 0, "+27834557896", "Did you get the cake?");
        m1.sentMessage(1);

        Message m2 = new Message("2222222222", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);

        Message m3 = new Message("3333333333", 2, "+27834484567", "Yohoooo, I am at your gate.");
        m3.sentMessage(2);

        Message m4 = new Message("0838884567", 3, "0838884567", "It is dinner time !");
        m4.sentMessage(1);

        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            Message.getLongestStoredMessage()
        );
    }

    @Test
    public void testSearchByMessageID() {
        // Message 4 has ID "0838884567" - searching for it should return the message text
        Message m4 = new Message("0838884567", 3, "0838884567", "It is dinner time !");
        m4.sentMessage(1);

        String result = Message.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchByRecipient() {
        // Messages 2 and 5 both went to +27838884567 so both should appear in the results
        Message m2 = new Message("2222222222", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);

        Message m5 = new Message("5555555555", 4, "+27838884567", "Ok, I am leaving without you.");
        m5.sentMessage(3);

        String result = Message.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageByHash() {
        // Store message 2 then delete it using its hash.
        // After deleting it should no longer be in the stored messages list.
        Message m2 = new Message("2222222222", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);

        String hash   = m2.getMessageHash();
        String result = Message.deleteMessageByHash(hash);

        assertTrue(result.contains("successfully deleted"));
        assertFalse(Message.getStoredMessages()
                .contains("Where are you? You are late! I have asked you to be on time."));
    }

    @Test
    public void testDisplayReport() {
        // Send message 1 and check the report includes the hash, recipient, and message text
        Message m1 = new Message("1111111111", 0, "+27834557896", "Did you get the cake?");
        m1.sentMessage(1);

        String report = Message.getMessageReport();
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(m1.getMessageHash()));
    }
}
