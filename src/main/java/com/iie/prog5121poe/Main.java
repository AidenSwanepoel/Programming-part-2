package com.iie.prog5121poe;

import java.util.Scanner;

// This is the main class where the program starts when you click Run.
// The app runs in order: register → log in → main menu → quit.
// Everything is console only - no windows or pop-ups.
public class Main {

    public static void main(String[] args) {

        // Create one Scanner for the whole program so all methods share the same input
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // ── Registration ──────────────────────────────────────────────────────
        System.out.println("=== Registration ===");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (must contain '_' and be 5 characters or fewer): ");
        String userName = scanner.nextLine();
        System.out.println(login.getUserNameMessage(userName));

        System.out.print("Enter password (8+ chars, uppercase, digit, special character): ");
        String password = scanner.nextLine();
        System.out.println(login.getPasswordMessage(password));

        System.out.print("Enter South African cell number (example: +27838968976): ");
        String cellPhoneNumber = scanner.nextLine();
        System.out.println(login.getCellPhoneMessage(cellPhoneNumber));

        // Run all three checks together and print the overall result
        String registrationMessage = login.registerUser(
                firstName, lastName, userName, password, cellPhoneNumber
        );
        System.out.println(registrationMessage);

        // If registration failed, stop here and ask them to restart
        if (!"User has been registered successfully.".equals(registrationMessage)) {
            System.out.println("Registration failed. Please restart and try again.");
            scanner.close();
            return;
        }

        // ── Login ─────────────────────────────────────────────────────────────
        System.out.println();
        System.out.println("=== Login ===");

        System.out.print("Enter username: ");
        String enteredUserName = scanner.nextLine();

        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();

        boolean loginSuccessful = login.loginUser(enteredUserName, enteredPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));

        // If login failed, stop here - only logged-in users can send messages
        if (!loginSuccessful) {
            System.out.println("Login failed. Please restart and try again.");
            scanner.close();
            return;
        }

        // Login worked - load any messages saved from a previous session
        System.out.println("\nWelcome to QuickChat.");
        Message.loadStoredMessages();

        // ── Main menu loop ────────────────────────────────────────────────────
        // The while loop keeps the menu open until the user picks option 4 to quit.
        boolean running = true;

        while (running) {
            System.out.println("\n=== Menu ===");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                sendMessages(scanner);
            } else if (choice.equals("2")) {
                // This feature is not implemented yet
                System.out.println("Coming Soon.");
            } else if (choice.equals("3")) {
                storedMessagesMenu(scanner);
            } else if (choice.equals("4")) {
                running = false;
            } else {
                System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }

        // Show the total when the user exits
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
        scanner.close();
    }

    // ── Send Messages ─────────────────────────────────────────────────────────
    // Asks how many messages to send then loops that many times.
    // If a check fails, i-- retries that slot so no message slots are skipped.
    private static void sendMessages(Scanner scanner) {

        System.out.print("How many messages do you want to send? ");
        String input = scanner.nextLine();
        int numMessages;

        try {
            numMessages = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number. Returning to menu.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            System.out.print("Recipient cell number (e.g. +27...): ");
            String recipient = scanner.nextLine();

            System.out.print("Enter message (max 250 characters): ");
            String text = scanner.nextLine();

            // Check the message is not too long
            if (text.length() > 250) {
                int excess = text.length() - 250;
                System.out.println("Message exceeds 250 characters by " + excess + "; please reduce the size.");
                i--;
                continue;
            }

            // Create the message object - this also generates the ID and hash
            Message message = new Message(i, recipient, text);
            System.out.println("Message ID generated: " + message.getMessageID());

            // Check the recipient number and show the result
            String cellCheck = message.checkRecipientCell();
            System.out.println(cellCheck);

            // If the number is invalid, retry this message slot
            if (!cellCheck.equals("Cell phone number successfully captured.")) {
                i--;
                continue;
            }

            System.out.println("Message Hash: " + message.getMessageHash());

            System.out.println("What would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose: ");

            String actionInput = scanner.nextLine();
            int action;

            try {
                action = Integer.parseInt(actionInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Message disregarded.");
                action = 2;
            }

            System.out.println(message.sentMessage(action));

            // Show the full message details after sending or storing
            if (action == 1 || action == 3) {
                System.out.println("\nMessage ID   : " + message.getMessageID());
                System.out.println("Message Hash : " + message.getMessageHash());
                System.out.println("Recipient    : " + message.getRecipientCell());
                System.out.println("Message      : " + message.getMessageText());
            }
        }

        System.out.println("\nTotal messages sent this session: " + Message.returnTotalMessages());
    }

    // ── Stored Messages sub-menu ──────────────────────────────────────────────
    // Shows all the Part 3 options. A while loop keeps it open until the user presses x.
    private static void storedMessagesMenu(Scanner scanner) {

        boolean back = false;

        while (!back) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search by Message ID");
            System.out.println("d) Search by recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display full message report");
            System.out.println("x) Back to main menu");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("a")) {
                displayStoredSenders();
            } else if (choice.equals("b")) {
                System.out.println("Longest stored message:\n" + Message.getLongestStoredMessage());
            } else if (choice.equals("c")) {
                System.out.print("Enter Message ID to search: ");
                String id = scanner.nextLine();
                System.out.println(Message.searchByMessageID(id));
            } else if (choice.equals("d")) {
                System.out.print("Enter recipient cell number: ");
                String recipient = scanner.nextLine();
                System.out.println(Message.searchByRecipient(recipient));
            } else if (choice.equals("e")) {
                System.out.print("Enter message hash to delete: ");
                String hash = scanner.nextLine();
                System.out.println(Message.deleteMessageByHash(hash));
            } else if (choice.equals("f")) {
                System.out.println(Message.getMessageReport());
            } else if (choice.equals("x")) {
                back = true;
            } else {
                System.out.println("Invalid option. Please choose a letter from a to f, or x.");
            }
        }
    }

    // Shows the recipient for each stored message
    private static void displayStoredSenders() {
        if (Message.getStoredMessages().isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }

        System.out.println("Stored messages (Sender: current user):");

        for (int i = 0; i < Message.getMessageRecipients().size(); i++) {
            String text = Message.getMessageTexts().get(i);
            if (Message.getStoredMessages().contains(text)) {
                System.out.println("Recipient : " + Message.getMessageRecipients().get(i));
                System.out.println("Message   : " + text);
                System.out.println("----------");
            }
        }
    }
}
