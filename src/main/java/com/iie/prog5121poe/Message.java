package com.iie.prog5121poe;

// Gson is a library from Google that converts Java objects into JSON text and back
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// This class represents a single message.
// It also keeps track of all messages sent during the session using static ArrayLists.
// Static means the lists belong to the whole class, not to any one object,
// so every message object shares the same lists.
public class Message {

    // ── Static lists that track all messages in the session ───────────────────

    // Only messages the user chose to Send
    private static ArrayList<String> sentMessages        = new ArrayList<String>();

    // Only messages the user chose to Disregard
    private static ArrayList<String> disregardedMessages = new ArrayList<String>();

    // Messages the user chose to Store (also loaded from the JSON file on startup)
    private static ArrayList<String> storedMessages      = new ArrayList<String>();

    // These four lists are "parallel" - index 0 in each belongs to the same message.
    // For example messageHashes.get(2) is the hash for the message in messageTexts.get(2).
    private static ArrayList<String> messageHashes     = new ArrayList<String>();
    private static ArrayList<String> messageIDs        = new ArrayList<String>();
    private static ArrayList<String> messageTexts      = new ArrayList<String>();
    private static ArrayList<String> messageRecipients = new ArrayList<String>();

    // Counts how many messages have been Sent (not stored or disregarded)
    private static int totalMessagesSent = 0;

    // The file path can be changed in tests to avoid touching the real messages.json
    public static String jsonFilePath = "messages.json";

    // ── Fields for one specific message ───────────────────────────────────────
    private String messageID;
    private int    messageNumber;
    private String recipientCell;
    private String messageText;
    private String messageHash;

    // Normal constructor used when the app is running.
    // Generates a random 10-digit ID and builds the hash automatically.
    public Message(int messageNumber, String recipientCell, String messageText) {
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText   = messageText;
        this.messageID     = generateMessageID();
        this.messageHash   = createMessageHash();
    }

    // Second constructor used only in unit tests.
    // Lets us pass in a fixed ID so the hash is predictable in tests.
    public Message(String messageID, int messageNumber, String recipientCell, String messageText) {
        this.messageID     = messageID;
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText   = messageText;
        this.messageHash   = createMessageHash();
    }

    // Builds a random 10-digit number as a string, one digit at a time
    private String generateMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    // Checks that the message ID is not more than 10 characters
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Checks that the recipient's number has an international country code (starts with +)
    public String checkRecipientCell() {
        if (recipientCell.matches("^\\+\\d{1,12}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
             + "Please correct the number and try again.";
    }

    // Builds and returns the message hash.
    // Format: first 2 digits of ID : message number : first word + last word (all uppercase)
    // Example: ID starts with "00", message 0, "Hi Mike... tonight?" gives 00:0:HITONIGHT
    public String createMessageHash() {
        String firstTwo = messageID.substring(0, 2);
        String[] words  = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        messageHash = (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    // Handles what the user wants to do with their message after writing it.
    // 1 = Send it, 2 = Disregard it, 3 = Store it for later
    public String sentMessage(int choice) {

        if (choice == 1) {
            sentMessages.add(messageText);
            messageHashes.add(messageHash);
            messageIDs.add(messageID);
            messageTexts.add(messageText);
            messageRecipients.add(recipientCell);
            totalMessagesSent++;
            return "Message successfully sent.";

        } else if (choice == 2) {
            disregardedMessages.add(messageText);
            return "Press 0 to delete the message.";

        } else if (choice == 3) {
            storedMessages.add(messageText);
            messageHashes.add(messageHash);
            messageIDs.add(messageID);
            messageTexts.add(messageText);
            messageRecipients.add(recipientCell);
            storeMessage();
            return "Message successfully stored.";

        } else {
            return "Invalid option.";
        }
    }

    // Returns the total number of messages sent this session
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Saves the current message to a JSON file so it survives when the app closes.
    // Reads what is already in the file, adds the new message, then writes everything back.
    public void storeMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(jsonFilePath);

        ArrayList<MessageData> messages = new ArrayList<MessageData>();

        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                MessageData[] existing = gson.fromJson(reader, MessageData[].class);
                reader.close();

                if (existing != null) {
                    for (int i = 0; i < existing.length; i++) {
                        messages.add(existing[i]);
                    }
                }
            } catch (IOException e) {
                // File might be empty - just start with an empty list
            }
        }

        MessageData entry   = new MessageData();
        entry.messageID     = this.messageID;
        entry.messageNumber = this.messageNumber;
        entry.recipientCell = this.recipientCell;
        entry.messageText   = this.messageText;
        entry.messageHash   = this.messageHash;
        messages.add(entry);

        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(messages, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    // Reads stored messages from the JSON file when the app starts,
    // so any messages saved in a previous session are available straight away
    public static void loadStoredMessages() {
        Gson gson = new Gson();
        File file = new File(jsonFilePath);

        if (!file.exists()) {
            return;
        }

        try {
            FileReader reader = new FileReader(file);
            MessageData[] data = gson.fromJson(reader, MessageData[].class);
            reader.close();

            if (data == null) {
                return;
            }

            for (int i = 0; i < data.length; i++) {
                if (!storedMessages.contains(data[i].messageText)) {
                    storedMessages.add(data[i].messageText);
                    messageHashes.add(data[i].messageHash);
                    messageIDs.add(data[i].messageID);
                    messageTexts.add(data[i].messageText);
                    messageRecipients.add(data[i].recipientCell);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading stored messages: " + e.getMessage());
        }
    }

    // Finds the longest message in the stored list
    public static String getLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }

        String longest = storedMessages.get(0);
        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).length() > longest.length()) {
                longest = storedMessages.get(i);
            }
        }
        return longest;
    }

    // Searches through the message IDs to find a match and returns the recipient and text
    public static String searchByMessageID(String id) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(id)) {
                return "Recipient: " + messageRecipients.get(i) + "\nMessage: " + messageTexts.get(i);
            }
        }
        return "Message ID not found.";
    }

    // Searches all messages for a specific recipient number and returns everything sent to them
    public static String searchByRecipient(String recipient) {
        String result = "";
        for (int i = 0; i < messageRecipients.size(); i++) {
            if (messageRecipients.get(i).equals(recipient)) {
                result = result + messageTexts.get(i) + "\n";
            }
        }
        if (result.equals("")) {
            return "No messages found for this recipient.";
        }
        return result.trim();
    }

    // Deletes a message by finding its hash and removing it from all lists
    public static String deleteMessageByHash(String hash) {
        int index = messageHashes.indexOf(hash);

        if (index == -1) {
            return "Message hash not found.";
        }

        String deletedText = messageTexts.get(index);

        messageHashes.remove(index);
        messageIDs.remove(index);
        messageTexts.remove(index);
        messageRecipients.remove(index);

        sentMessages.remove(deletedText);
        storedMessages.remove(deletedText);

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // Builds and returns a report of all sent and stored messages
    public static String getMessageReport() {
        if (messageTexts.isEmpty()) {
            return "No messages to report.";
        }

        String report = "--- Message Report ---\n";
        for (int i = 0; i < messageTexts.size(); i++) {
            report = report + "Message Hash : " + messageHashes.get(i) + "\n";
            report = report + "Recipient    : " + messageRecipients.get(i) + "\n";
            report = report + "Message      : " + messageTexts.get(i) + "\n";
            report = report + "----------------------\n";
        }
        return report.trim();
    }

    // Clears all lists and resets the counter - only called in unit tests so each test starts clean
    public static void resetMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        messageTexts.clear();
        messageRecipients.clear();
        totalMessagesSent = 0;
    }

    // Simple inner class used to read and write message data as JSON
    static class MessageData {
        String messageID;
        int    messageNumber;
        String recipientCell;
        String messageText;
        String messageHash;
    }

    // Getter methods so other classes can read these values
    public String getMessageID()    { return messageID; }
    public String getMessageHash()  { return messageHash; }
    public String getRecipientCell(){ return recipientCell; }
    public String getMessageText()  { return messageText; }
    public int    getMessageNumber(){ return messageNumber; }

    public static ArrayList<String> getSentMessages()        { return sentMessages; }
    public static ArrayList<String> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<String> getStoredMessages()      { return storedMessages; }
    public static ArrayList<String> getMessageHashes()       { return messageHashes; }
    public static ArrayList<String> getMessageIDs()          { return messageIDs; }
    public static ArrayList<String> getMessageRecipients()   { return messageRecipients; }
    public static ArrayList<String> getMessageTexts()        { return messageTexts; }
}
