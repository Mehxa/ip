package purpleguy.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import purpleguy.CommandValidator;
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
    private static final String TODO_ADD_MESSAGE = "Another? Let's see how long this one lasts.\n %s\n"
                + "%d entries remain in your little list now \n";
    private static final String DEADLINE_ADD_MESSAGE = "A deadline? How fitting. "
                + "Time is a luxury most of them didn't have.\n %s \n"
                + "That's %d clocks ticking in the dark\n";
    private static final String EVENT_ADD_MESSAGE = "%s\nThat makes %s acts to follow.\n";

    // Help messages
    private static final String TODO_HELP_MESSAGE = "Command: todo [name]\n"
                + "For your mundane daily tasks\n";
    private static final String DEADLINE_HELP_MESSAGE = "Command: deadline [name] /by [time]\n"
                + "[time] must follow yyyy-MM-dd HH:mm format\n"
                + "When the clock is ticking against you\n";
    private static final String EVENT_HELP_MESSAGE = "Command: event [name] /from [time] /to [time]\n"
                + "For the performances you must attend\n";
    private static final String LIST_HELP_MESSAGE = "Command: list\n"
                + "Do not add any words after list\n"
                + "To see the current residents of my ledger.\n";
    private static final String FIND_HELP_MESSAGE = "Command: find [keyword]\n"
                + "To hunt for fragments of the past\n";
    private static final String MARK_HELP_MESSAGE = "Command: mark [index]\n"
                + "[index] must be a number\n"
                + "Use the index to silence a record. Once marked, it belongs to the past.\n";
    private static final String UNMARK_HELP_MESSAGE = "Command: unmark [index]\n"
                + "[index] must be a number\n"
                + "Nothing ever stays dead here. If you've made a mistake,"
                + " I can drag that record back into the light.\n";
    private static final String DELETE_HELP_MESSAGE = "Command: delete [index]\n"
                + "[index] must be a number\n"
                + "To erase something...or someone...forever\n";

    private static TaskList tL;
    private final CommandValidator validator;

    /**
     * Creates a new Parser object
     * @param ui AftonUI for printing out success and error messages
     * @param taskList ArrayList of Tasks, the current taskList
     */
    public Parser(TaskList taskList) {
        tL = taskList;
        validator = new CommandValidator();
    }

    /**
     * Formats the input into an array of strings which will be used to validate and execute the command
     * @param input String input from user, to be parsed
     * @throws AftonException If error is occured during validateCommand
     */
    public String parse(String input) throws AftonException {
        String[] caseVars = input.split("\\s+", 2); // To extract command
        String command = caseVars[0].toLowerCase();
        String[] details = (caseVars.length < 2 || caseVars[1].trim().isEmpty())
            ? new String[0]
            : caseVars[1].trim().split("\s+(?=/)| ^\s+ | \s+");
        validator.validate(command, details, tL);
        return runCommand(caseVars);
    }

    /**
     * Lists all valid tasks inputted and stored in this task list.
     * @return String of all content in list
     */

    public static String listTasks() {
        assert !tL.isEmpty();
        String listContent = "";
        for (int i = 0; i < tL.size(); i++) {
            listContent += String.format("%d.%s\n", (i + 1), tL.get(i));
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

        if (caseVars[0].equals("help") && caseVars.length == 1) {
            return displayAllHelp();
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

        case "help":
            resultString = displayHelp(details[0]);
            break;

        default:
            listTasks();
            break;
        }
        return resultString;
    }

    private static String formatTaskMessage(Task task, String message) {
        return String.format(message, task.toString(), tL.size());
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

    private static String displayHelp(String command) {
        String resultString = "";
        switch (command) {
        case "list":
            resultString = LIST_HELP_MESSAGE;
            break;
        case "todo":
            resultString = TODO_HELP_MESSAGE;
            break;
        case "deadline":
            resultString = DEADLINE_HELP_MESSAGE;
            break;
        case "event":
            resultString = EVENT_HELP_MESSAGE;
            break;
        case "mark":
            resultString = MARK_HELP_MESSAGE;
            break;
        case "unmark":
            resultString = UNMARK_HELP_MESSAGE;
            break;
        case "find":
            resultString = FIND_HELP_MESSAGE;
            break;
        case "delete":
            resultString = DELETE_HELP_MESSAGE;
            break;
        default:
            break;
        }
        return resultString;
    }

    private static String displayAllHelp() {
        return LIST_HELP_MESSAGE
            + TODO_HELP_MESSAGE + "\n"
            + DEADLINE_HELP_MESSAGE + "\n"
            + EVENT_HELP_MESSAGE + "\n"
            + MARK_HELP_MESSAGE + "\n"
            + UNMARK_HELP_MESSAGE + "\n"
            + FIND_HELP_MESSAGE + "\n"
            + DELETE_HELP_MESSAGE;
    }

}
