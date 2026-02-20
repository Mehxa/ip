package purpleguy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import purpleguy.storage.Storage;
import purpleguy.tasklist.TaskList;

/**
 * Executes the commands inputted by the user
 */
public class CommandRunner {
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
    private static final String HELP_HELP_MESSAGE = "Command: help [command]\n"
                + "Just type 'help' followed by the name of a command to peek behind the curtain.\n";
    private static final String BYE_HELP_MESSAGE = "Command: bye\n"
                + "Use this to sever our connection... but remember, I always come back.\n";

    private static TaskList tL;
    private static Storage storageFile = new Storage();

    public CommandRunner(TaskList taskList) {
        tL = taskList;
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
            listContent += String.format("%s\n", l.get(i));
        }
        return listContent;
    }

    /**
     * Executes the command inputted by the user
     * Retrieves details needed from caseVars
     * @param command Command derived from the user input
     * @param details String array of details derived from the user input
     * @return Output message of the commands
     */
    public String run(String command, String[] details) {
        String resultString = "";
        if (command.equals("list")) {
            return listTasks();
        }

        if (command.equals("help") && details.length == 0) {
            return displayAllHelp();
        }

        switch (command) {
        case "mark":
            assert !tL.isEmpty();
            resultString = markTask(Integer.parseInt(details[0]) - 1);
            break;

        case "unmark":
            assert !tL.isEmpty();
            resultString = unmarkTask(Integer.parseInt(details[0]) - 1);
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
            resultString = deleteTask(Integer.parseInt(details[0]) - 1);
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
        storageFile.storeTL(tL);
        return "Done. It's finally... over. For now.\n" + mTask + "\n";
    }

    private static String unmarkTask(int index) {
        Task umTask = tL.get(index);
        umTask.unmark();
        storageFile.storeTL(tL);
        return "Back again? It seems some things just won't stay buried.\n" + umTask + "\n";
    }

    private static String createTodo(String[] details) {
        String taskName = details[0];
        Task td = new ToDo(taskName);
        tL.addTask(td);
        storageFile.storeTL(tL);
        return formatTaskMessage(td, TODO_ADD_MESSAGE);
    }

    private static String createDeadline(String[] details) {
        String taskName = details[0];
        Task dlTask = new Deadline(taskName, LocalDateTime.parse(details[1]
            .replace("/by", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        tL.addTask(dlTask);
        storageFile.storeTL(tL);
        return formatTaskMessage(dlTask, DEADLINE_ADD_MESSAGE);
    }

    private static String createEvent(String[] details) {
        String taskName = details[0];
        Task evTask = new Event(taskName, LocalDateTime.parse(details[1]
            .replace("/from", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            LocalDateTime.parse(details[2]
            .replace("/to", "").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        tL.addTask(evTask);
        storageFile.storeTL(tL);
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
        storageFile.storeTL(tL);
        return resultString;
    }

    private static String findTask(String[] details) {
        String searchTerm = details[0].toLowerCase();
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
        case "help":
            resultString = HELP_HELP_MESSAGE;
            break;
        case "bye":
            resultString = BYE_HELP_MESSAGE;
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
            + DELETE_HELP_MESSAGE + "\n"
            + HELP_HELP_MESSAGE + "\n"
            + BYE_HELP_MESSAGE;
    }
}
