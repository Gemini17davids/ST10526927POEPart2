package com.mycompany.main;

// import Scanner so we can get a user input
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

// Create the main class
public class Main {
    
    // Create Scanner for user input
    static Scanner input = new Scanner(System.in);
    
    static String username;
    static String password;
    static String cellphone;
    
    // Stores the registered useername
    static String registeredUsername;
    
    // Stores the registered Password
    static String registeredPassword;
    
    // Stores the registered Cellphone number
    static String registeredCellPhone;
    
    public static boolean checkUserName(String username){
        
        // usernbame must have 5 Characters
       // and must contain an underscore
       if(username.length()== 5 && username.contains("_")){
         return true;
        } else {
        return false;
       }
    }
    
    // Create the password checking method
    public static boolean checkPasswordComplexity(String password){
        
        // Password must have 8 characters long, contain a capital letter
        // Contain a number and a spacial character
        if(password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*")) {
            return true;
        }else{
        
        return false;
    }
    }
    
    // Create the cellphone checking method
    public static boolean checkCellPhoneNumber(String cellphone){
        
        // Phone Number must have a country code (+27)
        if(cellphone.matches("\\+27[0-9]{9}$")){
            return true;
        } else{
        return false;
    }
    }
    
    // Create the registration method
    public static void registerUser(){
        
        // registration code will be added here
        while (true){
        System.out.print("Enter your Username: ");
        username = input.nextLine();
        // check username
        boolean usernameCorrect = checkUserName(username);
        if(usernameCorrect){
            break;
        } else{
            System.out.print("Username is incorerect formatted. Please try again");
        }
        }
        
        // infite loop for password
        while (true){
        System.out.print("Enter your Password: ");
        password = input.nextLine();
        // check username
       boolean passwordCorrect = checkPasswordComplexity(password);
        if(passwordCorrect){
            break;
        } else{
            System.out.print("Password is incorerect formatted. Please try again");
        }
        }
        
        // infite loop for cell phone number
        while (true){
        System.out.print("Enter your Cell Phone Number (+27): ");
        cellphone = input.nextLine();
        // check username
        boolean cellPhoneCorrect = checkCellPhoneNumber(cellphone);
        if(cellPhoneCorrect){
            break;
        } else{
            System.out.print("Cell Phone number is incorerect formatted. Please try again");
        }
      }
        
        // save the valid username
        registeredUsername = username;
        // save the valid password
        registeredPassword = password;
        // save the valid cellphone number
        registeredCellPhone = cellphone;
        // display success messege
        System.out.println("User registered successfully");
    }
    // Creates the log in method
    public static boolean loginUser(String username, String password){
        
        
        while (true){
        // Compare entered details with registered details
        if(username.equals(registeredUsername)
                && password.equals(registeredPassword)) {
            return true;
        } else {
            System.out.println("Username or password incorrect, please, please try again");
            // Re-promt the user for username
            System.out.print("Enter your username: ");
            username = input.nextLine();
            // Re-promt the user for password
            System.out.print("Enter your password: ");
            password = input.nextLine();
        }
        }
    }
    // Creates the login status method
    public static String returnLoginStatus(boolean loginSuccessful){
        
        // Check if login was successful
        if(loginSuccessful){
        
        return "Welcome, it is greate to see you again" + username;
        } else {
            return "Username or password incorrect, pleasew try again";
        }
    
    }
    
     // Main method where the program starts
    public static void main(String[] args) {
        
        // call registration method
        registerUser();
        
        System.out.println("");
        
        // aske user to login
        System.out.print("Enter your username to login: ");
            String loginUsername = input.nextLine();
            
        System.out.print("Enter your password to login: ");
            String loginPassword = input.nextLine();
            
            System.out.println("");
            
        // call login method
        boolean loginSuccessful = loginUser(loginUsername, loginPassword);
        // call login status method
        System.out.println(returnLoginStatus(loginSuccessful));
    
        
        //======================================
        //Part 2
        //======================================
       
        // Show the welcome message
        System.out.println("Welcome to QuickChat.");

        // Application loop: keeps running until the user chooses 3 (Quit)
        boolean running = true;
        while (running) {

            // Display the numeric menu
            System.out.println("");
            System.out.println("Please choose an option:");
            System.out.println("1) Send Message");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter option (1-3): ");
            String option = input.nextLine();

            if (option.equals("1")) {
                // Add messages
                sendMessages();
            } else if (option.equals("2")) {
                System.out.println("Coming Soon.");
            } else if (option.equals("3")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        }
    }

    //======================================
    // Part 2 : methods and variables
    //======================================

    // Simple class that holds the details of one message
    static class Message {
        String messageId;
        int messageNumber;
        String recipient;
        String messageText;
        String messageHash;

        Message(String messageId, int messageNumber, String recipient,
                String messageText, String messageHash) {
            this.messageId = messageId;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = messageHash;
        }
    }

    // ArrayList that stores all the messages
    static ArrayList<Message> messages = new ArrayList<>();

    // Counts the total number of messages entered (message number)
    static int messageCounter = 0;

    // Used to create random digits for the message ID
    static Random random = new Random();

    // Asks the user for a whole number greater than 0
    public static int askForNumber(String question) {
        while (true) {
            System.out.print(question);
            String text = input.nextLine();
            try {
                int number = Integer.parseInt(text.trim());
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // not a number, so ask again below
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    // Message ID: 10 random digits built one digit at a time with a loop counter
    public static String createMessageId() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    // Message Hash: first two digits of the ID + ":" + message number + ":"
    // + first word and last word of the message, all in capital letters
    public static String createMessageHash(String id, int number, String text) {
        // substring takes the first two digits of the message ID
        String firstTwo = id.substring(0, 2);

        // split the message into words
        String[] words = text.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (firstTwo + ":" + number + ":" + firstWord + lastWord).toUpperCase();
    }

    // Lets the user enter the number of messages they want to send
    public static void sendMessages() {

        int howMany = askForNumber("How many messages do you want to enter? ");

        // For loop runs once for each message the user wants to enter
        for (int i = 1; i <= howMany; i++) {

            // message number goes up by one each time the loop runs
            messageCounter++;
            System.out.println("");
            System.out.println("--- Message " + messageCounter + " ---");

            // Recipient cell number (same +27 rule used in registration)
            String recipient;
            while (true) {
                System.out.print("Enter the recipient cell phone number (+27): ");
                recipient = input.nextLine();
                if (checkCellPhoneNumber(recipient)) {
                    break;
                }
                System.out.println("Cell Phone number is incorrectly formatted. Please try again");
            }

            // Message text (1 to 250 characters)
            String text;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                text = input.nextLine();
                if (text.trim().length() > 0 && text.length() <= 250) {
                    break;
                }
                System.out.println("Message must be between 1 and 250 characters. Please try again");
            }

            // Create the ID and hash, then store the message in the ArrayList
            String id = createMessageId();
            String hash = createMessageHash(id, messageCounter, text);
            messages.add(new Message(id, messageCounter, recipient, text, hash));
        }

        // Display all the messages using a for-each loop
        System.out.println("");
        System.out.println("===== Message Details =====");
        for (Message m : messages) {
            System.out.println("Message ID:     " + m.messageId);
            System.out.println("Message Number: " + m.messageNumber);
            System.out.println("Message Hash:   " + m.messageHash);
            System.out.println("Recipient:      " + m.recipient);
            System.out.println("Message:        " + m.messageText);
            System.out.println("---------------------------");
        }

        saveMessagesToJson();
    }

    // Escapes quotes and backslashes so the text is valid inside JSON
    public static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Saves all the messages to messages.json
    // Attribution: the JSON text is built by hand with FileWriter (no library).
    // Code written with AI assistance from Claude (Anthropic, 2026).
    public static void saveMessagesToJson() {
        String json = "[\n";
        boolean firstMessage = true;

        // For-each loop goes through every stored message
        for (Message m : messages) {
            if (!firstMessage) {
                json = json + ",\n";
            }
            firstMessage = false;

            json = json + "  {\n"
                    + "    \"messageId\": \"" + escapeJson(m.messageId) + "\",\n"
                    + "    \"messageNumber\": " + m.messageNumber + ",\n"
                    + "    \"messageHash\": \"" + escapeJson(m.messageHash) + "\",\n"
                    + "    \"recipient\": \"" + escapeJson(m.recipient) + "\",\n"
                    + "    \"message\": \"" + escapeJson(m.messageText) + "\"\n"
                    + "  }";
        }
        json = json + "\n]\n";

        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json);
            System.out.println("Messages saved to messages.json");
        } catch (IOException e) {
            System.out.println("Could not save the messages file.");
        }
    }
}
