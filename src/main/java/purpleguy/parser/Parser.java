package purpleguy.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import purpleguy.Deadline;
import purpleguy.Event;
import purpleguy.Task;
import purpleguy.ToDo;
import purpleguy.exception.AftonException;
import purpleguy.tasklist.TaskList;
import purpleguy.ui.AftonUI;

/**
 * Parses and executes the commands inputted by the user
 */
public class Parser {
    private static AftonUI afton;
    private static TaskList tL;

    /**
     * Creates a new Parser object
     * @param ui AftonUI for printing out success and error messages
     * @param taskList ArrayList of Tasks, the current taskList
     */
    public Parser(AftonUI ui, TaskList taskList) {
        afton = ui;
        tL = taskList;
    }

    /**
     * Formats the input into an array of strings which will be used to validate and execute the command
     * @param input String input from user, to be parsed
     * @throws AftonException If error is occured during validateCommand
     */
    public void parse(String input) throws AftonException {
        String[] caseVars = input.split("\\s+", 2); // To extract command
        validateCommand(caseVars);
        runCommand(caseVars);
    }

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
            if (tL.isEmpty()) {
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

        if (command.equals("find")) {
            if (tL.isEmpty()) {
                throw new AftonException("You're hunting for shadows in an empty room. "
                    + "There is nothing here to find... yet."
                    + "\n[HINT]: Add a task before you attempt to find it");
            }
            return;
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
            if (tL.isEmpty()) {
                throw new AftonException("There is nothing here to silence. You haven't even started your work. "
                                    + "\n[HINT]: Add a task before trying to " + command);
            }
            try {
                int index = Integer.parseInt(details[0]);
                if (index < 1 || index > tL.size()) {
                    throw new AftonException("Are you seeing ghosts? That record doesn't exist. Stay within the count."
                    + "\n[HINT]: Enter a number between 1 and " + tL.size() + ".");
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
                + "\n[HINT]: I only respond to: todo, deadline, event, list, find, mark, unmark, or delete.");
        }

    }

    /**
     * Lists all valid tasks inputted and stored in this task list.
     */

    public static void listTasks() {
        for (int i = 0; i < tL.size(); i++) {
            afton.speak(String.format("%d.%s", (i + 1), tL.get(i)));
        }
    }

    /**
     * Displays all valid tasks in a given list of tasks
     * Used for dislpaying search results
     * @param l List of tasks to display
     */
    public static void listTasks(List<Task> l) {
        for (int i = 0; i < l.size(); i++) {
            afton.speak(String.format("%d.%s", (i + 1), l.get(i)));
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
            Task mTask = tL.get(index);
            mTask.mark();
            afton.speak("Done. It's finally... over. For now.");
            afton.speak(mTask + "\n");
            break;

        case "unmark":
            index = Integer.parseInt(caseVars[1]) - 1;
            Task umTask = tL.get(index);
            umTask.unmark();
            afton.speak("Back again? It seems some things just won't stay buried.");
            afton.speak(umTask + "\n");
            break;

        case "todo":
            taskName = details[0];
            Task td = new ToDo(taskName);
            tL.addTask(td);
            afton.speak("Another? Let's see how long this one lasts.");
            afton.speak(td.toString());
            afton.speak(tL.size() + " entries remain in your little list now.");
            break;

        case "deadline":
            taskName = details[0];
            Task dlTask = new Deadline(taskName, LocalDateTime.parse(details[1]
                .replace("/by", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            tL.addTask(dlTask);
            afton.speak("A deadline? How fitting. Time is a luxury most of them didn't have.");
            afton.speak(dlTask.toString());
            afton.speak("That's " + tL.size() + " clocks ticking in the dark");
            break;

        case "event":
            taskName = details[0];
            Task evTask = new Event(taskName,
                details[1].replace("/from", "").trim(),
                details[2].replace("/to", "").trim());
            tL.addTask(evTask);
            afton.speak("");
            afton.speak(evTask.toString());
            afton.speak("That makes " + tL.size() + " acts to follow.");
            break;

        case "delete":
            index = Integer.parseInt(caseVars[1]) - 1;
            Task delTask = tL.get(index);
            tL.remove(index);
            afton.speak("Erased. A pity... I was starting to like that one."
                + " Now no one will even know it existed.");
            afton.speak(delTask.toString());
            String delMessage = (tL.size() == 0)
                ? "The room is empty. Silence at last... but for how long?"
                : "There are " + tL.size() + " souls left to manage. We aren't finished yet";
            afton.speak(delMessage);
            break;
        case "find":
            String searchTerm = details[0];
            List<Task> results = tL.findTasks(searchTerm);
            if (results.isEmpty()) {
                afton.speak("A fruitless search. "
                    + "That particular memory doesn't exist in my records. Are you sure you didn't imagine it?"
                );
            } else {
                String findMessage = (results.size() == 1)
                    ? "There it is. Standing all alone in the dark. I've brought it to the light for you."
                    : "I've found them. The fragments you were looking for... "
                    + "they couldn't stay hidden from me forever.";
                afton.speak(findMessage);
                listTasks(results);
            }
            break;
        default:
            listTasks();
            break;
        }
    }

}
