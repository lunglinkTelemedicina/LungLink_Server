package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class used to safely read user input from the console.
 * Provides simple methods for reading integers and strings while
 * handling invalid input and avoiding program crashes.
 */
public class UIUtils {

    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    /**
     * Reads an integer from the console. If the user enters something
     * invalid, the method asks again until a valid number is provided.
     * @param message the prompt shown to the user
     * @return the integer entered by the user
     */
    public static int readInt(String message) {

        while (true) {
            try {
                System.out.print(message);
                String input = reader.readLine();
                return Integer.parseInt(input.trim());

            } catch (IOException ex) {
                System.out.println("Error reading input. Try again.");

            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a string from the console. Keeps asking until a non-null
     * string is entered. Leading and trailing spaces are removed.
     *
     * @param message the prompt shown to the user
     * @return the trimmed string entered by the user
     */
    public static String readString (String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = reader.readLine();
                if (input != null) {
                    return input.trim();
                }
            } catch (IOException ex) {
                System.out.println("Error reading input. Try again.");
            }
        }
    }
}
