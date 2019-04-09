/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author J
 */
public class Validation {

    Boolean validated;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    private String errorMessage;

//    String escape = "['\"\\\\&<>]";
//    Pattern regex = Pattern.compile("[$&+,:;=?@#|]+");
//    Pattern pattern = Pattern.compile(escape);
    public static void main(String[] args) {
        Path source = Paths.get("database.db");
        Path destination = Paths.get("dist/database.db");
        Validation v = new Validation();
        String desanitize = v.sanitize("hell;o&semi");
        v.deSanitize(desanitize);
        String desanitizes = v.sanitize("<script> console.alert('XSS'); console.log('HACKED')&lg&gt&amp;</script>");
        v.deSanitize(desanitizes);
        System.out.println(v.validate("asdasdasdasdasdasdasdasdasdasdas")); // return false > 32
        System.out.println(v.validate("asdasdasdasdasdasdasdasdasdasdas")); // return false > 32
        System.out.println(v.validate(100000000)); // return false > 2 billion
        System.out.println(v.validate(100.101)); // false 3 decimals
        System.out.println(v.validateDoubleSize("asd"));
        System.out.println(v.randomString());
        v.validate(v.randomString());
        v.copyFile(source, destination);
    }

    public void test() {
        validate("hello");
    }

    public boolean validate(Object input) {
        if (input instanceof String) {
            input = sanitize((String) input);
            validated = validateStringSize((String) input);
            System.out.println(validateStringSize((String) input));
            return validated;
        } else if (input instanceof Integer) {
            validated = validateIntSize(Integer.toString((int) input));
            return validated;
        } else if (input instanceof Double) {
            validated = validateDoubleSize(Double.toString((double) input));
        }
        return validated;
    }

    public String sanitize(String input) {
        input = input.contains("&") ? input.replaceAll("&", "&amp") : input;
        input = input.contains("<") ? input.replaceAll("<", "&lt") : input;
        input = input.contains(">") ? input.replaceAll(">", "&gt") : input;
        input = input.contains("\\") ? input.replaceAll("\\\\", "&back") : input;
        input = input.contains(";") ? input.replaceAll(";", "&semi") : input;

        System.out.println("input sa = " + input);
        return input;
    }

    public String deSanitize(String input) {
        input = input.contains("&lt") ? input.replaceAll("&lt", "<") : input;
        input = input.contains("&gt") ? input.replaceAll("&gt", ">") : input;
        input = input.contains("&semi") ? input.replaceAll("&semi", ";") : input;
        input = input.contains("&back") ? input.replaceAll("&back", "\\") : input;
        input = input.contains("&amp") ? input.replaceAll("&amp", "&") : input;
        System.out.println("input de = " + input);
        return input;
    }
//
//    public void match(String input){
//        Matcher matcher = regex.matcher(input);
//        if(matcher.find()){
//            String out = matcher.group();
//            out = out.replaceAll(";", "&semi");
//            System.out.println("out = " + out);
//        }
//        
//    }

    public boolean validateStringSize(String input) {

        return (input.length() < 32) && input.length() > 0;
    }

    public boolean validateIntSize(String input) {
        try {
            return (Integer.parseInt(input) < 2_000_000_000 && input.length() < 32 && input.length() > 0 && !(Integer.parseInt(input) < 0));
        } catch (NumberFormatException e) {
            e.printStackTrace(pw);
            setErrorMessage(sw.toString()); // stack trace as a string
            return false;
        }
    }

    public boolean validateDoubleSize(String input) {
        if (input.contains(".")) {
            int integerPlaces = input.indexOf('.');
            int decimalPlaces = input.length() - integerPlaces - 1;
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0 && decimalPlaces <= 2 && !(Double.parseDouble(input) < 0));
            } catch (NumberFormatException e) {
                e.printStackTrace(pw);
                setErrorMessage(sw.toString()); // stack trace as a string
                return false;
            }
        } else {
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0 && !(Double.parseDouble(input) < 0));
            } catch (NumberFormatException e) {
                e.printStackTrace(pw);
                setErrorMessage(sw.toString()); // stack trace as a string
                return false;
            }
        }
    }

    public String randomString() {
        Random r = new Random();
        ArrayList<Character> generatedString = new ArrayList<Character>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
        String generated = "";
        for (int i = 0; i < 10; i++) {
            generatedString.add(alphabet.charAt(r.nextInt(alphabet.length())));
            for (char s : generatedString) {
                generated += s + "";
            }
        }
        System.out.println("generated = " + generated);

        return generated;
    }

    public static void copyFile(Path from, Path to){
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copied");
        } catch (Exception e) {
        }
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
}
