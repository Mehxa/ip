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

/**
 * Parses and executes the commands inputted by the user
 */
public class Parser {
    private static final String ERROR_MISSING_FROM_VALUE = "The stage is set, but the actors have no cues for /from."
                + " Provide a time, or the curtains stay closed."
                + "\n[HINT]: Provide the timing details immediately after the /from tag";
    private static final String ERROR_MISSING_TO_VALUE = "You've started the performance,"
                + " but left the ending in a void."
                + " Tell me when /to finish it"
                + "\n[HINT]: Provide the timing details immediately after the /to tag";
    private static final String TODO_ADD_MESSAGE = "Another? Let's see how long this one lasts.\n %s\n"
                + "%d entries remain in your little list now \n";
    private static final String DEADLINE_ADD_MESSAGE = "A deadline? How fitting. "
                + "Time is a luxury most of them didn't have.\n %s \n"
                + "That's %d clocks ticking in the dark\n";
    private static final String EVENT_ADD_MESSAGE = "%s\nThat makes %s acts to follow.\n";

    private static TaskList tL;

    /**
     * Creates a new Parser object
     * @param ui AftonUI for printing out success and error messages
     * @param taskList ArrayList of Tasks, the current taskList
     */
    public Parser(TaskList taskList) {
        tL = taskList;
    }

    /**
     * Formats the input into an array of strings which will be used to validate and execute the command
     * @param input String input from user, to be parsed
     * @throws AftonException If error is occured during validateCommand
     */
    public String parse(String input) throws AftonException {
        String[] caseVars = input.split("\\s+", 2); // To extract command
        validateCommand(caseVars);
        return runCommand(caseVars);
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
            validateList(caseVars);
            return;
        }

        // Command missing essential information
        validateMissingInfo(caseVars, command);

        if (command.equals("find")) {
            validateFind();
            return;
        }

        String[] details = caseVars[1].trim().split("\s+(?=/)| ^\s+ | \s+");

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
            validateDeadline(details);
            break;

        case "event":
            validateEvent(details);
            break;

        case "mark":
            // Fallthrough
            // Mark unmark and delete go through the same validation process
        case "unmark":
            // Fallthrough
        case "delete":
            validateIndex(details, command);
            break;
        // Any other unrecognised command
        default:
            throw new AftonException("'" + command + "'? I don't recognize that. Don't waste my time with nonsense. "
                + "\n[HINT]: I only respond to: todo, deadline, event, list, find, mark, unmark, or delete.");
        }

    }

    /**
     * Lists all valid tasks inputted and stored in this task list.
     * @return String of all content in list
     */

    public static String listTasks() {
        assert !tL.isEmpty();
        List<Task> sortedtL = tL.getTasksSortedByPriority();
        String listContent = "";
        for (int i = 0; i < tL.size(); i++) {
            listContent += String.format("%d.%s\n", (i + 1), sortedtL.get(i));
        }
        return listContent;
    }

    /**
     * Displays all valid tasks in a given list of tasks
     * Used for dislpaying search results
     * @param l List of tasks to display
     * @return String of all content in list
     */
    public static String listTasks(List<Task> l) {
        String listContent = "";
        for (int i = 0; i < l.size(); i++) {
            listContent += String.format("%d.%s\n", (i + 1), l.get(i));
        }
        return listContent;
    }

    /**
     * Executes the command inputted by the user
     * Retrieves details needed from caseVars
     * @param caseVars String array of variables derived from the user input
     * @return Output message of the commands
     */
    public static String runCommand(String[] caseVars) {
        String resultString = "";
        if (caseVars[0].equals("list")) {
            return listTasks();
        }
        String[] details = caseVars[1].trim().split("\s+(?=/)| ^\s+ | \s+");
        switch (caseVars[0]) {
        case "mark":
            assert !tL.isEmpty();
            resultString = markTask(Integer.parseInt(caseVars[1]) - 1);
            break;

        case "unmark":
            assert !tL.isEmpty();
            resultString = unmarkTask(Integer.parseInt(caseVars[1]) - 1);
            break;

        case "todo":
            resultString = createTodo(details);
            break;

        case "deadline":
            resultString = createDeadline(details);
            break;

        case "event":
            resultString = createEvent(details);
            break;

        case "delete":
            assert !tL.isEmpty();
            resultString = deleteTask(Integer.parseInt(caseVars[1]) - 1);
            break;

        case "find":
            assert !tL.isEmpty();
            resultString = findTask(details);
            break;

        default:
            listTasks();
            break;
        }
        return resultString;
    }

    private static void validateList(String[] caseVars) throws AftonException {
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
    }

    private static String formatTaskMessage(Task task, String message) {
        return String.format(message, task.toString(), tL.size());
    }

    private static void validateMissingInfo(String[] caseVars, String command) throws AftonException {
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
    }

    private static void validateFind() throws AftonException {
        if (tL.isEmpty()) {
            throw new AftonException("You're hunting for shadows in an empty room. "
                    + "There is nothing here to find... yet."
                    + "\n[HINT]: Add a task before you attempt to find it");
        }
    }

    private static int countTags(String[] details, String tag) {
        int count = 0;
        for (int i = 0; i < details.length; i++) {
            String detail = details[i].trim();
            if (detail.startsWith(tag)) {
                count++;
            }
        }
        return count;
    }

    private static int getTagIndex(String[] details, String tag) {
        int index = -1;
        for (int i = 0; i < details.length; i++) {
            String detail = details[i].trim();
            if (detail.startsWith(tag)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private static void validateTagContent(String[] details, int index,
        String tag, String errorMessage) throws AftonException {
        if (details[index].trim().equals(tag)) {
            throw new AftonException(errorMessage);
        }
    }

    private static void validateDeadline(String[] details) throws AftonException {
        int noOfBy = countTags(details, "/by");
        int noOfFrom = countTags(details, "/from");
        int noOfTo = countTags(details, "/to");

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
    }

    private static void validateEvent(String[] details) throws AftonException {
        int noOfBy = countTags(details, "/by");

        if (noOfBy > 0) {
            throw new AftonException("Events don't have deadlines; they have schedules. Lose the /by tag."
                + "\n[HINT]: Replace '/by' with '/from' and '/to'.");
        }

        int noOfFrom = countTags(details, "/from");
        int noOfTo = countTags(details, "/to");

        if (noOfFrom != 1 || noOfTo != 1) {
            throw new AftonException("The stage is set, but the timing is incomplete."
                + " I require exactly one start and one end."
                + "\n[HINT]: Ensure you have exactly one '/from' and one '/to' tag.");
        }

        int fromIdx = getTagIndex(details, "/from");
        int toIdx = getTagIndex(details, "/to");

        if (toIdx < fromIdx) {
            throw new AftonException("You're trying to end the show before the curtains even rise?"
                + " Order is everything."
                + "\n[HINT]: Place the '/from' tag before the '/to' tag.");
        }

        validateTagContent(details, fromIdx, "/from", ERROR_MISSING_FROM_VALUE);
        validateTagContent(details, toIdx, "/to", ERROR_MISSING_TO_VALUE);
    }

    private static void validateIndex(String[] details, String command) throws AftonException {
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
    }

    private static String markTask(int index) {
        Task mTask = tL.get(index);
        mTask.mark();
        return "Done. It's finally... over. For now.\n" + mTask + "\n";
    }

    private static String unmarkTask(int index) {
        Task umTask = tL.get(index);
        umTask.unmark();
        return "Back again? It seems some things just won't stay buried.\n" + umTask + "\n";
    }

    private static String createTodo(String[] details) {
        String taskName = details[0];
        Task td = new ToDo(taskName);
        tL.addTask(td);
        return formatTaskMessage(td, TODO_ADD_MESSAGE);
    }

    private static String createDeadline(String[] details) {
        String taskName = details[0];
        Task dlTask = new Deadline(taskName, LocalDateTime.parse(details[1]
            .replace("/by", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        tL.addTask(dlTask);
        return formatTaskMessage(dlTask, DEADLINE_ADD_MESSAGE);
    }

    private static String createEvent(String[] details) {
        String taskName = details[0];
        Task evTask = new Event(taskName,
            details[1].replace("/from", "").trim(),
            details[2].replace("/to", "").trim());
        tL.addTask(evTask);
        return formatTaskMessage(evTask, EVENT_ADD_MESSAGE);
    }

    private static String deleteTask(int index) {
        Task delTask = tL.get(index);
        tL.remove(index);
        String resultString = "Erased. A pity... I was starting to like that one."
            + " Now no one will even know it existed.\n";
        resultString += delTask.toString() + "\n";
        String delMessage = (tL.size() == 0)
            ? "The room is empty. Silence at last... but for how long?\n"
            : "There are " + tL.size() + " souls left to manage. We aren't finished yet.\n";
        resultString += delMessage;
        return resultString;
    }

    private static String findTask(String[] details) {
        String searchTerm = details[0];
        List<Task> results = tL.findTasks(searchTerm);
        String resultString = "";
        if (results.isEmpty()) {
            resultString = "A fruitless search. "
                + "That particular memory doesn't exist in my records. "
                + "Are you sure you didn't imagine it?\n";
        } else {
            String findMessage = (results.size() == 1)
                ? "There it is. Standing all alone in the dark. I've brought it to the light for you.\n"
                : "I've found them. The fragments you were looking for... "
                + "they couldn't stay hidden from me forever.\n";
            resultString += findMessage;
            resultString += listTasks(results);
        }
        return resultString;
    }

}
