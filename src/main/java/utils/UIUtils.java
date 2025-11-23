package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIUtils {

    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

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

    public static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = reader.readLine();

                return Double.parseDouble(input.trim());

            } catch (IOException ex) {
                System.out.println("Error reading input. Try again.");

            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
