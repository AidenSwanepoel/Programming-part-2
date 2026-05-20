package quickchat.main;

// These imports let us use the Login and Message classes from their packages
import quickchat.login.Login;
import quickchat.messaging.Message;

// Scanner is what lets us read what the user types in the console
import java.util.Scanner;

// This is the main class where the program starts when you click Run.
// The app runs in order: register → log in → main menu → quit.
// Everything is console only - no windows or pop-ups.
public class QuickChat {

    public static void main(String[] args) {

        // Create one Scanner for the whole program so all methods share the same input
        Scanner scanner = new Scanner(System.in);

        // ── Registration ──────────────────────────────────────────────────────
        System.out.println("=== QuickChat Registration ===");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (must contain '_' and be 5 characters or fewer): ");
        String username = scanner.nextLine();

        System.out.print("Enter password (8+ chars, uppercase, digit, special character): ");
        String password = scanner.nextLine();

        System.out.print("Enter cell phone number (international format e.g. +27...): ");
        String cellPhone = scanner.nextLine();

        // Create the Login object and run the validation
        Login login = new Login(username, password, cellPhone, firstName, lastName);
        System.out.println(login.registerUser());

        // If any check failed, stop here and ask them to restart
        if (!login.checkUserName() || !login.checkPasswordComplexity() || !login.checkCellPhoneNumber()) {
            System.out.println("Registration failed. Please restart and try again.");
            scanner.close();
            return;
        }

        // ── Login ─────────────────────────────────────────────────────────────
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String enteredUsername = scanner.nextLine();

        System.out.print("Password: ");
        String enteredPassword = scanner.nextLine();

        // Print either the welcome message or the error message
        System.out.println(login.returnLoginStatus(enteredUsername, enteredPassword));

        // If login failed, stop here - only logged in users can send messages
        if (!login.loginUser(enteredUsername, enteredPassword)) {
            System.out.println("Login failed. Please restart and try again.");
            scanner.close();
            return;
        }

        // Login worked - show welcome and load any messages saved from a previous session
        System.out.println("\nWelcome to QuickChat.");
        Message.loadStoredMessages();

        // ── Main menu loop ────────────────────────────────────────────────────
        // The while loop keeps the menu open until the user picks option 4 to quit.
        // I use a boolean called "running" to control when the loop stops.
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
    // This method handles everything for sending messages.
    // The user says how many messages they want, then we loop that many times.
    // If the recipient number or message text fails a check we do i-- to retry that slot.
    private static void sendMessages(Scanner scanner) {

        System.out.print("How many messages do you want to send? ");
        String input = scanner.nextLine();
        int numMessages;

        // Try to convert the input to a number - if it fails, go back to the menu
        try {
            numMessages = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number. Returning to menu.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            // Get the recipient's number
            System.out.print("Recipient cell number (e.g. +27...): ");
            String recipient = scanner.nextLine();

            // Get the message text
            System.out.print("Enter message (max 250 characters): ");
            String text = scanner.nextLine();

            // Check the message is not too long
            if (text.length() > 250) {
                int excess = text.length() - 250;
                System.out.println("Message exceeds 250 characters by " + excess + "; please reduce the size.");
                i--; // go back and retry this message
                continue;
            }

            // Create the message object - this also generates the ID and hash
            // I pass i as the message number so the first is 0, second is 1, etc.
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

            // Number is valid - show the hash
            System.out.println("Message Hash: " + message.getMessageHash());

            // Ask the user what to do with the message
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
                // If they type something that is not a number, default to disregard
                System.out.println("Invalid choice. Message disregarded.");
                action = 2;
            }

            // Pass the choice to sentMessage() and print the result
            System.out.println(message.sentMessage(action));

            // Show the full message details after sending or storing
            if (action == 1 || action == 3) {
                System.out.println("\nMessage ID   : " + message.getMessageID());
                System.out.println("Message Hash : " + message.getMessageHash());
                System.out.println("Recipient    : " + message.getRecipientCell());
                System.out.println("Message      : " + message.getMessageText());
            }
        }

        // Show the running total once all messages in this batch are done
        System.out.println("\nTotal messages sent this session: " + Message.returnTotalMessages());
    }

    // ── Stored Messages sub-menu ──────────────────────────────────────────────
    // This shows the Part 3 options. It works the same as the main menu -
    // a while loop keeps it open until the user presses x to go back.
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

    // Shows the sender (current user) and recipient for each stored message.
    // I check each entry in the parallel lists and only print ones that are in storedMessages.
    private static void displayStoredSenders() {
        if (Message.getStoredMessages().isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }

        System.out.println("Stored messages (Sender: current user):");

        for (int i = 0; i < Message.getMessageRecipients().size(); i++) {
            String text = Message.getMessageTexts().get(i);
            // Only print this entry if the message is in the stored category
            if (Message.getStoredMessages().contains(text)) {
                System.out.println("Recipient : " + Message.getMessageRecipients().get(i));
                System.out.println("Message   : " + text);
                System.out.println("----------");
            }
        }
    }
}
