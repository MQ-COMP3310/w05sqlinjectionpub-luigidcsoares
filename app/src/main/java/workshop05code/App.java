package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    private static final ClassLoader classLoader = App.class.getClassLoader();
    private static final String dataPath = classLoader.getResource("data.txt").getPath();
    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    static {
	String logPropertiesPath = classLoader.getResource("logging.properties").getPath();

        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
	    FileInputStream logProperties = new FileInputStream(logPropertiesPath);
            LogManager.getLogManager().readConfiguration(logProperties);
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }
    // End code for logging exercise

    private static Boolean isValidGuess(String guess) {
	int validSize = 4;
	String validRegex = String.format("[a-z]{%d}", validSize);
	return guess.matches(validRegex);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader(dataPath))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");
		if (!isValidGuess(guess)) {
		    String invalidMsg = "This is not a valid guess!"
			+ " A guess must consist of four lowercase letters a-z."
			+ " Please, try again: ";
		    
		    System.out.print(invalidMsg);
		    guess = scanner.nextLine();
		    continue;
		}

                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
