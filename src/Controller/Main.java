package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import View.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;

public class Main extends Validation {

    public SQLite sqlite;
    public int logInAttempts = 0;
    private int passLength = 0;
    private User LoggedInUsername;
    public Validation validation;
    Properties prop = new Properties();
    InputStream input = null;
    private String errorMessage;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    private String generated;

    public static void main(String[] args) {
        new Main().init();
        //m.init();
    }

    public void init() {
        // Initialize a driver object
        sqlite = new SQLite();
//        // Create a database
        sqlite.createNewDatabase();
//        
//        // Drop users table if needed
        sqlite.dropHistoryTable();
        sqlite.dropLogsTable();
        sqlite.dropProductTable();
        sqlite.dropUserTable();
//        
//        // Create users table if not exist
        sqlite.createHistoryTable();
        sqlite.createLogsTable();
        sqlite.createProductTable();
        sqlite.createUserTable();
//        
//        // Add sample history
        sqlite.addHistory("admin", "Antivirus", 1, 500.0, "2019-04-03 14:30:00.000");
        sqlite.addHistory("manager", "Firewall", 1, 1000.0, "2019-04-03 14:30:01.000");
        sqlite.addHistory("staff", "Scanner", 1, 100.0, "2019-04-03 14:30:02.000");
//        
//        // Add sample logs
        sqlite.addLogs("NOTICE", "admin", "User creation successful", new Timestamp(new Date().getTime()).toString());
        sqlite.addLogs("NOTICE", "manager", "User creation successful", new Timestamp(new Date().getTime()).toString());
        sqlite.addLogs("NOTICE", "admin", "User creation successful", new Timestamp(new Date().getTime()).toString());
//        
//        // Add sample product
        sqlite.addProduct("Antivirus", 5, 500.0);
        sqlite.addProduct("Firewall", 3, 1000.0);
        sqlite.addProduct("Scanner", 10, 100.0);
//
//        // Add sample users
        sqlite.addUser("admin", hashPassword("qwerty1234"), 5);
        sqlite.addUser("manager", hashPassword("qwerty1234"), 4);
        sqlite.addUser("staff", hashPassword("qwerty1234"), 3);
        sqlite.addUser("client1", hashPassword("qwerty1234"), 2);
        sqlite.addUser("client2", hashPassword("qwerty1234"), 2);
//        
//        
//        // Get users
        ArrayList<History> histories = sqlite.getHistory();
        for (int nCtr = 0; nCtr < histories.size(); nCtr++) {
            System.out.println("===== History " + histories.get(nCtr).getId() + " =====");
            System.out.println(" Username: " + histories.get(nCtr).getUsername());
            System.out.println(" Name: " + histories.get(nCtr).getName());
            System.out.println(" Stock: " + histories.get(nCtr).getStock());
            System.out.println(" Timestamp: " + histories.get(nCtr).getTimestamp());
        }
//        
//        // Get users
        ArrayList<Logs> logs = sqlite.getLogs();
        for (int nCtr = 0; nCtr < logs.size(); nCtr++) {
            System.out.println("===== Logs " + logs.get(nCtr).getId() + " =====");
            System.out.println(" Username: " + logs.get(nCtr).getEvent());
            System.out.println(" Password: " + logs.get(nCtr).getUsername());
            System.out.println(" Role: " + logs.get(nCtr).getDesc());
            System.out.println(" Timestamp: " + logs.get(nCtr).getTimestamp());
        }
//        
//        // Get users
        ArrayList<Product> products = sqlite.getProduct();
        for (int nCtr = 0; nCtr < products.size(); nCtr++) {
            System.out.println("===== Product " + products.get(nCtr).getId() + " =====");
            System.out.println(" Name: " + products.get(nCtr).getName());
            System.out.println(" Stock: " + products.get(nCtr).getStock());
            System.out.println(" Price: " + products.get(nCtr).getPrice());
        }
//        // Get users
        ArrayList<User> users = sqlite.getUsers();
        for (int nCtr = 0; nCtr < users.size(); nCtr++) {
            System.out.println("===== User " + users.get(nCtr).getId() + " =====");
            System.out.println(" Username: " + users.get(nCtr).getUsername());
            System.out.println(" Password: " + users.get(nCtr).getPassword());
            System.out.println(" Role: " + users.get(nCtr).getRole());
            System.out.println(" Locked: " + users.get(nCtr).getLocked());
        }

        // Initialize User Interface
        Frame frame = new Frame();
        frame.init(this);
        loadResources();
    }

    private byte[] generateSalt() { // generates random salt
        Random random = new Random();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes;
    }

    public String hashPassword(String password) {

        try {
            int iterations = 1; // random number of iterations to perform
            char[] chars = password.toCharArray();
            byte[] salt = generateSalt();
            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512); // hash password iterations slow down log in
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return iterations + ":" + bytesToHex(salt) + ":" + bytesToHex(hash);
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            setErrorMessage(sw.toString()); // stack trace as a string
        }
        return null;
    }

    public boolean validatePassword(String originalPassword, String storedPassword) { // check if equal password
        try {
            String[] parts = storedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) { // slow function to compare the byte arrays
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0; // true if the arrays are equal false if not
        } catch (Exception ex) {
            ex.printStackTrace(pw);
            setErrorMessage(sw.toString()); // stack trace as a string
        }
        return false;
    }

    private String bytesToHex(byte[] input) { // convert hexadecimal to byte[]
        return DatatypeConverter.printHexBinary(input);
    }

    private static byte[] fromHex(String hex) { // convert byte[] to hexadecimal
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public boolean checkRequiredMinPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
        if (password.matches(passwordRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addUser(String username, String password) {
        ArrayList<User> users = sqlite.getUsers();
        User user = new User(username, password);
        if (checkRequiredMinPassword(password)) {
            if (sqlite.checkExistingUsers(username)) {
                sqlite.addUser(username, hashPassword(password));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean updatePassword(String username, String password) {
        ArrayList<User> users = sqlite.getUsers();
        User user = new User(username, password);
        if (checkRequiredMinPassword(password)) {
            sqlite.updatePassword(username, hashPassword(password));
            return true;
        } else {

            return false;
        }
    }

    public boolean loginUser(String username, String password) { // check login
        ArrayList<User> users = sqlite.getUsers();
        for (int ctr = 0; ctr < users.size(); ctr++) {
            if (username.equals(users.get(ctr).getUsername()) && users.get(ctr).getLocked() == 3) {
                JOptionPane.showMessageDialog(null, "Succeeded Log in Attempts");
                return false;
            }
            if (username.equals(users.get(ctr).getUsername())) { // check if username in list
                if (validatePassword(password, users.get(ctr).getPassword())) { // check if password matches user password
                    saveLoggedInUser(users.get(ctr).getId(), users.get(ctr).getUsername(), users.get(ctr).getPassword(), users.get(ctr).getRole());
                    return true;
                } else {
                    int up = users.get(ctr).getLocked();
                    up++;
                    sqlite.updateLocked(username, up);
                    return false;
                }
            }
        }
        return false;
    }

    public void saveLoggedInUser(int id, String username, String password, int role) {
        this.LoggedInUsername = new User(id, username, password, role, 0);
    }

    public User getLoggedInUser() {
        return this.LoggedInUsername;
    }

    public void removeLoggedInUser() {
        this.LoggedInUsername = null;
    }

    public void loadResources() {

        try {

            input = getClass().getResourceAsStream("/properties/errorResource.properties");;

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("ERROR404"));
            System.out.println(prop.getProperty("ERROR500"));

        } catch (IOException ex) {
            ex.printStackTrace(pw);
            setErrorMessage(sw.toString()); // stack trace as a string
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace(pw);
                    setErrorMessage(sw.toString()); // stack trace as a string
                }
            }
        }

    }

    public String getError(String error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {

            input = getClass().getResourceAsStream("/properties/errorResource.properties");;

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("ERROR404"));
            System.out.println(prop.getProperty("ERROR500"));

        } catch (IOException ex) {
            ex.printStackTrace(pw);
            setErrorMessage(sw.toString()); // stack trace as a string
            System.out.println(getErrorMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace(pw);
                    setErrorMessage(sw.toString()); // stack trace as a string

                }
            }
        }
        return prop.getProperty(error);
    }

    public String randomString() {
        Random r = new Random();
        ArrayList<Character> generatedString = new ArrayList<Character>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
        setGenerated("");
        for (int i = 0; i < 3; i++) {
            generatedString.add(alphabet.charAt(r.nextInt(alphabet.length())));
            for (char s : generatedString) {
                setGenerated(getGenerated() + s + "");
            }
        }
        return getGenerated();
    }

    public void copyFile() {
        Path source = Paths.get("database.db");
        Path destination = Paths.get("dist/database.db");
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copied");
        } catch (Exception e) {
            System.out.println("not copied");
        }
    }

    public void getErrorFromValidation() {
        setErrorMessage(validation.getErrorMessage());
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the generated
     */
    public String getGenerated() {
        return generated;
    }

    /**
     * @param generated the generated to set
     */
    public void setGenerated(String generated) {
        this.generated = generated;
    }
}

//    public String decrypt(byte[] encrypted, SecretKey secretKey){
//        Cipher cipher;
//        try {
//            cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            byte[] decrypted = cipher.doFinal(encrypted);
//            return new String(decrypted);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    public byte[] encryptPassword(String password, SecretKey secretKey){
//        Cipher cipher;
//        try {
//            cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            byte[] encrypted = cipher.doFinal(password.getBytes()); // encrypt hashedPassword
//            return encrypted;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//    public SecretKey getSecretKey() {
//        KeyGenerator keyGen;
//        SecretKey secretKey;
//        try {
//            keyGen = KeyGenerator.getInstance("AES"); // encrypt in AES
//            keyGen.init(128); // encrypt in AES 128 bit
//            secretKey = keyGen.generateKey();
//            return secretKey;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
//        public byte[] hashPassword(String password, byte[] salt) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.reset();
//            md.update(salt);
//            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8)); // digest password
//            return hashed;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
    
//}
