package purpleguy;


import java.util.ArrayList;

/**
 * Runs the main() code for the PurpleGuy program.
 * Implements CRUD functionality for various types of tasks.
 */

public class PurpleGuy {
    private static AftonUI afton = new AftonUI();
    private static Storage storageFile = new Storage();
    private static ArrayList<Task> taskList = new ArrayList<>(100);

    public static void main(String[] args) {
        storageFile.readTL(taskList);
        Parser p = new Parser(afton, taskList);
        afton.initialise();
        String userInput = afton.readInput();
        while (!userInput.equals("bye")) {
            try {
                if (userInput.trim().isEmpty()) {
                    throw new AftonException("Silence? You wake me only to offer... nothing?"
                    + " Speak, or stay out of my wires."
                    + "\n[HINT]: Type a valid command (todo, deadline, event, mark, unmark).");
                }
                p.parse(userInput);
            } catch (AftonException e) {
                afton.speak(e.getMessage());
            }
            userInput = afton.readInput();
        }
        afton.shutDown();
        storageFile.storeTL(taskList);
    }
}
