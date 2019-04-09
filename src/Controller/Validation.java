/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author J
 */
public class Validation {

    String output;
    Boolean validated;
    String apos = "'";
    String slash = "/";
    String quote = "\"";
    String ampersand = "&";
//    String escape = "['\"\\\\&<>]";
//    Pattern regex = Pattern.compile("[$&+,:;=?@#|]+");
//    Pattern pattern = Pattern.compile(escape);

    public static void main(String[] args) {
        Validation v = new Validation();
        String desanitize = v.sanitize("hell;o&semi");
        v.deSanitize(desanitize);
        String desanitizes = v.sanitize("<script> console.alert('XSS'); console.log('HACKED')&lg&gt&amp;</script>");
        v.deSanitize(desanitizes);
        System.out.println(v.validate("asdasdasdasdasdasdasdasdasdasdas")); // return false > 32
        System.out.println(v.validate("asdasdasdasdasdasdasdasdasdasdas")); // return false > 32
        System.out.println(v.validate(100000000)); // return false > 2 billion
        System.out.println(v.validate(100.101)); // false 3 decimals
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
            return (Integer.parseInt(input) < 2_000_000_000 && input.length() < 32 && input.length() > 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateDoubleSize(String input) {
        if (input.contains(".")) {
            int integerPlaces = input.indexOf('.');
            int decimalPlaces = input.length() - integerPlaces - 1;
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0 && decimalPlaces <= 2);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
