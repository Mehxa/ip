package purpleguy;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Runs the main() code for the PurpleGuy program.
 * Implements CRUD functionality for various types of tasks.
 */

public class PurpleGuy {
    private static AftonUI afton = new AftonUI();
    private static Storage storageFile = new Storage();
    private static ArrayList<Task> taskList = new ArrayList<>(100);

    /**
     * Validates input given by the user
     * @param caseVars String array of variables derived from the user input
     * @throws AftonException If input is invalid: Missing tag/task name, improper tag/command usage
     */

    public static void validateCommand(String[] caseVars) throws AftonException {
        String command = caseVars[0].toLowerCase();
        // Resolving list first as it is the only command
        // which does not require extra parameters
        if (command.equals("list")) {
            if (caseVars.length > 1) {
                throw new AftonException("Do you think I'm blind? "
                                    + "I don't need your 'extra information' to view my own ledger. "
                                    + "Just say the word and be quiet."
                                    + "\n[HINT]: The 'list' command requires no additional words.");
            }
            if (taskList.isEmpty()) {
                throw new AftonException("There is nothing here to silence. You haven't even started your work. "
                                    + "\n[HINT]: Add a task before trying to list");
            }
            return;
        }

        // Command missing essential information
        if (caseVars.length < 2 || caseVars[1].trim().isEmpty() || caseVars[1].startsWith("/")) {
            // More specific error message for missing index rather than missing name
            if (command.equals("mark") || command.equals("unmark") || command.equals("delete")) {
                throw new AftonException("You're pointing at thin air. Give me a number, or step away. "
                                    + "\n[HINT]: Usage: " + command + " [task index number]");
            }
            throw new AftonException("A hollow entry? Much like those empty suits, "
                                + "it's useless without...something inside. Give it a name."
                                + "\n[HINT]: Try: " + command + " [description] ...");
        }
        String[] details = caseVars[1].trim().split("\\s+(?=/)| ^\\s+ | \\s+");
        int noOfBy = 0;
        int noOfFrom = 0;
        int noOfTo = 0;
        int fromIdx = -1;
        int toIdx = -1;

        for (int i = 0; i < details.length; i++) {
            String detail = details[i].trim();
            if (detail.startsWith("/by")) {
                noOfBy++;
            }
            if (detail.startsWith("/from")) {
                noOfFrom++;
                fromIdx = i;
            }
            if (detail.startsWith("/to")) {
                noOfTo++;
                toIdx = i;
            }
        }

        switch (command) {
        case "todo":
            // Todo command inputted with /by, /from or /to
            if (details.length > 1) {
                throw new AftonException("A simple task shouldn't have extra baggage."
                    + " Keep it clean... like a well-wiped crime scene."
                    + "\n[HINT]: Todo tasks do not use /by, /from, or /to tags.");
            }
            break;

        case "deadline":
            if (noOfFrom > 0 || noOfTo > 0) {
                throw new AftonException("You're confusing a deadline with an event."
                    + " Only one end matters here."
                    + "\n[HINT]: Remove /from and /to. Only use /by.");
            }
            if (noOfBy == 0) {
                throw new AftonException("How disappointing. You forgot the '/by' tag."
                    + " Precision is the difference between a masterpiece and a... mess."
                    + "\n[HINT]: A deadline requires a '/by [time]' marker.");
            }
            if (noOfBy > 1) {
                throw new AftonException("Too many endings? Even I only needed one."
                    + " One /by is enough."
                    + "\n[HINT]: Ensure you only have one '/by' tag.");
            }
            if (details[1].trim().equals("/by")) {
                throw new AftonException("A tag with no data? You're stalling. Tell me *when* the clock stops for "
                    + details[0]
                    + "\n[HINT]: Provide the timing details immediately after the /by tag");
            }
            break;

        case "event":
            if (noOfBy > 0) {
                throw new AftonException("Events don't have deadlines; they have schedules. Lose the /by tag."
                    + "\n[HINT]: Replace '/by' with '/from' and '/to'.");
            }
            if (noOfFrom != 1 || noOfTo != 1) {
                throw new AftonException("The stage is set, but the timing is incomplete."
                    + " I require exactly one start and one end."
                    + "\n[HINT]: Ensure you have exactly one '/from' and one '/to' tag.");
            }
            if (toIdx < fromIdx) {
                throw new AftonException("You're trying to end the show before the curtains even rise?"
                    + " Order is everything."
                    + "\n[HINT]: Place the '/from' tag before the '/to' tag.");
            }

            if (details[1].trim().equals("/from")) {
                throw new AftonException("The stage is set, but the actors have no cues for /from."
                    + " Provide a time, or the curtains stay closed."
                    + "\n[HINT]: Provide the timing details immediately after the /from tag");
            }
            if (details[2].trim().equals("/to")) {
                throw new AftonException("You've started the performance, but left the ending in a void."
                    + " Tell me when /to finish it"
                    + "\n[HINT]: Provide the timing details immediately after the /to tag");
            }
            break;

        case "mark":
            // Fallthrough
            // Mark unmark and delete go through the same validation process
        case "unmark":
            // Fallthrough
        case "delete":
            if (taskList.isEmpty()) {
                throw new AftonException("There is nothing here to silence. You haven't even started your work. "
                                    + "\n[HINT]: Add a task before trying to " + command);
            }
            try {
                int index = Integer.parseInt(details[0]);
                if (index < 1 || index > taskList.size()) {
                    throw new AftonException("Are you seeing ghosts? That record doesn't exist. Stay within the count."
                    + "\n[HINT]: Enter a number between 1 and " + taskList.size() + ".");
                }
            } catch (NumberFormatException e) {
                throw new AftonException("'" + details[0] + "' is not a number."
                + "I require mathematical precision, not guesswork."
                + "\n[HINT]: Provide a valid integer index.");
            }
            break;
        // Any other unrecognised command
        default:
            throw new AftonException("'" + command + "'? I don't recognize that. Don't waste my time with nonsense. "
            + "\n[HINT]: I only respond to: todo, deadline, event, list, mark, unmark, or delete.");
        }

    }

    /**
     * Lists all valid tasks inputted and stored in the task list.
     */

    public static void listTasks() {
        for (int i = 0; i < taskList.size(); i++) {
            afton.speak(String.format("%d.%s", (i + 1), taskList.get(i)));
        }
    }

    /**
     * Executes the command inputted by the user
     * Retrieves details needed from caseVars
     * @param caseVars String array of variables derived from the user input
     */
    public static void runCommand(String[] caseVars) {
        int index;
        String taskName;
        if (caseVars[0].equals("list")) {
            listTasks();
            System.out.println();
            return;
        }
        String[] details = caseVars[1].trim().split("\\s+(?=/)| ^\\s+ | \\s+");
        switch (caseVars[0]) {
        case "mark":
            index = Integer.parseInt(caseVars[1]) - 1;
            Task mTask = taskList.get(index);
            mTask.mark();
            afton.speak("Done. It's finally... over. For now.");
            afton.speak(mTask + "\n");
            storageFile.storeTL(taskList);
            break;

        case "unmark":
            index = Integer.parseInt(caseVars[1]) - 1;
            Task umTask = taskList.get(index);
            umTask.unmark();
            afton.speak("Back again? It seems some things just won't stay buried.");
            afton.speak(umTask + "\n");
            storageFile.storeTL(taskList);
            break;

        case "todo":
            taskName = details[0];
            Task td = new ToDo(taskName);
            taskList.add(td);
            afton.speak("Another? Let's see how long this one lasts.");
            afton.speak(td.toString());
            afton.speak(taskList.size() + " entries remain in your little list now.");
            storageFile.storeTL(taskList);
            break;

        case "deadline":
            taskName = details[0];
            Task dlTask = new Deadline(taskName, LocalDateTime.parse(details[1]
                .replace("/by", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            taskList.add(dlTask);
            afton.speak("A deadline? How fitting. Time is a luxury most of them didn't have.");
            afton.speak(dlTask.toString());
            afton.speak("That's " + taskList.size() + " clocks ticking in the dark");
            storageFile.storeTL(taskList);
            break;

        case "event":
            taskName = details[0];
            Task evTask = new Event(taskName,
                details[1].replace("/from", "").trim(),
                details[2].replace("/to", "").trim());
            taskList.add(evTask);
            afton.speak("");
            afton.speak(evTask.toString());
            afton.speak("That makes " + taskList.size() + " acts to follow.");
            storageFile.storeTL(taskList);
            break;

        case "delete":
            index = Integer.parseInt(caseVars[1]) - 1;
            Task delTask = taskList.get(index);
            taskList.remove(index);
            afton.speak("Erased. A pity... I was starting to like that one."
                + " Now no one will even know it existed.");
            afton.speak(delTask.toString());
            String delMessage = (taskList.size() == 0)
                ? "The room is empty. Silence at last... but for how long?"
                : "There are " + taskList.size() + " souls left to manage. We aren't finished yet";
            afton.speak(delMessage);
            storageFile.storeTL(taskList);
            break;


        default:
            listTasks();
            break;
        }
    }

    public static void main(String[] args) {
        storageFile.readTL(taskList);
        afton.initialise();
        String userInput = afton.readInput();
        while (!userInput.equals("bye")) {
            try {
                if (userInput.trim().isEmpty()) {
                    throw new AftonException("Silence? You wake me only to offer... nothing?"
                    + " Speak, or stay out of my wires."
                    + "\n[HINT]: Type a valid command (todo, deadline, event, mark, unmark).");
                }
                String[] caseVars = userInput.split("\\s+", 2); // To extract command
                validateCommand(caseVars);
                runCommand(caseVars);
            } catch (AftonException e) {
                afton.speak(e.getMessage());
            }
            userInput = afton.readInput();
        }
        afton.shutDown();
    }
}
